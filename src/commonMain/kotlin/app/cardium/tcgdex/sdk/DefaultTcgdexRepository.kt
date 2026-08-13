package app.cardium.tcgdex.sdk

import app.cardium.tcgdex.db.Card_with_pokemon
import app.cardium.tcgdex.db.Card_with_set
import app.cardium.tcgdex.db.Card_prices
import app.cardium.tcgdex.db.Card_recognition_hashes
import app.cardium.tcgdex.db.CountCardsPerIllustrator
import app.cardium.tcgdex.db.GetSetWithSerieName
import app.cardium.tcgdex.db.Illustrators
import app.cardium.tcgdex.db.Rarities
import app.cardium.tcgdex.db.Series
import app.cardium.tcgdex.db.Sets
import app.cardium.tcgdex.db.TcgdexDatabase
import app.cardium.tcgdex.sdk.model.Card
import app.cardium.tcgdex.sdk.model.CardPrice
import app.cardium.tcgdex.sdk.model.CardRecognitionHash
import app.cardium.tcgdex.sdk.model.CardSet
import app.cardium.tcgdex.sdk.model.Illustrator
import app.cardium.tcgdex.sdk.model.IllustratorCardIdEntry
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
import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
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
        val result = queries.getAllSeriesByLanguage(language).awaitAsList()
        println("[Tcgdex][i] getAllSeries($language) returned ${result.size} rows")
        result.map { it.toModel() }
    }

    override suspend fun getSerieById(serieId: String, language: String): Serie? = withContext(dispatcher) {
        queries.getSeriesById(serieId, language).awaitAsOneOrNull()?.toModel()
    }

    // =========================================================================
    // Set Queries
    // =========================================================================

    override suspend fun getAllSets(language: String): List<CardSet> = withContext(dispatcher) {
        // Load series names once (localized rows first, EN fallback rows fill holes)
        // instead of issuing one getSeriesById query per set row (N+1).
        val serieNameById = queries.getAllSeriesByLanguage(language)
            .awaitAsList()
            .associate { it.id to it.name }
        val result = queries.getAllSetsByLanguage(language).awaitAsList()
        println("[Tcgdex][i] getAllSets($language) returned ${result.size} rows")
        result.map { set ->
            set.toModel(serieNameById[set.serie_id])
        }
    }

    override suspend fun getSetIdsWithCards(language: String): Set<String> = withContext(dispatcher) {
        queries.getSetIdsWithCards(language).awaitAsList().toSet()
    }

    override suspend fun getSetsForSerie(serieId: String, language: String): List<CardSet> =
        withContext(dispatcher) {
            val serieName = queries.getSeriesById(serieId, language)
                .awaitAsOneOrNull()?.name
            queries.getSetsForSerie(serieId, language).awaitAsList().map { it.toModel(serieName) }
        }

    override suspend fun getSetById(setId: String, language: String): CardSet? = withContext(dispatcher) {
        queries.getSetWithSerieName(setId, language).awaitAsOneOrNull()?.toModel()
    }

    // =========================================================================
    // Card Queries
    // =========================================================================

    override suspend fun getCardsForSet(setId: String, language: String): List<Card> =
        withContext(dispatcher) {
            queries.getCardsForSet(setId, language).awaitAsList().map { it.toModel() }
        }

    override suspend fun getCardsForIllustrator(illustratorId: String, language: String): List<Card> =
        withContext(dispatcher) {
            queries.getCardsForIllustrator(illustratorId, language).awaitAsList().map { it.toModel() }
        }

    override suspend fun getCardsForRarity(rarityId: String, language: String): List<Card> =
        withContext(dispatcher) {
            queries.getCardsForRarity(rarityId, language).awaitAsList().map { it.toModel() }
        }

    override suspend fun getCardsForPokemonDexId(dexId: Int, language: String): List<Card> =
        withContext(dispatcher) {
            queries.getCardsForDexId(dexId.toLong(), language).awaitAsList().map { it.toModel() }
        }

    override suspend fun getCardById(cardId: String, language: String): Card? = withContext(dispatcher) {
        queries.getCardById(cardId, language).awaitAsOneOrNull()?.toModel()
    }

    override suspend fun getRecognitionHashesForCard(cardId: String, language: String): List<CardRecognitionHash> =
        withContext(dispatcher) {
            queries.getCardRecognitionHashesForCard(cardId, language)
                .awaitAsList()
                .map { it.toModel() }
        }

    override suspend fun getRecognitionHashesForPokemon(language: String): List<CardRecognitionHash> =
        withContext(dispatcher) {
            queries.getCardRecognitionHashesForPokemon(language)
                .awaitAsList()
                .map { it.toModel() }
        }

    override suspend fun getCardPrices(cardId: String, language: String): List<CardPrice> =
        withContext(dispatcher) {
            queries.getCardPrices(cardId, language).awaitAsList().map { it.toModel() }
        }

    override suspend fun getCardPricesForCard(
        cardId: String,
        language: String,
        sellerCountry: String,
    ): List<CardPrice> = withContext(dispatcher) {
        queries.getCardPriceRowsForCard(cardId, language, sellerCountry)
            .awaitAsList()
            .map { it.toModel() }
    }

    override suspend fun getCardPricesForCards(
        cardIds: Collection<String>,
        language: String,
        sellerCountry: String,
    ): Map<String, List<CardPrice>> = withContext(dispatcher) {
        if (cardIds.isEmpty()) return@withContext emptyMap()
        val result = LinkedHashMap<String, MutableList<CardPrice>>()
        cardIds.toList().chunked(500).forEach { chunk ->
            queries.getCardPriceRowsByCountry(chunk, language, sellerCountry)
                .awaitAsList()
                .forEach { row ->
                    result.getOrPut(row.card_id) { mutableListOf() }.add(row.toModel())
                }
        }
        result.mapValues { it.value.toList() }
    }

    override suspend fun getAllSellerCountries(): List<String> =
        withContext(dispatcher) {
            queries.getAllSellerCountries().awaitAsList().map { it.trim() }.distinct()
        }

    override suspend fun getLatestPriceUpdateIso(): String? =
        withContext(dispatcher) {
            queries.getLatestPriceUpdateIso().awaitAsOneOrNull()?.latest_price_update_iso
        }

    override suspend fun getLatestExportPriceUpdateIso(): String? =
        withContext(dispatcher) {
            queries.getLatestExportPriceUpdateIso().awaitAsOneOrNull()?.latest_export_update_iso
        }

    override suspend fun getCardsByIds(cardIds: Collection<String>, language: String): Map<String, Card> =
        withContext(dispatcher) {
            if (cardIds.isEmpty()) return@withContext emptyMap()

            // SQLite has a limit on the number of bound variables; chunk to stay safe.
            val result = LinkedHashMap<String, Card>(cardIds.size)
            cardIds.toList().chunked(500).forEach { chunk ->
                queries.getCardsByIds(chunk, language)
                    .awaitAsList()
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
            .awaitAsList()
            .map { it.toModel() }
    }

    override suspend fun searchCardsByNameFts(
        query: String,
        language: String,
        limit: Int,
        offset: Int
    ): List<Card> = withContext(dispatcher) {
        queries.searchCardsByNameFts(language, query, limit.toLong(), offset.toLong())
            .awaitAsList()
            .map { it.toModel() }
    }

    override suspend fun countCardsByName(query: String, language: String): Long = withContext(dispatcher) {
        queries.countCardsByName(language, query).awaitAsOne()
    }

    // =========================================================================
    // Local ID / Reference Search Queries
    // =========================================================================

    override suspend fun searchCardsByLocalId(localId: String, language: String, limit: Int): List<Card> =
        withContext(dispatcher) {
            queries.searchCardsByLocalId(language, localId, limit.toLong())
                .awaitAsList()
                .map { it.toModel() }
        }

    override suspend fun searchCardsByLocalIdInSet(localId: String, setId: String, language: String): List<Card> =
        withContext(dispatcher) {
            queries.searchCardsByLocalIdInSet(language, setId, localId)
                .awaitAsList()
                .map { it.toModel() }
        }

    override suspend fun searchCardsByLocalIdAndCardCount(
        localId: String,
        cardCount: Int,
        language: String,
        limit: Int
    ): List<Card> = withContext(dispatcher) {
        queries.searchCardsByLocalIdAndCardCount(language, localId, cardCount.toLong(), limit.toLong())
            .awaitAsList()
            .map { it.toModel() }
    }

    override suspend fun searchSetsByName(query: String, language: String, limit: Int): List<CardSet> =
        withContext(dispatcher) {
            queries.searchSetsByName(language, query, limit.toLong())
                .awaitAsList()
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
                        releaseDate = row.release_date,
                        officialAbbreviation = row.abbreviation_official,
                        parentSetId = row.parent_set_id,
                    )
                }
        }

    // =========================================================================
    // Illustrator & Rarity Queries
    // =========================================================================

    override suspend fun getAllIllustrators(): List<Illustrator> = withContext(dispatcher) {
        val result = queries.getAllIllustrators().awaitAsList()
        println("[Tcgdex][i] getAllIllustrators() returned ${result.size} rows")
        result.map { it.toModel() }
    }

    override suspend fun getIllustratorsWithCounts(language: String): List<IllustratorWithCount> =
        withContext(dispatcher) {
            queries.countCardsPerIllustrator(language).awaitAsList().map { it.toModel() }
        }

    override suspend fun getCardIdsByLanguage(language: String): List<IllustratorCardIdEntry> =
        withContext(dispatcher) {
            queries.getCardIdsByLanguage(language).awaitAsList().map { row ->
                IllustratorCardIdEntry(
                    cardId = row.card_id,
                    illustratorId = row.illustrator_id,
                    rarityId = row.rarity_id,
                    setReleaseDate = row.set_release_date,
                    setId = row.set_id,
                    localId = row.local_id,
                    category = row.category,
                )
            }
        }

    override suspend fun getIllustratorValueSums(language: String): Map<String, Double> =
        withContext(dispatcher) {
            queries.getIllustratorValueSumByLanguage(language)
                .awaitAsList()
                .mapNotNull { row ->
                    row.total_value?.let { totalValue -> row.illustrator_id to totalValue }
                }.toMap()
        }

    override suspend fun getAllRarities(): List<Rarity> = withContext(dispatcher) {
        queries.getAllRarities().awaitAsList().map { it.toModel() }
    }

    // =========================================================================
    // Pokémon Dex Queries
    // =========================================================================

    override suspend fun getAllPokemonDexEntries(language: String): List<PokemonDexEntry> =
        withContext(dispatcher) {
            queries.getAllPokemonDexIds(language).awaitAsList().map { row ->
                PokemonDexEntry(
                    dexId = row.dex_id.toInt(),
                    name = row.pokemon_name ?: "Unknown",
                    cardCount = row.card_count.toInt()
                )
            }
        }

    override suspend fun getAllCardIdsByPokemon(language: String): Map<Int, Set<String>> =
        withContext(dispatcher) {
            val result = mutableMapOf<Int, MutableSet<String>>()
            queries.getAllCardIdsByPokemon(language).awaitAsList().forEach { row ->
                val dexId = row.dex_id.toInt()
                val cardId = row.card_id
                result.getOrPut(dexId) { mutableSetOf() }.add(cardId)
            }
            result.mapValues { it.value.toSet() }
        }

    override suspend fun getCardCountsPerSetForPokemon(dexId: Int, language: String): List<PokemonSetCardCount> =
        withContext(dispatcher) {
            queries.getCardCountsPerSetForDexId(dexId.toLong(), language).awaitAsList().map { row ->
                PokemonSetCardCount(
                    setId = row.set_id,
                    setName = row.set_name ?: row.set_id,
                    releaseDate = row.release_date,
                    serieId = row.serie_id ?: "unknown",
                    serieName = row.serie_name,
                    logoUrl = row.logo_url,
                    symbolUrl = row.symbol_url,
                    cardCount = row.card_count.toInt()
                )
            }
        }

    override suspend fun getCardsForPokemonInSet(dexId: Int, setId: String, language: String): List<Card> =
        withContext(dispatcher) {
            queries.getCardsForDexIdInSet(dexId.toLong(), setId, language).awaitAsList().map { it.toModel() }
        }

    override suspend fun getDexIdForCard(cardId: String, language: String): Int? =
        withContext(dispatcher) {
            queries.getDexIdForCard(cardId, language).awaitAsOneOrNull()?.toInt()
        }

    override suspend fun getDexIdsForCards(cardIds: List<String>, language: String): Map<String, Int> =
        withContext(dispatcher) {
            if (cardIds.isEmpty()) return@withContext emptyMap()

            val rows = queries.getDexIdsForCards(cardIds, language).awaitAsList()
            val result = mutableMapOf<String, Int>()

            // card_with_pokemon can contain multiple rows for a single card (multi-Pokémon cards).
            // Preserve legacy semantics from getDexIdForCard(): keep the first dex ID we see per card.
            for (row in rows) {
                val dex = row.pokemon_dex_id.toInt()
                if (!result.containsKey(row.id)) {
                    result[row.id] = dex
                }
            }
            result
        }

    override suspend fun getEvolutionDepthsForDexIds(dexIds: Collection<Int>, language: String): Map<Int, Int> =
        withContext(dispatcher) {
            if (dexIds.isEmpty()) return@withContext emptyMap()

            val evolvesFromByDex = mutableMapOf<Int, Int?>()

            suspend fun loadSpeciesRows(ids: Collection<Int>, lang: String) {
                if (ids.isEmpty()) return
                queries.getPokemonSpeciesByDexIds(ids.map(Int::toLong), lang)
                    .awaitAsList()
                    .forEach { row ->
                        evolvesFromByDex[row.dex_id.toInt()] = row.evolves_from?.toInt()
                    }
            }

            val pending = ArrayDeque<Int>()
            val queued = mutableSetOf<Int>()
            dexIds.forEach { dexId ->
                pending.addLast(dexId)
                queued += dexId
            }

            while (pending.isNotEmpty()) {
                val batch = mutableListOf<Int>()
                while (pending.isNotEmpty() && batch.size < 500) {
                    batch += pending.removeFirst()
                }

                loadSpeciesRows(batch, language)
                val unresolved = batch.filterNot { evolvesFromByDex.containsKey(it) }
                if (unresolved.isNotEmpty() && language.lowercase() != "en") {
                    loadSpeciesRows(unresolved, "en")
                }

                batch.forEach { dexId ->
                    val parentDexId = evolvesFromByDex[dexId]
                    if (parentDexId != null && queued.add(parentDexId)) {
                        pending.addLast(parentDexId)
                    }
                }
            }

            fun computeDepth(dexId: Int): Int {
                var depth = 0
                var current = evolvesFromByDex[dexId]
                val seen = mutableSetOf<Int>()
                while (current != null && seen.add(current)) {
                    depth += 1
                    current = evolvesFromByDex[current]
                }
                return depth
            }

            dexIds.associateWith(::computeDepth)
        }

    // =========================================================================
    // Utility Queries
    // =========================================================================

    override suspend fun getAvailableLanguages(): List<String> = withContext(dispatcher) {
        queries.getAvailableLanguages().awaitAsList()
    }

    // =========================================================================
    // Rarity Aggregation Queries
    // =========================================================================

    override suspend fun getRaritiesGroupedBySeries(language: String): List<RarityAggregate> =
        withContext(dispatcher) {
            val result = queries.getRaritiesGroupedBySeries(language).awaitAsList()
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
            val result = queries.getRaritiesGroupedBySeriesAndSet(language).awaitAsList()
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
    releaseDate = release_date,
    officialAbbreviation = abbreviation_official,
    parentSetId = parent_set_id,
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
    releaseDate = release_date,
    officialAbbreviation = abbreviation_official,
    parentSetId = parent_set_id,
)

/**
 * Maps a SQLDelight Card_with_set view row to a domain Card model.
 * The view pre-joins cards with sets, series, illustrators, and rarities.
 * Note: This view does NOT include pokemon_dex_id (use Card_with_pokemon for Pokédex queries).
 * Includes embedded Cardmarket EUR pricing snapshot.
 */
private fun Card_with_set.toModel(): Card {
    val baseImage = image_url?.let { selectBaseImageUrl(it) ?: it }
    val resolvedSetName = set_name ?: set_id
    val resolvedSerieId = serie_id ?: "unknown"
    val resolvedSetOfficialCount = set_card_count_official?.toInt() ?: 0
    return Card(
        id = id,
        localId = local_id,
        setId = set_id,
        setName = resolvedSetName,
        setLanguage = language,
        originLanguage = origin_language,
        serieId = resolvedSerieId,
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
        hp = hp?.toInt(),
        category = category,
        types = parseTypes(types),
        supertype = supertype,
        reference = buildReference(local_id, resolvedSetOfficialCount),
        setOfficialCardCount = resolvedSetOfficialCount,
        setReleaseDate = set_release_date,
        // Embedded Cardmarket EUR pricing snapshot
        priceCardmarketTrend = price_cardmarket_trend,
        priceCardmarketAvg = price_cardmarket_avg,
        priceCardmarketLow = price_cardmarket_low,
        priceUpdatedIso = price_updated_iso,
        priceUnit = price_unit,
        isCameo = false,
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
    val resolvedSetName = set_name ?: set_id
    val resolvedSerieId = serie_id ?: "unknown"
    val resolvedSetOfficialCount = set_card_count_official?.toInt() ?: 0
    return Card(
        id = id,
        localId = local_id,
        setId = set_id,
        setName = resolvedSetName,
        setLanguage = language,
        originLanguage = origin_language,
        serieId = resolvedSerieId,
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
        hp = hp?.toInt(),
        category = category,
        types = parseTypes(types),
        supertype = supertype,
        reference = buildReference(local_id, resolvedSetOfficialCount),
        setOfficialCardCount = resolvedSetOfficialCount,
        setReleaseDate = set_release_date,
        // Embedded Cardmarket EUR pricing snapshot
        priceCardmarketTrend = price_cardmarket_trend,
        priceCardmarketAvg = price_cardmarket_avg,
        priceCardmarketLow = price_cardmarket_low,
        priceUpdatedIso = price_updated_iso,
        priceUnit = price_unit,
        isCameo = is_cameo != 0L,
    )
}

private fun Card_prices.toModel(): CardPrice = CardPrice(
    cardId = card_id,
    cardLanguage = card_language,
    variant = variant,
    priceLanguage = price_language,
    sellerCountry = seller_country,
    condition = condition,
    currency = currency,
    minPrice = min_price,
    avgPrice = avg_price,
    medianPrice = median_price,
    maxPrice = max_price,
    recommendedPrice = recommended_price,
    availableCount = available_count,
    productId = product_id,
)

private fun Card_recognition_hashes.toModel(): CardRecognitionHash = CardRecognitionHash(
    cardId = card_id,
    language = language,
    imageSource = image_source,
    imageUrl = image_url,
    lighting = lighting,
    rotation = rotation.toInt(),
    dhash = dhash,
    phash = phash,
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
