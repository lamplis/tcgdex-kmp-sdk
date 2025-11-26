package app.cardium.tcgdex.sdk.embedded

/**
 * Backward-compatible stub for the previous embedded catalog JSON accessors.
 * The offline SQLite database now acts as the single source of truth, so
 * these helpers return null.
 */
object EmbeddedCatalogData {
    fun seriesJson(language: String): String? = null
    fun setsJson(language: String): String? = null
    fun illustratorsIndexJson(): String? = null
    fun illustratorArtistFiles(): List<String> = emptyList()
    fun illustratorCollectionJson(fileName: String): String? = null
}

