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

    private val HOLO_V_SLUGS =
        setOf(
            "holo-rare-v",
            "holo-rare-vmax",
            "holo-rare-vstar",
            "rare-holo-v",
            "rare-holo-vmax",
            "rare-holo-vstar",
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
        val matched = slugForName(cardName)
        if (matched != null && id in HOLO_V_SLUGS) return matched
        if (id !in ULTRA_SLUGS) return raritySlug
        return matched ?: raritySlug
    }

    @Suppress("UNUSED_PARAMETER")
    fun persistRarityId(
        raritySlug: String,
        cardName: String?,
        setId: String,
    ): String = canonicalize(raritySlug, cardName)

    fun displayName(canonicalSlug: String): String? =
        when (canonicalSlug) {
            "holo-rare-vmax" -> "Holo Rare VMAX"
            "holo-rare-vstar" -> "Holo Rare VSTAR"
            "holo-rare-v" -> "Holo Rare V"
            "black-star-promo" -> "Black Star Promo"
            else -> null
        }

    private fun slugForName(cardName: String?): String? {
        val name = cardName.orEmpty()
        return when {
            VMAX_SUFFIX.containsMatchIn(name) -> "holo-rare-vmax"
            VSTAR_SUFFIX.containsMatchIn(name) -> "holo-rare-vstar"
            V_SUFFIX.containsMatchIn(name) -> "holo-rare-v"
            else -> null
        }
    }
}
