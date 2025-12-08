package app.cardium.tcgdex.sdk

import app.cardium.tcgdex.sdk.model.Card
import app.cardium.tcgdex.sdk.model.CardSet
import app.cardium.tcgdex.sdk.model.Illustrator
import app.cardium.tcgdex.sdk.model.IllustratorWithCount
import app.cardium.tcgdex.sdk.model.PokemonDexEntry
import app.cardium.tcgdex.sdk.model.PokemonSetCardCount
import app.cardium.tcgdex.sdk.model.Rarity
import app.cardium.tcgdex.sdk.model.Serie

/**
 * Repository interface for accessing the offline TCGdex database.
 *
 * All methods that return localized data (series, sets, cards) require a `language` parameter.
 * The language should match one of the languages included in the database at build time.
 *
 * ## Thread Safety
 * All methods are suspend functions and safe to call from any coroutine context.
 * The underlying SQLDelight queries handle thread synchronization.
 *
 * ## Offline Guarantee
 * All data is served from the local SQLite database. No network calls are made.
 * Image URLs point to TCGdex CDN but images are loaded separately by the app.
 *
 * @see DefaultTcgdexRepository for the implementation
 */
interface TcgdexRepository {

    // =========================================================================
    // Series Queries
    // =========================================================================

    /**
     * Returns all series for the specified language, ordered by position.
     *
     * @param language ISO language code (e.g., "en", "fr")
     * @return List of series, empty if language not found in database
     */
    suspend fun getAllSeries(language: String): List<Serie>

    /**
     * Returns a specific series by ID and language.
     *
     * @param serieId The series identifier (e.g., "sv", "swsh")
     * @param language ISO language code
     * @return The series if found, null otherwise
     */
    suspend fun getSerieById(serieId: String, language: String): Serie?

    // =========================================================================
    // Set Queries
    // =========================================================================

    /**
     * Returns all sets for the specified language, ordered by release date (newest first).
     *
     * @param language ISO language code
     * @return List of sets with their series information
     */
    suspend fun getAllSets(language: String): List<CardSet>

    /**
     * Returns all sets belonging to a specific series.
     *
     * @param serieId The series identifier
     * @param language ISO language code
     * @return List of sets in the series, ordered by release date
     */
    suspend fun getSetsForSerie(serieId: String, language: String): List<CardSet>

    /**
     * Returns a specific set by ID and language.
     *
     * @param setId The set identifier (e.g., "sv01", "swsh1")
     * @param language ISO language code
     * @return The set if found, null otherwise
     */
    suspend fun getSetById(setId: String, language: String): CardSet?

    // =========================================================================
    // Card Queries
    // =========================================================================

    /**
     * Returns all cards in a specific set, ordered by card number.
     *
     * @param setId The set identifier
     * @param language ISO language code
     * @return List of cards with full metadata including set and series info
     */
    suspend fun getCardsForSet(setId: String, language: String): List<Card>

    /**
     * Returns all cards by a specific illustrator across all sets.
     *
     * @param illustratorId The illustrator's slug ID
     * @param language ISO language code
     * @return List of cards ordered by set release date (newest first)
     */
    suspend fun getCardsForIllustrator(illustratorId: String, language: String): List<Card>

    /**
     * Returns all cards with a specific rarity.
     *
     * @param rarityId The rarity slug ID
     * @param language ISO language code
     * @return List of cards ordered by set release date (newest first)
     */
    suspend fun getCardsForRarity(rarityId: String, language: String): List<Card>

    /**
     * Returns all cards featuring a specific Pokémon by Pokédex ID.
     *
     * @param dexId The national Pokédex number
     * @param language ISO language code
     * @return List of cards ordered by set release date (newest first)
     */
    suspend fun getCardsForPokemonDexId(dexId: Int, language: String): List<Card>

    /**
     * Returns a specific card by ID and language.
     *
     * @param cardId The card identifier (e.g., "sv01-001")
     * @param language ISO language code
     * @return The card with full metadata if found, null otherwise
     */
    suspend fun getCardById(cardId: String, language: String): Card?

    /**
     * Searches for cards by name (case-insensitive partial match).
     *
     * @param query Search query (minimum 2 characters recommended)
     * @param language ISO language code
     * @param limit Maximum number of results to return
     * @param offset Number of results to skip (for pagination)
     * @return List of matching cards ordered by name
     */
    suspend fun searchCardsByName(query: String, language: String, limit: Int, offset: Int): List<Card>

    /**
     * Counts the total number of cards matching a name search.
     *
     * @param query Search query
     * @param language ISO language code
     * @return Total count of matching cards
     */
    suspend fun countCardsByName(query: String, language: String): Long

    // =========================================================================
    // Local ID / Reference Search Queries
    // =========================================================================

    /**
     * Searches for cards by local ID (e.g., "001", "TG09").
     * Excludes TCGP (TCG Pocket) cards.
     *
     * @param localId The card's local ID within its set
     * @param language ISO language code
     * @param limit Maximum number of results
     * @return List of cards matching the local ID, ordered by release date (newest first)
     */
    suspend fun searchCardsByLocalId(localId: String, language: String, limit: Int): List<Card>

    /**
     * Searches for a card by local ID within a specific set.
     * Excludes TCGP (TCG Pocket) cards.
     *
     * @param localId The card's local ID within its set
     * @param setId The set identifier
     * @param language ISO language code
     * @return List of cards (usually 0 or 1) matching the local ID in the set
     */
    suspend fun searchCardsByLocalIdInSet(localId: String, setId: String, language: String): List<Card>

    /**
     * Searches for cards by local ID and official card count (for "X/Y" format).
     * Excludes TCGP (TCG Pocket) cards.
     *
     * @param localId The card's local ID (X in "X/Y")
     * @param cardCount The set's official card count (Y in "X/Y")
     * @param language ISO language code
     * @param limit Maximum number of results
     * @return List of cards matching the criteria
     */
    suspend fun searchCardsByLocalIdAndCardCount(localId: String, cardCount: Int, language: String, limit: Int): List<Card>

    /**
     * Searches for sets by name (partial match).
     * Excludes TCGP (TCG Pocket) sets.
     *
     * @param query Search query (e.g., "151" to match "Pokémon 151")
     * @param language ISO language code
     * @param limit Maximum number of results
     * @return List of sets matching the name query
     */
    suspend fun searchSetsByName(query: String, language: String, limit: Int): List<CardSet>

    // =========================================================================
    // Illustrator & Rarity Queries (Language-Agnostic)
    // =========================================================================

    /**
     * Returns all illustrators in the database.
     * Illustrators are language-agnostic (original artist names).
     *
     * @return List of illustrators ordered by name
     */
    suspend fun getAllIllustrators(): List<Illustrator>

    /**
     * Returns all illustrators with their card counts for a specific language.
     *
     * @param language ISO language code
     * @return List of illustrators with counts, ordered by count (highest first)
     */
    suspend fun getIllustratorsWithCounts(language: String): List<IllustratorWithCount>

    /**
     * Returns all rarities in the database.
     * Rarities are language-agnostic (slugified names).
     *
     * @return List of rarities ordered by name
     */
    suspend fun getAllRarities(): List<Rarity>

    // =========================================================================
    // Pokémon Dex Queries
    // =========================================================================

    /**
     * Returns all unique Pokémon dex IDs with card counts.
     *
     * Used for the Pokédex collection screen to show all Pokémon with cards.
     * Excludes TCGP (TCG Pocket) cards.
     *
     * @param language ISO language code
     * @return List of Pokémon entries ordered by dex ID
     */
    suspend fun getAllPokemonDexEntries(language: String): List<PokemonDexEntry>

    /**
     * Returns card counts per set for a specific Pokémon.
     *
     * Used for grouping cards by set in the Pokémon gallery view.
     * Excludes TCGP (TCG Pocket) cards.
     *
     * @param dexId National Pokédex number
     * @param language ISO language code
     * @return List of set card counts ordered by release date (newest first)
     */
    suspend fun getCardCountsPerSetForPokemon(dexId: Int, language: String): List<PokemonSetCardCount>

    /**
     * Returns all cards for a Pokémon in a specific set.
     *
     * Used for rendering the card grid within a set section.
     * Excludes TCGP (TCG Pocket) cards.
     *
     * @param dexId National Pokédex number
     * @param setId Set identifier
     * @param language ISO language code
     * @return List of cards ordered by card number
     */
    suspend fun getCardsForPokemonInSet(dexId: Int, setId: String, language: String): List<Card>

    // =========================================================================
    // Utility Queries
    // =========================================================================

    /**
     * Returns all languages available in the database.
     *
     * @return List of ISO language codes (e.g., ["en", "fr"])
     */
    suspend fun getAvailableLanguages(): List<String>
}
