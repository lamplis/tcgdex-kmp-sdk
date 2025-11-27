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
    val rarityId: String?,
    val rarityName: String?,
    val illustratorId: String?,
    val illustratorName: String?,
    val pokemonDexId: Int?,
    val category: String?,
    val types: List<String>,
    val supertype: String?,
    val regulationMark: String?,
    val reference: String,
    val setOfficialCardCount: Int,
    val setReleaseDate: String?
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
    val cardCount: Int
)
