package app.cardium.tcgdex.sdk.util

import app.cardium.tcgdex.sdk.model.Card

private val qualitySuffixRegex = Regex("/(low|high)\\.png$", RegexOption.IGNORE_CASE)

fun selectBaseImageUrl(url: String?): String? {
    val trimmed = url?.trim().orEmpty()
    if (trimmed.isEmpty()) return null
    val withoutQuality = trimmed.replace(qualitySuffixRegex, "")
    return if (withoutQuality.endsWith(".png", ignoreCase = true)) {
        withoutQuality.removeSuffix(".png").removeSuffix(".PNG")
    } else {
        withoutQuality
    }
}

private val directImageExtensions = setOf("jpg", "jpeg", "png", "webp", "avif")

/**
 * A finished picture file on a host other than the TCGdex CDN is already a
 * thumbnail and a full image. Clients must not append /low.png or /high.png.
 */
internal fun isDirectImageFile(url: String): Boolean {
    val trimmed = url.trim()
    val schemeEnd = trimmed.indexOf("://")
    if (schemeEnd < 0) return false
    val hostStart = schemeEnd + 3
    val hostEnd = trimmed.indexOf('/', hostStart).let { if (it < 0) trimmed.length else it }
    val host = trimmed.substring(hostStart, hostEnd).substringBefore(':').lowercase()
    if (host == "assets.tcgdex.net") return false
    val path = trimmed.substring(hostEnd).substringBefore('?').substringBefore('#')
    val extension = path.substringAfterLast('.', "").lowercase()
    return extension in directImageExtensions
}

fun toThumbnailUrl(baseUrl: String?): String? {
    val trimmed = baseUrl?.trim().orEmpty()
    if (trimmed.isNotEmpty() && isDirectImageFile(trimmed)) return trimmed
    val base = selectBaseImageUrl(baseUrl) ?: return null
    return "$base/low.png"
}

fun toHighQualityUrl(baseUrl: String?): String? {
    val trimmed = baseUrl?.trim().orEmpty()
    if (trimmed.isNotEmpty() && isDirectImageFile(trimmed)) return trimmed
    val base = selectBaseImageUrl(baseUrl) ?: return null
    return "$base/high.png"
}

fun Card.thumbnailImageUrl(): String? = toThumbnailUrl(this.imageUrl)

fun Card.highQualityImageUrl(): String? = toHighQualityUrl(this.imageUrl)

