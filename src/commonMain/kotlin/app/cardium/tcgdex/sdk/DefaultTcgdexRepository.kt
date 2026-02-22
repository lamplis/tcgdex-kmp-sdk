package app.cardium.tcgdex.sdk

import app.cardium.tcgdex.db.Card_with_pokemon
import app.cardium.tcgdex.db.Card_with_set
import app.cardium.tcgdex.db.Card_prices
import app.cardium.tcgdex.db.CountCardsPerIllustrator
import app.cardium.tcgdex.db.GetSetWithSerieName
import app.cardium.tcgdex.db.Illustrators
import app.cardium.tcgdex.db.Rarities
import app.cardium.tcgdex.db.Series
import app.cardium.tcgdex.db.Sets
import app.cardium.tcgdex.db.TcgdexDatabase
import app.cardium.tcgdex.sdk.model.Card
import app.cardium.tcgdex.sdk.model.CardPrice
import app.cardium.tcgdex.sdk.model.CardSet
import app.cardium.tcgdex.sdk.model.Illustrator
import app.cardium.tcgdex.sdk.model.IllustratorWithCount
import app.cardium.tcgdex.sdk.model.PokemonDexEntry
import app.cardium.tcgdex.sdk.model.PokemonSetCardCount
import app.cardium.tcgdex.sdk.model.Rarity
import app.cardium.tcgdex.sdk.model.RarityAggregate
import app.cardium.tcgdex.sdk.model.RarityAggregateBySet
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

    override suspend fun getCardPrices(cardId: String, language: String): List<CardPrice> =
        withContext(dispatcher) {
            queries.getCardPrices(cardId, language).executeAsList().map { it.toModel() }
        }

    override suspend fun getRecommendedPrice(cardId: String, language: String, sellerCountry: String): Double? =
        withContext(dispatcher) {
            queries.getRecommendedPriceForCard(cardId, language, sellerCountry).executeAsOneOrNull()?.recommended_price
        }

    override suspend fun getAllSellerCountries(): List<String> =
        withContext(dispatcher) {
            queries.getAllSellerCountries().executeAsList().map { it.trim() }.distinct()
        }

    override suspend fun getCardsByIds(cardIds: Collection<String>, language: String): Map<String, Card> =
        withContext(dispatcher) {
            if (cardIds.isEmpty()) return@withContext emptyMap()

            // SQLite has a limit on the number of bound variables; chunk to stay safe.
            val result = LinkedHashMap<String, Card>(cardIds.size)
            cardIds.toList().chunked(500).forEach { chunk ->
                queries.getCardsByIds(chunk, language)
                    .executeAsList()
                    .forEach { row ->
                        val model = row.toModel()
                        result[model.id] = model
                    }
            }
            result
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
    // Local ID / Reference Search Queries
    // =========================================================================

    override suspend fun searchCardsByLocalId(localId: String, language: String, limit: Int): List<Card> =
        withContext(dispatcher) {
            queries.searchCardsByLocalId(language, localId, limit.toLong())
                .executeAsList()
                .map { it.toModel() }
        }

    override suspend fun searchCardsByLocalIdInSet(localId: String, setId: String, language: String): List<Card> =
        withContext(dispatcher) {
            queries.searchCardsByLocalIdInSet(language, setId, localId)
                .executeAsList()
                .map { it.toModel() }
        }

    override suspend fun searchCardsByLocalIdAndCardCount(
        localId: String,
        cardCount: Int,
        language: String,
        limit: Int
    ): List<Card> = withContext(dispatcher) {
        queries.searchCardsByLocalIdAndCardCount(language, localId, cardCount.toLong(), limit.toLong())
            .executeAsList()
            .map { it.toModel() }
    }

    override suspend fun searchSetsByName(query: String, language: String, limit: Int): List<CardSet> =
        withContext(dispatcher) {
            queries.searchSetsByName(language, query, limit.toLong())
                .executeAsList()
                .map { row ->
                    CardSet(
                        id = row.id,
                        serieId = row.serie_id,
                        serieName = row.serie_name,
                        name = row.name,
                        language = row.language,
                        logoUrl = row.logo_url,
                        symbolUrl = row.symbol_url,
                        cardCountOfficial = row.card_count_official.toInt(),
                        cardCountTotal = row.card_count_total.toInt(),
                        releaseDate = row.release_date
                    )
                }
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
    // Pokémon Dex Queries
    // =========================================================================

    override suspend fun getAllPokemonDexEntries(language: String): List<PokemonDexEntry> =
        withContext(dispatcher) {
            queries.getAllPokemonDexIds(language).executeAsList().map { row ->
                PokemonDexEntry(
                    dexId = row.dex_id!!.toInt(),
                    name = row.pokemon_name ?: "Unknown",
                    cardCount = row.card_count.toInt()
                )
            }
        }

    override suspend fun getAllCardIdsByPokemon(language: String): Map<Int, Set<String>> =
        withContext(dispatcher) {
            val result = mutableMapOf<Int, MutableSet<String>>()
            queries.getAllCardIdsByPokemon(language).executeAsList().forEach { row ->
                val dexId = row.dex_id?.toInt() ?: return@forEach
                val cardId = row.card_id ?: return@forEach
                result.getOrPut(dexId) { mutableSetOf() }.add(cardId)
            }
            result.mapValues { it.value.toSet() }
        }

    override suspend fun getCardCountsPerSetForPokemon(dexId: Int, language: String): List<PokemonSetCardCount> =
        withContext(dispatcher) {
            queries.getCardCountsPerSetForDexId(dexId.toLong(), language).executeAsList().map { row ->
                PokemonSetCardCount(
                    setId = row.set_id,
                    setName = row.set_name,
                    releaseDate = row.release_date,
                    serieId = row.serie_id,
                    serieName = row.serie_name,
                    logoUrl = row.logo_url,
                    symbolUrl = row.symbol_url,
                    cardCount = row.card_count.toInt()
                )
            }
        }

    override suspend fun getCardsForPokemonInSet(dexId: Int, setId: String, language: String): List<Card> =
        withContext(dispatcher) {
            queries.getCardsForDexIdInSet(dexId.toLong(), setId, language).executeAsList().map { it.toModel() }
        }

    override suspend fun getDexIdForCard(cardId: String, language: String): Int? =
        withContext(dispatcher) {
            queries.getDexIdForCard(cardId, language).executeAsOneOrNull()?.toInt()
        }

    override suspend fun getDexIdsForCards(cardIds: List<String>, language: String): Map<String, Int> =
        withContext(dispatcher) {
            if (cardIds.isEmpty()) return@withContext emptyMap()

            val rows = queries.getDexIdsForCards(cardIds, language).executeAsList()
            val result = mutableMapOf<String, Int>()

            // card_with_pokemon can contain multiple rows for a single card (multi-Pokémon cards).
            // Preserve legacy semantics from getDexIdForCard(): keep the first dex ID we see per card.
            for (row in rows) {
                val dex = row.pokemon_dex_id?.toInt() ?: continue
                if (!result.containsKey(row.id)) {
                    result[row.id] = dex
                }
            }
            result
        }

    // =========================================================================
    // Utility Queries
    // =========================================================================

    override suspend fun getAvailableLanguages(): List<String> = withContext(dispatcher) {
        queries.getAvailableLanguages().executeAsList()
    }

    // =========================================================================
    // Rarity Aggregation Queries
    // =========================================================================

    override suspend fun getRaritiesGroupedBySeries(language: String): List<RarityAggregate> =
        withContext(dispatcher) {
            val result = queries.getRaritiesGroupedBySeries(language).executeAsList()
            println("[Tcgdex][i] getRaritiesGroupedBySeries($language) returned ${result.size} rows")
            // Filter out any rows without a sample card (shouldn't happen, but SQL returns nullable)
            result.mapNotNull { row ->
                val sampleCardId = row.sample_card_id ?: return@mapNotNull null
                RarityAggregate(
                    seriesId = row.series_id,
                    seriesName = row.series_name,
                    seriesPosition = row.series_position.toInt(),
                    rarityId = row.rarity_id,
                    rarityName = row.rarity_name,
                    cardCount = row.card_count.toInt(),
                    sampleCardId = sampleCardId,
                )
            }
        }

    override suspend fun getRaritiesGroupedBySeriesAndSet(language: String): List<RarityAggregateBySet> =
        withContext(dispatcher) {
            val result = queries.getRaritiesGroupedBySeriesAndSet(language).executeAsList()
            println("[Tcgdex][i] getRaritiesGroupedBySeriesAndSet($language) returned ${result.size} rows")
            result.map { row ->
                RarityAggregateBySet(
                    seriesId = row.series_id,
                    seriesName = row.series_name,
                    seriesPosition = row.series_position.toInt(),
                    setId = row.set_id,
                    setName = row.set_name,
                    setReleaseDate = row.set_release_date,
                    rarityId = row.rarity_id,
                    rarityName = row.rarity_name,
                    cardCount = row.card_count.toInt(),
                )
            }
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
 * Note: This view does NOT include pokemon_dex_id (use Card_with_pokemon for Pokédex queries).
 * Includes embedded Cardmarket EUR pricing snapshot.
 */
private fun Card_with_set.toModel(): Card {
    val baseImage = image_url?.let { selectBaseImageUrl(it) ?: it }
    return Card(
        id = id,
        localId = local_id,
        setId = set_id,
        setName = set_name,
        setLanguage = language,
        originLanguage = origin_language,
        serieId = serie_id,
        serieName = serie_name,
        name = name,
        imageUrl = baseImage,
        thumbnailUrl = baseImage?.let { toThumbnailUrl(it) ?: "$it/low.png" },
        highQualityUrl = baseImage?.let { toHighQualityUrl(it) ?: "$it/high.png" },
        fallbackImageUrl = fallback_image_url,
        fallbackImageSource = fallback_image_source,
        rarityId = rarity_id,
        rarityName = rarity_name,
        illustratorId = illustrator_id,
        illustratorName = illustrator_name,
        pokemonDexId = null, // card_with_set doesn't include dex ID
        regulationMark = regulation_mark,
        category = category,
        types = parseTypes(types),
        supertype = supertype,
        reference = buildReference(local_id, set_card_count_official.toInt()),
        setOfficialCardCount = set_card_count_official.toInt(),
        setReleaseDate = set_release_date,
        // Embedded Cardmarket EUR pricing snapshot
        priceCardmarketTrend = price_cardmarket_trend,
        priceCardmarketAvg = price_cardmarket_avg,
        priceCardmarketLow = price_cardmarket_low,
        priceUpdatedIso = price_updated_iso,
        priceUnit = price_unit,
    )
}

/**
 * Maps a SQLDelight Card_with_pokemon view row to a domain Card model.
 * The view joins cards with the card_pokemon junction table to support multi-Pokémon cards.
 * Used for Pokédex queries where a card may appear multiple times (once per dex ID).
 * Includes embedded Cardmarket EUR pricing snapshot.
 */
private fun Card_with_pokemon.toModel(): Card {
    val baseImage = image_url?.let { selectBaseImageUrl(it) ?: it }
    return Card(
        id = id,
        localId = local_id,
        setId = set_id,
        setName = set_name,
        setLanguage = language,
        originLanguage = origin_language,
        serieId = serie_id,
        serieName = serie_name,
        name = name,
        imageUrl = baseImage,
        thumbnailUrl = baseImage?.let { toThumbnailUrl(it) ?: "$it/low.png" },
        highQualityUrl = baseImage?.let { toHighQualityUrl(it) ?: "$it/high.png" },
        fallbackImageUrl = fallback_image_url,
        fallbackImageSource = fallback_image_source,
        rarityId = rarity_id,
        rarityName = rarity_name,
        illustratorId = illustrator_id,
        illustratorName = illustrator_name,
        pokemonDexId = pokemon_dex_id.toInt(),
        regulationMark = regulation_mark,
        category = category,
        types = parseTypes(types),
        supertype = supertype,
        reference = buildReference(local_id, set_card_count_official.toInt()),
        setOfficialCardCount = set_card_count_official.toInt(),
        setReleaseDate = set_release_date,
        // Embedded Cardmarket EUR pricing snapshot
        priceCardmarketTrend = price_cardmarket_trend,
        priceCardmarketAvg = price_cardmarket_avg,
        priceCardmarketLow = price_cardmarket_low,
        priceUpdatedIso = price_updated_iso,
        priceUnit = price_unit,
    )
}

private fun Card_prices.toModel(): CardPrice = CardPrice(
    cardId = card_id,
    cardLanguage = card_language,
    variant = variant,
    priceLanguage = price_language,
    sellerCountry = seller_country,
    currency = currency,
    minPrice = min_price,
    avgPrice = avg_price,
    medianPrice = median_price,
    maxPrice = max_price,
    recommendedPrice = recommended_price,
    availableCount = available_count,
    productId = product_id,
)

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
