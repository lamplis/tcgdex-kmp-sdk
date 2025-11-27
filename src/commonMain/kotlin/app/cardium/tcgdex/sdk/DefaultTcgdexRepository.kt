package app.cardium.tcgdex.sdk

import app.cardium.tcgdex.db.Card_with_set
import app.cardium.tcgdex.db.CountCardsPerIllustrator
import app.cardium.tcgdex.db.GetSetWithSerieName
import app.cardium.tcgdex.db.Illustrators
import app.cardium.tcgdex.db.Rarities
import app.cardium.tcgdex.db.Series
import app.cardium.tcgdex.db.Sets
import app.cardium.tcgdex.db.TcgdexDatabase
import app.cardium.tcgdex.sdk.model.Card
import app.cardium.tcgdex.sdk.model.CardSet
import app.cardium.tcgdex.sdk.model.Illustrator
import app.cardium.tcgdex.sdk.model.IllustratorWithCount
import app.cardium.tcgdex.sdk.model.Rarity
import app.cardium.tcgdex.sdk.model.Serie
import app.cardium.tcgdex.sdk.util.selectBaseImageUrl
import app.cardium.tcgdex.sdk.util.toHighQualityUrl
import app.cardium.tcgdex.sdk.util.toThumbnailUrl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Default implementation of [TcgdexRepository] backed by SQLDelight.
 *
 * All queries are executed on the provided [dispatcher] (defaults to [Dispatchers.Default])
 * to avoid blocking the main thread.
 *
 * ## Thread Safety
 * SQLDelight handles connection pooling and thread synchronization internally.
 * This class is safe to use from multiple coroutines concurrently.
 *
 * ## Performance Notes
 * - Queries use indexed columns for efficient lookups
 * - The `card_with_set` view pre-joins related tables to minimize query overhead
 * - Results are mapped to domain models on the query thread
 *
 * @param database The SQLDelight database instance
 * @param dispatcher Coroutine dispatcher for query execution
 */
class DefaultTcgdexRepository(
    database: TcgdexDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : TcgdexRepository {

    private val queries = database.tcgdexQueries

    // =========================================================================
    // Series Queries
    // =========================================================================

    override suspend fun getAllSeries(language: String): List<Serie> = withContext(dispatcher) {
        val result = queries.getAllSeriesByLanguage(language).executeAsList()
        println("[Tcgdex][i] getAllSeries($language) returned ${result.size} rows")
        result.map { it.toModel() }
    }

    override suspend fun getSerieById(serieId: String, language: String): Serie? = withContext(dispatcher) {
        queries.getSeriesById(serieId, language).executeAsOneOrNull()?.toModel()
    }

    // =========================================================================
    // Set Queries
    // =========================================================================

    override suspend fun getAllSets(language: String): List<CardSet> = withContext(dispatcher) {
        // Debug: check database state
        try {
            val availableLanguages = queries.getAvailableLanguages().executeAsList()
            println("[Tcgdex][i] Available languages in DB: $availableLanguages")
            
            // Debug: count sets per language
            val allSets = queries.countAllSets().executeAsOne()
            println("[Tcgdex][i] Total sets in DB: $allSets")
            
            // Debug: count sets for specific language using raw count
            val frCount = queries.countSetsByLanguage("fr").executeAsOne()
            val enCount = queries.countSetsByLanguage("en").executeAsOne()
            println("[Tcgdex][i] Sets by language: fr=$frCount, en=$enCount")
        } catch (e: Exception) {
            println("[Tcgdex][x] Failed to get DB stats: ${e.message}")
            e.printStackTrace()
        }
        
        // Use the query that includes serie_name for complete CardSet data
        val result = queries.getAllSetsByLanguage(language).executeAsList()
        println("[Tcgdex][i] getAllSets($language) returned ${result.size} rows")
        result.map { set ->
            // Fetch serie name separately for the basic Sets result
            val serieName = queries.getSeriesById(set.serie_id, language)
                .executeAsOneOrNull()?.name
            set.toModel(serieName)
        }
    }

    override suspend fun getSetsForSerie(serieId: String, language: String): List<CardSet> =
        withContext(dispatcher) {
            val serieName = queries.getSeriesById(serieId, language)
                .executeAsOneOrNull()?.name
            queries.getSetsForSerie(serieId, language).executeAsList().map { it.toModel(serieName) }
        }

    override suspend fun getSetById(setId: String, language: String): CardSet? = withContext(dispatcher) {
        queries.getSetWithSerieName(setId, language).executeAsOneOrNull()?.toModel()
    }

    // =========================================================================
    // Card Queries
    // =========================================================================

    override suspend fun getCardsForSet(setId: String, language: String): List<Card> =
        withContext(dispatcher) {
            queries.getCardsForSet(setId, language).executeAsList().map { it.toModel() }
        }

    override suspend fun getCardsForIllustrator(illustratorId: String, language: String): List<Card> =
        withContext(dispatcher) {
            queries.getCardsForIllustrator(illustratorId, language).executeAsList().map { it.toModel() }
        }

    override suspend fun getCardsForRarity(rarityId: String, language: String): List<Card> =
        withContext(dispatcher) {
            queries.getCardsForRarity(rarityId, language).executeAsList().map { it.toModel() }
        }

    override suspend fun getCardsForPokemonDexId(dexId: Int, language: String): List<Card> =
        withContext(dispatcher) {
            queries.getCardsForDexId(dexId.toLong(), language).executeAsList().map { it.toModel() }
        }

    override suspend fun getCardById(cardId: String, language: String): Card? = withContext(dispatcher) {
        queries.getCardById(cardId, language).executeAsOneOrNull()?.toModel()
    }

    override suspend fun searchCardsByName(
        query: String,
        language: String,
        limit: Int,
        offset: Int
    ): List<Card> = withContext(dispatcher) {
        queries.searchCardsByName(language, query, limit.toLong(), offset.toLong())
            .executeAsList()
            .map { it.toModel() }
    }

    override suspend fun countCardsByName(query: String, language: String): Long = withContext(dispatcher) {
        queries.countCardsByName(language, query).executeAsOne()
    }

    // =========================================================================
    // Illustrator & Rarity Queries
    // =========================================================================

    override suspend fun getAllIllustrators(): List<Illustrator> = withContext(dispatcher) {
        val result = queries.getAllIllustrators().executeAsList()
        println("[Tcgdex][i] getAllIllustrators() returned ${result.size} rows")
        result.map { it.toModel() }
    }

    override suspend fun getIllustratorsWithCounts(language: String): List<IllustratorWithCount> =
        withContext(dispatcher) {
            queries.countCardsPerIllustrator(language).executeAsList().map { it.toModel() }
        }

    override suspend fun getAllRarities(): List<Rarity> = withContext(dispatcher) {
        queries.getAllRarities().executeAsList().map { it.toModel() }
    }

    // =========================================================================
    // Utility Queries
    // =========================================================================

    override suspend fun getAvailableLanguages(): List<String> = withContext(dispatcher) {
        queries.getAvailableLanguages().executeAsList()
    }
}

// =============================================================================
// Model Mappers
// =============================================================================

/**
 * Maps a SQLDelight Series row to a domain Serie model.
 */
private fun Series.toModel(): Serie = Serie(
    id = id,
    name = name,
    language = language,
    position = position.toInt()
)

/**
 * Maps a SQLDelight Sets row to a domain CardSet model.
 *
 * @param serieName Optional series name to include (fetched separately)
 */
private fun Sets.toModel(serieName: String? = null): CardSet = CardSet(
    id = id,
    serieId = serie_id,
    serieName = serieName,
    name = name,
    language = language,
    logoUrl = logo_url,
    symbolUrl = symbol_url,
    cardCountOfficial = card_count_official.toInt(),
    cardCountTotal = card_count_total.toInt(),
    releaseDate = release_date
)

/**
 * Maps a SQLDelight GetSetWithSerieName result to a domain CardSet model.
 * This query includes the serie_name from the join.
 */
private fun GetSetWithSerieName.toModel(): CardSet = CardSet(
    id = id,
    serieId = serie_id,
    serieName = serie_name,
    name = name,
    language = language,
    logoUrl = logo_url,
    symbolUrl = symbol_url,
    cardCountOfficial = card_count_official.toInt(),
    cardCountTotal = card_count_total.toInt(),
    releaseDate = release_date
)

/**
 * Maps a SQLDelight Card_with_set view row to a domain Card model.
 * The view pre-joins cards with sets, series, illustrators, and rarities.
 */
private fun Card_with_set.toModel(): Card {
    val baseImage = image_url?.let { selectBaseImageUrl(it) ?: it }
    return Card(
    id = id,
    localId = local_id,
    setId = set_id,
    setName = set_name,
    setLanguage = language,
    serieId = serie_id,
    serieName = serie_name,
    name = name,
        imageUrl = baseImage,
        thumbnailUrl = baseImage?.let { toThumbnailUrl(it) ?: "$it/low.png" },
        highQualityUrl = baseImage?.let { toHighQualityUrl(it) ?: "$it/high.png" },
    rarityId = rarity_id,
    rarityName = rarity_name,
    illustratorId = illustrator_id,
    illustratorName = illustrator_name,
    pokemonDexId = pokemon_dex_id?.toInt(),
    regulationMark = regulation_mark,
    category = category,
    types = parseTypes(types),
    supertype = supertype,
    reference = buildReference(local_id, set_card_count_official.toInt()),
    setOfficialCardCount = set_card_count_official.toInt(),
        setReleaseDate = set_release_date,
)
}

/**
 * Maps a SQLDelight Illustrators row to a domain Illustrator model.
 */
private fun Illustrators.toModel(): Illustrator = Illustrator(
    id = id,
    name = name
)

/**
 * Maps a SQLDelight CountCardsPerIllustrator result to a domain IllustratorWithCount model.
 */
private fun CountCardsPerIllustrator.toModel(): IllustratorWithCount = IllustratorWithCount(
    id = id,
    name = name,
    cardCount = card_count.toInt()
)

/**
 * Maps a SQLDelight Rarities row to a domain Rarity model.
 */
private fun Rarities.toModel(): Rarity = Rarity(
    id = id,
    name = name
)

// =============================================================================
// Helper Functions
// =============================================================================

/**
 * Parses a comma-separated types string into a list.
 * Example: "Fire,Water" -> ["Fire", "Water"]
 */
private fun parseTypes(raw: String?): List<String> =
    raw
        ?.split(',', '|')
        ?.mapNotNull { fragment ->
            val trimmed = fragment.trim()
            if (trimmed.isEmpty()) null else trimmed
        }
        ?: emptyList()

/**
 * Builds a display reference string (e.g., "001/198").
 *
 * @param localId The card's local ID within the set
 * @param officialCount The set's official card count
 */
private fun buildReference(localId: String, officialCount: Int): String =
    "$localId/$officialCount"
