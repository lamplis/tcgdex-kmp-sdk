package app.cardium.tcgdex.sdk.model

/**
 * A TCG series (e.g., "Scarlet & Violet", "Sword & Shield").
 *
 * Series group related card sets together. Each series has localized names
 * stored per-language in the database.
 *
 * @property id Unique identifier (e.g., "sv", "swsh")
 * @property name Localized series name
 * @property language ISO language code this name is in
 * @property position Display order (lower = earlier in timeline)
 */
data class Serie(
    val id: String,
    val name: String,
    val language: String,
    val position: Int
)

/**
 * A TCG card set within a series (e.g., "Scarlet & Violet", "Paldea Evolved").
 *
 * Sets contain cards and have localized names and logo URLs.
 *
 * @property id Unique identifier (e.g., "sv01", "sv02")
 * @property serieId Parent series identifier
 * @property serieName Localized series name (denormalized for convenience)
 * @property name Localized set name
 * @property language ISO language code
 * @property logoUrl URL to the set logo image (language-specific)
 * @property symbolUrl URL to the set symbol image (language-agnostic)
 * @property cardCountOfficial Number of cards in the official set
 * @property cardCountTotal Total cards including secret rares
 * @property releaseDate Release date in YYYY-MM-DD format, or null if unknown
 */
data class CardSet(
    val id: String,
    val serieId: String,
    val serieName: String?,
    val name: String,
    val language: String,
    val logoUrl: String?,
    val symbolUrl: String?,
    val cardCountOfficial: Int,
    val cardCountTotal: Int,
    val releaseDate: String?
)

/**
 * A TCG card with full metadata.
 *
 * Cards have localized names and image URLs. The image URL includes the
 * language segment for localized card images.
 *
 * @property id Unique identifier (e.g., "sv01-001")
 * @property localId Card number within the set (e.g., "001", "TG01")
 * @property setId Parent set identifier
 * @property setName Localized set name (denormalized)
 * @property setLanguage Language of the set data
 * @property serieId Parent series identifier
 * @property serieName Localized series name (denormalized)
 * @property name Localized card name
 * @property imageUrl Base URL to the card image (language-specific, without quality suffix)
 * @property thumbnailUrl Low-resolution image URL (imageUrl + /low.png)
 * @property highQualityUrl High-resolution image URL (imageUrl + /high.png)
 * @property fallbackImageUrl Direct URL to a non-TCGdex fallback asset (e.g., Poképedia PNG)
 * @property fallbackImageSource Identifier for the fallback provider ("pokepedia", etc.)
 * @property rarityId Rarity slug ID, or null if unknown
 * @property rarityName Original rarity name, or null
 * @property illustratorId Illustrator slug ID, or null
 * @property illustratorName Original illustrator name, or null
 * @property pokemonDexId National Pokédex number, or null for non-Pokémon cards
 * @property category Card category (e.g., "Pokemon", "Trainer", "Energy")
 * @property types List of Pokémon types (e.g., ["Fire", "Water"])
 * @property supertype Card supertype
 * @property regulationMark Regulation mark letter (e.g., "G", "H")
 * @property reference Display reference string (e.g., "001/198")
 * @property setOfficialCardCount Official card count of the parent set
 * @property setReleaseDate Release date of the parent set
 * @property priceCardmarketTrend Cardmarket trend price in EUR (embedded at build time)
 * @property priceCardmarketAvg Cardmarket average sell price in EUR
 * @property priceCardmarketLow Cardmarket low price in EUR
 * @property priceUpdatedIso ISO 8601 timestamp when price was fetched
 * @property priceUnit Currency unit (always "EUR" for Cardmarket)
 * @property isCameo True when this row comes from cameoDexIds linkage in card_pokemon
 */
data class Card(
    val id: String,
    val localId: String,
    val setId: String,
    val setName: String,
    val setLanguage: String,
    val originLanguage: String?,
    val serieId: String,
    val serieName: String?,
    val name: String,
    val imageUrl: String?,
    val thumbnailUrl: String?,
    val highQualityUrl: String?,
    val fallbackImageUrl: String?,
    val fallbackImageSource: String?,
    val rarityId: String?,
    val rarityName: String?,
    val illustratorId: String?,
    val illustratorName: String?,
    val pokemonDexId: Int?,
    val category: String?,
    val types: List<String>,
    val supertype: String?,
    val regulationMark: String?,
    // Printed PV/HP for Pokemon cards; null for trainers/energies or unknown
    val hp: Int? = null,
    val reference: String,
    val setOfficialCardCount: Int,
    val setReleaseDate: String?,
    // Embedded Cardmarket EUR pricing snapshot (generated at build time)
    val priceCardmarketTrend: Double? = null,
    val priceCardmarketAvg: Double? = null,
    val priceCardmarketLow: Double? = null,
    val priceUpdatedIso: String? = null,
    val priceUnit: String? = null,
    val isCameo: Boolean = false,
) {
    /**
     * Returns the best available price in EUR.
     * Priority: trend (most accurate) -> average sell -> low price.
     */
    val bestPrice: Double?
        get() = priceCardmarketTrend ?: priceCardmarketAvg ?: priceCardmarketLow
}

/**
 * Detailed Cardmarket pricing row for a card.
 *
 * This model mirrors the `card_prices` table. One row exists per
 * (variant, priceLanguage, sellerCountry, condition). The database stores the
 * raw Cardmarket data verbatim (no collapse across conditions, no fallback
 * between tiers); callers are responsible for picking the condition to display.
 *
 * @property condition Cardmarket condition grade (e.g. "NM", "MT", "EX", "GD",
 *   "LP", "PL", "PO"). Empty string means the row is the GLOBAL price-guide
 *   baseline, which is condition-agnostic.
 */
data class CardPrice(
    val cardId: String,
    val cardLanguage: String,
    val variant: String,
    val priceLanguage: String,
    val sellerCountry: String,
    val condition: String,
    val currency: String,
    val minPrice: Double?,
    val avgPrice: Double?,
    val medianPrice: Double?,
    val maxPrice: Double?,
    val recommendedPrice: Double?,
    val availableCount: Long?,
    val productId: Long?,
)

/**
 * Recognition hash payload for card-matching workflows.
 *
 * One row exists per (card, language, lighting, rotation) tuple and stores the
 * image source metadata together with 256-bit dHash/pHash strings.
 */
data class CardRecognitionHash(
    val cardId: String,
    val language: String,
    val imageSource: String,
    val imageUrl: String,
    val lighting: String,
    val rotation: Int,
    val dhash: String,
    val phash: String,
)

/**
 * An illustrator (card artist).
 *
 * Illustrators are stored with their original names (language-agnostic).
 *
 * @property id Slug identifier generated from the name
 * @property name Original artist name
 */
data class Illustrator(
    val id: String,
    val name: String
)

/**
 * An illustrator with their card count for a specific language.
 *
 * @property id Slug identifier
 * @property name Original artist name
 * @property cardCount Number of cards by this illustrator in the specified language
 */
data class IllustratorWithCount(
    val id: String,
    val name: String,
    val cardCount: Int
)

/**
 * Lightweight card row for illustrator count aggregation.
 *
 * Keeps only fields required to resolve BasicSet/MasterSet virtual variants
 * without loading full card payloads per illustrator.
 */
data class IllustratorCardIdEntry(
    val cardId: String,
    val illustratorId: String,
    val rarityId: String?,
    val setReleaseDate: String?,
    val setId: String?,
    val localId: String?,
    val category: String?,
)

/**
 * A card rarity classification.
 *
 * Rarities are stored with slugified IDs for consistency.
 *
 * @property id Slug identifier (e.g., "common", "rare-holo")
 * @property name Original rarity name (e.g., "Common", "Rare Holo")
 */
data class Rarity(
    val id: String,
    val name: String
)

/**
 * A Pokémon entry in the Pokédex index.
 *
 * Aggregates card counts for a specific Pokémon across all sets.
 *
 * @property dexId National Pokédex number
 * @property name Representative Pokémon name (from the first card found)
 * @property cardCount Total number of cards featuring this Pokémon
 */
data class PokemonDexEntry(
    val dexId: Int,
    val name: String,
    val cardCount: Int
)

/**
 * Per-set card count for a specific Pokémon.
 *
 * Used for grouping cards by set in the Pokémon gallery view.
 *
 * @property setId Set identifier
 * @property setName Localized set name
 * @property releaseDate Set release date (YYYY-MM-DD)
 * @property serieId Parent series identifier
 * @property serieName Localized series name
 * @property logoUrl Set logo URL
 * @property cardCount Number of cards for this Pokémon in this set
 */
data class PokemonSetCardCount(
    val setId: String,
    val setName: String,
    val releaseDate: String?,
    val serieId: String,
    val serieName: String?,
    val logoUrl: String?,
    val symbolUrl: String?,
    val cardCount: Int
)

/**
 * Aggregated rarity data per series.
 *
 * Used by the rarity debug screen to display rarity counts without loading all cards.
 * Each row represents a unique (series, rarity) combination with a count and sample card.
 *
 * @property seriesId Series identifier
 * @property seriesName Localized series name
 * @property seriesPosition Series display order (higher = newer)
 * @property rarityId Rarity slug ID, or null for unknown rarities
 * @property rarityName Original rarity name, or null
 * @property cardCount Number of cards with this rarity in this series
 * @property sampleCardId ID of a sample card (MIN cardId) for preview on tap
 */
data class RarityAggregate(
    val seriesId: String,
    val seriesName: String,
    val seriesPosition: Int,
    val rarityId: String?,
    val rarityName: String?,
    val cardCount: Int,
    val sampleCardId: String,
)

/**
 * Aggregated rarity data grouped by series AND set.
 * Each row represents a unique (series, set, rarity) combination.
 *
 * @property seriesId Series identifier
 * @property seriesName Localized series name
 * @property seriesPosition Series display order (higher = newer)
 * @property setId Set identifier
 * @property setName Localized set name
 * @property setReleaseDate Set release date (ISO format)
 * @property rarityId Rarity slug ID, or null for unknown rarities
 * @property rarityName Original rarity name, or null
 * @property cardCount Number of cards with this rarity in this set
 */
data class RarityAggregateBySet(
    val seriesId: String,
    val seriesName: String,
    val seriesPosition: Int,
    val setId: String,
    val setName: String,
    val setReleaseDate: String?,
    val rarityId: String?,
    val rarityName: String?,
    val cardCount: Int,
)
