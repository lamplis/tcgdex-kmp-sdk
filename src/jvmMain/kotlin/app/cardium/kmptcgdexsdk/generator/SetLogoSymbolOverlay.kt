package app.cardium.kmptcgdexsdk.generator

import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val POKEPEDIA_SET_ASSET_PREFIX = "https://www.pokepedia.fr/"
private const val TCGDEX_CDN_PREFIX = "https://assets.tcgdex.net/"

@Serializable
internal data class SetLogoSymbolEntry(
    val logoUrl: String? = null,
    val symbolUrl: String? = null,
)

internal fun isAllowedSetAssetUrl(url: String?): Boolean {
    val trimmed = url?.trim().orEmpty()
    if (trimmed.isEmpty()) return false
    return trimmed.startsWith(POKEPEDIA_SET_ASSET_PREFIX) || trimmed.startsWith(TCGDEX_CDN_PREFIX)
}

internal fun resolveSetAssetUrl(
    compiledUrl: String?,
    overlayUrl: String?,
): String? {
    val compiled = compiledUrl?.trim()?.takeIf { it.isNotEmpty() }
    if (compiled != null) return compiled
    val overlay = overlayUrl?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    return overlay.takeIf(::isAllowedSetAssetUrl)
}

internal fun loadSetLogoSymbolOverlay(
    path: String?,
    json: Json,
): Map<String, SetLogoSymbolEntry> {
    if (path.isNullOrBlank()) {
        println("[Tcgdex][i] Set logo overlay not provided, skipping")
        return emptyMap()
    }
    val file = File(path)
    if (!file.exists()) {
        println("[Tcgdex][!] Set logo overlay not found: $path")
        return emptyMap()
    }
    return runCatching {
        val loaded = json.decodeFromString<Map<String, SetLogoSymbolEntry>>(file.readText())
        println("[Tcgdex] Loaded ${loaded.size} set logo overlay entries from $path")
        loaded
    }.getOrElse { error ->
        println("[Tcgdex][!] Failed to load set logo overlay: ${error.message}")
        emptyMap()
    }
}
