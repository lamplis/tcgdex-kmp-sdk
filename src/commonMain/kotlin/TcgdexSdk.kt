package app.cardium.tcgdex.sdk

import io.ktor.client.HttpClient
import app.cardium.tcgdex.sdk.http.createPlatformHttpClient
import app.cardium.tcgdex.sdk.model.TcgdxSet
import app.cardium.tcgdex.sdk.model.TcgdxSetResponse
import app.cardium.tcgdex.sdk.model.TcgdxCard
import app.cardium.tcgdex.sdk.model.TcgdxSearchResponse
import app.cardium.tcgdex.sdk.model.TcgdxSerie
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.Clock
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.Serializable

/**
 * Minimal entrypoint for the TCGdex KMP SDK.
 * The concrete HTTP engine should be supplied by the platform layer.
 */
interface TcgDexClient {
    suspend fun ping(): Result<Boolean>

    suspend fun getSets(
        language: String = "en",
        page: Int = 1,
        pageSize: Int = 50,
    ): Result<TcgdxSetResponse>

    suspend fun getSetById(
        language: String = "en",
        setId: String,
    ): Result<TcgdxSet>

    suspend fun getCardById(
        language: String = "en",
        cardId: String,
    ): Result<TcgdxCard>

    suspend fun searchCardsByName(
        language: String = "en",
        query: String,
        page: Int = 1,
        pageSize: Int = 20,
    ): Result<TcgdxSearchResponse>

    suspend fun getCardsBySet(
        language: String = "en",
        setId: String,
        page: Int = 1,
        pageSize: Int = 20,
    ): Result<TcgdxSearchResponse>

    suspend fun getSeries(
        language: String = "en",
        page: Int = 1,
        pageSize: Int = 50,
    ): Result<TcgdxSerieResponse>

    suspend fun searchNameIndex(
        language: String = "en",
        query: String,
    ): Result<TcgdxNameIndexPage>

    // GraphQL: search cards by name with set details
    suspend fun searchCardsGraphQL(
        language: String = "en",
        query: String,
        page: Int = 1,
        pageSize: Int = 50,
    ): Result<TcgdxSearchResponse>

    // GraphQL: get series with sets
    suspend fun getSeriesWithSetsGraphQL(): Result<List<TcgdxSerieWithSets>>

    // Maintenance: clear internal caches (sets/series/pocket registry)
    fun clearCaches(language: String? = null)

    // REST: search by localId token (e.g., "075", "75", "TG09")
    suspend fun searchCardsByLocalId(
        language: String = "en",
        localId: String,
        page: Int = 1,
        pageSize: Int = 50,
    ): Result<TcgdxSearchResponse>

    // Reference-aware search (promos and exact ids).
    suspend fun searchCardByReference(
        language: String = "en",
        input: String,
        timeoutMs: Long = 20_000,
    ): Result<TcgdxCard?>

    // Numeric ref search: "<number>/<officialCount>", e.g., "159/086"
    suspend fun searchCardsByNumericReference(
        language: String = "en",
        input: String,
        timeoutMs: Long = 20_000,
    ): Result<List<TcgdxCard>>
}

@Serializable
data class TcgdxSerieWithSets(
    val id: String,
    val name: String,
    val sets: List<TcgdxSetRef> = emptyList(),
)

@Serializable
data class TcgdxSetRef(
    val id: String,
    val name: String,
)

@Serializable
data class MinimalCard(
    val id: String,
    val localId: String? = null,
    val name: String,
)

@Serializable
data class TcgdxNameIndexItem(
    val id: String,
    val localId: String? = null,
    val name: String,
    val image: String? = null,
    val official: Int? = null,
    val setId: String? = null,
    val setName: String? = null,
)

@Serializable
data class TcgdxNameIndexPage(
    val items: List<TcgdxNameIndexItem>,
    val total: Int,
)

private data class CachedSets(
    val fetchedAtMs: Long,
    val sets: List<TcgdxSet>,
)

private data class CachedSeries(
    val fetchedAtMs: Long,
    val series: List<TcgdxSerie>,
)

@Serializable
data class TcgdxSerieResponse(
    val data: List<TcgdxSerie>? = null,
    val total: Int? = null,
    val page: Int? = null,
    val pageSize: Int? = null,
)

class TcgDexClientImpl(
    private val httpClient: HttpClient,
) : TcgDexClient {
    private val setsCache: MutableMap<String, CachedSets> = mutableMapOf()
    private val setsCacheTtlMs: Long = 24L * 60L * 60L * 1000L // 24 hours per product requirement
    private val seriesCache: MutableMap<String, CachedSeries> = mutableMapOf()

    override suspend fun ping(): Result<Boolean> = Result.success(true)

    override suspend fun getSets(
        language: String,
        page: Int,
        pageSize: Int,
    ): Result<TcgdxSetResponse> =
        runCatching {
            val nowMs = Clock.System.now().toEpochMilliseconds()
            val cached: List<TcgdxSet>? =
                setsCache[language]?.let { cache ->
                    val fresh = (nowMs - cache.fetchedAtMs) < setsCacheTtlMs
                    if (fresh) cache.sets else null
                }
            val all: List<TcgdxSet> =
                cached ?: httpClient
                    .get("https://api.tcgdex.net/v2/$language/sets")
                    .body<List<TcgdxSet>>()
                    .also { fetched ->
                        setsCache[language] = CachedSets(fetchedAtMs = nowMs, sets = fetched)
                    }
            val start = (page - 1) * pageSize
            val end = minOf(start + pageSize, all.size)
            val pageItems = if (start < all.size) all.subList(start, end) else emptyList()
            TcgdxSetResponse(data = pageItems, total = all.size, page = page, pageSize = pageSize)
        }

    override suspend fun getSetById(
        language: String,
        setId: String,
    ): Result<TcgdxSet> =
        runCatching {
            val nowMs = Clock.System.now().toEpochMilliseconds()
            val cachedHit: TcgdxSet? =
                setsCache[language]?.let { cache ->
                    val fresh = (nowMs - cache.fetchedAtMs) < setsCacheTtlMs
                    if (fresh) cache.sets.firstOrNull { it.id == setId } else null
                }
            cachedHit ?: httpClient.get("https://api.tcgdex.net/v2/$language/sets/$setId").body()
        }

    override suspend fun getCardById(
        language: String,
        cardId: String,
    ): Result<TcgdxCard> =
        runCatching {
            httpClient.get("https://api.tcgdex.net/v2/$language/cards/$cardId").body()
        }

    override suspend fun searchCardsByName(
        language: String,
        query: String,
        page: Int,
        pageSize: Int,
    ): Result<TcgdxSearchResponse> =
        runCatching {
            val arr: JsonArray =
                httpClient.get("https://api.tcgdex.net/v2/$language/cards") {
                    url { parameters.append("name", query) }
                }.body()
            val items: List<MinimalCard> =
                arr.mapNotNull { el ->
                    val obj = el as? JsonObject ?: return@mapNotNull null
                    val id = obj["id"]?.jsonPrimitive?.content ?: return@mapNotNull null
                    val local = obj["localId"]?.jsonPrimitive?.content
                    val nm = obj["name"]?.jsonPrimitive?.content ?: ""
                    if (app.cardium.tcgdex.sdk.model.PocketFilter.isPocketId(id)) return@mapNotNull null
                    MinimalCard(id = id, localId = local, name = nm)
                }
            val total = items.size
            // No paging for localId search; return all
            val slice = items
            val cards: List<TcgdxCard> =
                slice.mapNotNull { minimal ->
                    runCatching { httpClient.get("https://api.tcgdex.net/v2/$language/cards/${minimal.id}").body<TcgdxCard>() }.getOrNull()
                }
            TcgdxSearchResponse(data = cards, total = total, page = 1, pageSize = total)
        }

    override suspend fun getSeries(
        language: String,
        page: Int,
        pageSize: Int,
    ): Result<TcgdxSerieResponse> =
        runCatching {
            val nowMs = Clock.System.now().toEpochMilliseconds()
            val cachedSeries: List<TcgdxSerie>? =
                seriesCache[language]?.let { cache ->
                    val fresh = (nowMs - cache.fetchedAtMs) < setsCacheTtlMs
                    if (fresh) cache.series else null
                }

            val series: List<TcgdxSerie> =
                cachedSeries ?: run {
                    // Ensure we have a fresh sets list
                    val sets: List<TcgdxSet> =
                        setsCache[language]?.let { cache ->
                            val fresh = (nowMs - cache.fetchedAtMs) < setsCacheTtlMs
                            if (fresh) cache.sets else null
                        } ?: httpClient
                            .get("https://api.tcgdex.net/v2/$language/sets")
                            .body<List<TcgdxSet>>()
                            .also { fetched ->
                                setsCache[language] = CachedSets(fetchedAtMs = nowMs, sets = fetched)
                            }

                    val derived: List<TcgdxSerie> =
                        sets
                            .mapNotNull { it.serie }
                            .distinctBy { it.id.lowercase() }

                    seriesCache[language] = CachedSeries(fetchedAtMs = nowMs, series = derived)
                    derived
                }

            val total = series.size
            val start = (page - 1) * pageSize
            val end = minOf(start + pageSize, total)
            val pageItems = if (start < total) series.subList(start, end) else emptyList()
            TcgdxSerieResponse(data = pageItems, total = total, page = page, pageSize = pageSize)
        }

    override suspend fun getCardsBySet(
        language: String,
        setId: String,
        page: Int,
        pageSize: Int,
    ): Result<TcgdxSearchResponse> =
        runCatching {
            val arr: JsonArray =
                httpClient.get("https://api.tcgdex.net/v2/$language/cards") {
                    url { parameters.append("set", setId) }
                }.body()
            val items: List<MinimalCard> =
                arr.mapNotNull { el ->
                    val obj = el as? JsonObject ?: return@mapNotNull null
                    val id = obj["id"]?.jsonPrimitive?.content ?: return@mapNotNull null
                    val local = obj["localId"]?.jsonPrimitive?.content
                    val nm = obj["name"]?.jsonPrimitive?.content ?: ""
                    MinimalCard(id = id, localId = local, name = nm)
                }
            val total = items.size
            val start = (page - 1) * pageSize
            val end = minOf(start + pageSize, total)
            val slice = if (start < total) items.subList(start, end) else emptyList()
            val cards: List<TcgdxCard> =
                slice.mapNotNull { minimal ->
                    runCatching { httpClient.get("https://api.tcgdex.net/v2/$language/cards/${minimal.id}").body<TcgdxCard>() }.getOrNull()
                }
            TcgdxSearchResponse(data = cards, total = total, page = page, pageSize = pageSize)
        }

    override suspend fun searchNameIndex(
        language: String,
        query: String,
    ): Result<TcgdxNameIndexPage> =
        runCatching {
            val arr: JsonArray =
                httpClient.get("https://api.tcgdex.net/v2/$language/cards") {
                    url { parameters.append("name", query) }
                }.body()
            val items: List<TcgdxNameIndexItem> =
                arr.mapNotNull { el ->
                    val obj = el as? JsonObject ?: return@mapNotNull null
                    val id = obj["id"]?.jsonPrimitive?.content ?: return@mapNotNull null
                    val local = obj["localId"]?.jsonPrimitive?.content
                    val nm = obj["name"]?.jsonPrimitive?.content ?: ""
                    val img = obj["image"]?.jsonPrimitive?.content
                    if (app.cardium.tcgdex.sdk.model.PocketFilter.isPocketId(id)) return@mapNotNull null
                    val setObj = obj["set"] as? JsonObject
                    val official =
                        (setObj?.get("cardCount") as? JsonObject)
                            ?.get("official")
                            ?.jsonPrimitive
                            ?.let { primitive -> primitive.content.toIntOrNull() }
                    val setId = setObj?.get("id")?.jsonPrimitive?.content
                    val setName = setObj?.get("name")?.jsonPrimitive?.content
                    TcgdxNameIndexItem(
                        id = id,
                        localId = local,
                        name = nm,
                        image = img,
                        official = official,
                        setId = setId,
                        setName = setName,
                    )
                }
            TcgdxNameIndexPage(items = items, total = items.size)
        }

    override suspend fun searchCardsGraphQL(
        language: String,
        query: String,
        page: Int,
        pageSize: Int,
    ): Result<TcgdxSearchResponse> =
        runCatching {
            // GraphQL query; server ignores pagination, we page client-side
            val gql =
                """
                { cards(filters: { name: \"$query\" }) {
                    id
                    localId
                    name
                    image
                    set { id name cardCount { official } }
                    rarity
                  }
                }
                """.trimIndent()

            @kotlinx.serialization.Serializable
            data class GraphRequest(val query: String)

            @kotlinx.serialization.Serializable
            data class GraphErrors(val message: String)

            @kotlinx.serialization.Serializable
            data class GraphData(val cards: List<app.cardium.tcgdex.sdk.model.TcgdxCard>)

            @kotlinx.serialization.Serializable
            data class GraphResponse(val data: GraphData? = null, val errors: List<GraphErrors>? = null)

            val resp: GraphResponse =
                httpClient.post("https://api.tcgdex.net/v2/graphql") {
                    contentType(ContentType.Application.Json)
                    setBody(GraphRequest(gql))
                }.body()

            if (!resp.errors.isNullOrEmpty()) {
                throw IllegalStateException(resp.errors.joinToString("; ") { it.message })
            }

            val all =
                resp.data?.cards.orEmpty()
                    .filterNot { card -> app.cardium.tcgdex.sdk.model.PocketFilter.isPocketBySeriesOrId(card) }

            val total = all.size
            val start = (page - 1) * pageSize
            val end = minOf(start + pageSize, total)
            val slice = if (start < total) all.subList(start, end) else emptyList()
            TcgdxSearchResponse(data = slice, total = total, page = page, pageSize = pageSize)
        }

    override suspend fun getSeriesWithSetsGraphQL(): Result<List<TcgdxSerieWithSets>> =
        runCatching {
            val gql =
                """
                { series {
                    id
                    name
                    sets { id name }
                  } }
                """.trimIndent()

            @kotlinx.serialization.Serializable
            data class GraphRequest(val query: String)

            @kotlinx.serialization.Serializable
            data class GraphErrors(val message: String)

            @kotlinx.serialization.Serializable
            data class Serie(val id: String, val name: String, val sets: List<TcgdxSetRef> = emptyList())

            @kotlinx.serialization.Serializable
            data class GraphData(val series: List<Serie> = emptyList())

            @kotlinx.serialization.Serializable
            data class GraphResponse(val data: GraphData? = null, val errors: List<GraphErrors>? = null)

            val resp: GraphResponse =
                httpClient.post("https://api.tcgdex.net/v2/graphql") {
                    contentType(ContentType.Application.Json)
                    setBody(GraphRequest(gql))
                }.body()

            if (!resp.errors.isNullOrEmpty()) {
                throw IllegalStateException(resp.errors.joinToString("; ") { it.message })
            }

            resp.data?.series?.map { s -> TcgdxSerieWithSets(id = s.id, name = s.name, sets = s.sets) } ?: emptyList()
        }

    override fun clearCaches(language: String?) {
        if (language == null) {
            setsCache.clear()
            seriesCache.clear()
            app.cardium.tcgdex.sdk.model.PocketRegistry.clear(null)
        } else {
            setsCache.remove(language.lowercase())
            seriesCache.remove(language.lowercase())
            app.cardium.tcgdex.sdk.model.PocketRegistry.clear(language)
        }
    }

    override suspend fun searchCardsByLocalId(
        language: String,
        localId: String,
        page: Int,
        pageSize: Int,
    ): Result<TcgdxSearchResponse> =
        runCatching {
            val arr: JsonArray =
                httpClient.get("https://api.tcgdex.net/v2/$language/cards") {
                    url { parameters.append("localId", localId) }
                }.body()
            val items: List<MinimalCard> =
                arr.mapNotNull { el ->
                    val obj = el as? JsonObject ?: return@mapNotNull null
                    val id = obj["id"]?.jsonPrimitive?.content ?: return@mapNotNull null
                    val local = obj["localId"]?.jsonPrimitive?.content
                    val nm = obj["name"]?.jsonPrimitive?.content ?: ""
                    if (app.cardium.tcgdex.sdk.model.PocketFilter.isPocketId(id)) return@mapNotNull null
                    MinimalCard(id = id, localId = local, name = nm)
                }
            val total = items.size
            val start = (page - 1) * pageSize
            val end = minOf(start + pageSize, total)
            val slice = if (start < total) items.subList(start, end) else emptyList()
            val cards: List<TcgdxCard> =
                slice.mapNotNull { minimal ->
                    runCatching { httpClient.get("https://api.tcgdex.net/v2/$language/cards/${minimal.id}").body<TcgdxCard>() }.getOrNull()
                }
            TcgdxSearchResponse(data = cards, total = total, page = page, pageSize = pageSize)
        }

    private fun detectPromoTokens(raw: String): Pair<String, String>? {
        val m = Regex("""(?i)^\s*([a-z]{2,4})\s*[-/\s]?\s*(\d{1,3})\s*$""").matchEntire(raw.trim()) ?: return null
        return m.groupValues[1].lowercase() to m.groupValues[2]
    }

    private fun resolvePromoExactId(prefix: String, digits: String): String? {
        val pad = digits.padStart(3, '0')
        return when (prefix.lowercase()) {
            "svp" -> "svp-$pad"
            "swsh" -> "swshp-SWSH$pad"
            "sm" -> "smp-SM$pad"
            "xy" -> "xyp-XY$pad"
            "bw" -> "bwp-BW$pad"
            "hgss" -> "hsp-HGSS$pad"
            "dp" -> "dpp-DP$pad"
            else -> null
        }
    }

    override suspend fun searchCardByReference(
        language: String,
        input: String,
        timeoutMs: Long,
    ): Result<TcgdxCard?> =
        runCatching {
            val trimmed = input.trim()
            val promo = detectPromoTokens(trimmed) ?: return@runCatching null
            val (prefix, digits) = promo
            if (digits.length < 3) return@runCatching null
            // Exact id fast path
            resolvePromoExactId(prefix, digits)?.let { exactId ->
                try {
                    return@runCatching kotlinx.coroutines.withTimeout(timeoutMs) { getCardById(language, exactId).getOrThrow() }
                } catch (_: Throwable) {
                }
            }

            // Fallback: try localId variants
            val padded = digits.padStart(3, '0')
            val tries = listOf("$prefix$padded", "$prefix$digits", "$prefix $padded", "$prefix $digits", padded, digits)
            for (tok in tries) {
                val resp = try {
                    kotlinx.coroutines.withTimeout(timeoutMs) { searchCardsByLocalId(language, tok, 1, 1000).getOrThrow() }
                } catch (_: Throwable) {
                    null
                }
                val list = resp?.data.orEmpty()
                if (list.isNotEmpty()) {
                    // Choose best match: same localId and promo set id suffix 'p'
                    val best =
                        list.firstOrNull { card ->
                            val lid = card.localId?.replace(" ", "")?.uppercase()
                            (lid == (prefix.uppercase() + padded) || lid == (prefix.uppercase() + digits.uppercase())) &&
                                (card.set?.id?.endsWith("p", ignoreCase = true) == true)
                        } ?: list.first()
                    return@runCatching best
                }
            }
            null
        }

    override suspend fun searchCardsByNumericReference(
        language: String,
        input: String,
        timeoutMs: Long,
    ): Result<List<TcgdxCard>> =
        runCatching {
            // Accept strictly numeric localId and official count: X/Y
            val m = Regex("""^\s*(\d+)\s*/\s*(\d+)\s*$""").matchEntire(input.trim()) ?: return@runCatching emptyList()
            val localIdNum = m.groupValues[1].toIntOrNull() ?: return@runCatching emptyList()
            val official = m.groupValues[2].toIntOrNull() ?: return@runCatching emptyList()

            // Ensure we have sets; use cached when possible
            val setsResp = getSets(language = language, page = 1, pageSize = 2000).getOrThrow()
            val candidateSets: List<TcgdxSet> =
                setsResp.data.orEmpty().filter { s -> (s.cardCount?.official == official) }

            if (candidateSets.isEmpty()) return@runCatching emptyList()

            // Build direct ids and resolve in parallel with per-call timeout
            val ids: List<String> = candidateSets.map { s -> "${s.id}-${localIdNum}" }
            val results = mutableListOf<TcgdxCard>()

            kotlinx.coroutines.coroutineScope {
                val jobs = ids.map { cid ->
                    async {
                        try {
                            kotlinx.coroutines.withTimeout(timeoutMs) {
                                getCardById(language, cid).getOrThrow()
                            }
                        } catch (_: Throwable) {
                            null
                        }
                    }
                }
                jobs.forEach { d ->
                    d.await()?.let { card ->
                        if (!app.cardium.tcgdex.sdk.model.PocketFilter.isPocketBySeriesOrId(card)) {
                            results.add(card)
                        }
                    }
                }
            }
            results
        }
}

object TcgDex {
    fun from(httpClient: HttpClient = createPlatformHttpClient()): TcgDexClient = TcgDexClientImpl(httpClient)
}
