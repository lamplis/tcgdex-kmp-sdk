package app.cardium.tcgdex.sdk.model

/**
 * Central predicate utilities for identifying Pokémon Pocket (TCG Pocket) cards.
 * Rules:
 * - Primary: series id equals "tcgp" (case-insensitive)
 * - Fallback: reference/number matches known Pocket-style pattern (e.g., "P-A-071/000")
 */
object PocketFilter {
    private const val POCKET_SERIE_ID: String = "tcgp"

    // Example formats:
    //  - P-A-071/000 or P-A-009 (Pocket promo-like)
    //  - A1-094, A2b-082 (Pocket set numbering)
    private val pocketIdRegex: Regex = Regex("(?i)^(p-[a-z]-\\d{3}(?:/\\d{3})?|a\\d+[a-z]?-[0-9]{3})$")
    private val pocketSetIdRegex: Regex = Regex("(?i)^a\\d+[a-z]?$")
    private val pocketRefRegex: Regex = Regex("(?i)^p-[a-z]-\\d{3}(?:/\\d{3})?$")

    /** Returns true if the provided inputs identify a Pocket card. */
    fun isPocket(seriesId: String?, numberOrRef: String?): Boolean {
        if (seriesId?.equals(POCKET_SERIE_ID, ignoreCase = true) == true) return true
        val ref = numberOrRef?.trim()
        if (ref != null && pocketRefRegex.matches(ref)) return true
        return false
    }

    /** Returns true if the given card id looks like a Pocket id. */
    fun isPocketId(cardId: String?): Boolean = cardId?.let { pocketIdRegex.matches(it.trim()) } == true

    /** Returns true if the given set id looks like a Pocket set id (e.g., A1, A2b). */
    fun isPocketSetId(setId: String?): Boolean = setId?.let { pocketSetIdRegex.matches(it.trim()) } == true

    /** Returns true if the given set belongs to the Pocket series. */
    fun isPocket(set: TcgdxSet?): Boolean = isPocket(set?.serie?.id, null)

    /** Returns true if the given card is a Pocket card. */
    fun isPocket(card: TcgdxCard): Boolean =
        isPocket(card.set?.serie?.id, card.number) || isPocketId(card.id) || isPocketSetId(card.set?.id)
}


