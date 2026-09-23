package app.cardium.kmptcgdexsdk.generator

/**
 * Rewrites stored rarity ids after V-line persist so the catalog uses one slug
 * per official symbol from [docs/RARITY_NAMING.md].
 */
internal object StoredRaritySlug {
    private val ALIASES =
        mapOf(
            "rare-holo-lv-x" to "holo-rare-lv-x",
            "rare-holo-lvx" to "holo-rare-lv-x",
            "full-art-trainer" to "ultra-rare",
            "black-star-promo" to "promo",
            "rare-holo-gx" to "holo-rare-gx",
            "rare-holo-ex" to "holo-rare-ex",
            "rare-rainbow" to "rainbow-rare",
            "rare-shiny-gx" to "shiny-rare-gx",
        )

    fun canonicalize(
        slug: String,
        cardId: String,
        breakCardIds: Set<String>,
        shiningCardIds: Set<String>,
    ): String {
        if (cardId in breakCardIds) return "rare-break"
        if (cardId in shiningCardIds) return "rare-shining"
        return ALIASES[slug.lowercase()] ?: slug
    }

    fun displayName(canonicalSlug: String): String? =
        when (canonicalSlug) {
            "holo-rare-lv-x" -> "Rare Holo LV.X"
            "ultra-rare" -> "Ultra Rare"
            "rare-break" -> "Rare BREAK"
            "rare-shining" -> "Rare Shining"
            else -> VLineRaritySlug.displayName(canonicalSlug)
        }
}
