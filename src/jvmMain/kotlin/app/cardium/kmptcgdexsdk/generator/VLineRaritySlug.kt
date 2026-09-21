package app.cardium.kmptcgdexsdk.generator

/**
 * Parallel of composeApp [app.cardium.domain.cards.VLineRarity] for the database generator.
 * The SDK must not import composeApp.
 */
internal object VLineRaritySlug {
    private val SECRET_SLUGS =
        setOf(
            "secret-rare",
            "rare-secret",
            "trainer-gallery-secret-rare",
            "galarian-gallery-secret-rare",
        )

    private val ULTRA_SLUGS =
        setOf(
            "ultra-rare",
            "rare-ultra",
            "trainer-gallery-ultra-rare",
            "galarian-gallery-ultra-rare",
        )

    private val VMAX_SUFFIX = Regex("""(?:^|[\s-])VMAX\s*$""", RegexOption.IGNORE_CASE)
    private val VSTAR_SUFFIX = Regex("""(?:^|[\s-])VSTAR\s*$""", RegexOption.IGNORE_CASE)
    private val V_SUFFIX = Regex("""(?:^|[\s-])V\s*$""", RegexOption.IGNORE_CASE)

    fun canonicalize(
        raritySlug: String,
        cardName: String?,
    ): String {
        val id = raritySlug.lowercase()
        if (id in SECRET_SLUGS) return raritySlug
        if (id !in ULTRA_SLUGS) return raritySlug
        return when {
            VMAX_SUFFIX.containsMatchIn(cardName.orEmpty()) -> "holo-rare-vmax"
            VSTAR_SUFFIX.containsMatchIn(cardName.orEmpty()) -> "holo-rare-vstar"
            V_SUFFIX.containsMatchIn(cardName.orEmpty()) -> "holo-rare-v"
            else -> raritySlug
        }
    }

    fun displayName(canonicalSlug: String): String? =
        when (canonicalSlug) {
            "holo-rare-vmax" -> "Holo Rare VMAX"
            "holo-rare-vstar" -> "Holo Rare VSTAR"
            "holo-rare-v" -> "Holo Rare V"
            else -> null
        }
}
