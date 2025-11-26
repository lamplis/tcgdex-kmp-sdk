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

fun toThumbnailUrl(baseUrl: String?): String? {
    val base = selectBaseImageUrl(baseUrl) ?: return null
    return "$base/low.png"
}

fun toHighQualityUrl(baseUrl: String?): String? {
    val base = selectBaseImageUrl(baseUrl) ?: return null
    return "$base/high.png"
}

fun Card.thumbnailImageUrl(): String? = toThumbnailUrl(this.imageUrl)

fun Card.highQualityImageUrl(): String? = toHighQualityUrl(this.imageUrl)

