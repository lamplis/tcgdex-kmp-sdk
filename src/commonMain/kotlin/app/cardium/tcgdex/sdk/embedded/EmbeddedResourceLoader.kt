package app.cardium.tcgdex.sdk.embedded

/**
 * Expect/actual resource loader for reading embedded JSON files across platforms.
 *
 * Paths are Compose KMP resource paths, e.g.:
 * - "composeResources/tcgdex/en/series.json"
 * - "composeResources/tcgdex/en/sets/sv01.json"
 * - "composeResources/illustrators/global/illustrators-index.json"
 */
@Suppress("EXPECTED_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object EmbeddedResourceLoader {
    /**
     * Reads a resource file as text. Returns null if not found or on error.
     */
    fun readText(path: String): String?
}


