package app.cardium.tcgdex.sdk

import io.ktor.client.HttpClient
import app.cardium.tcgdex.sdk.http.createPlatformHttpClient
import app.cardium.tcgdex.sdk.model.TcgdxSet
import app.cardium.tcgdex.sdk.model.TcgdxSetResponse
import app.cardium.tcgdex.sdk.model.TcgdxCard
import app.cardium.tcgdex.sdk.model.TcgdxSearchResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.jsonObject
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
    suspend fun getSetById(language: String = "en", setId: String): Result<TcgdxSet>
    suspend fun getCardById(language: String = "en", cardId: String): Result<TcgdxCard>
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
}

@Serializable
data class MinimalCard(
    val id: String,
    val localId: String? = null,
    val name: String,
)

class TcgDexClientImpl(
    private val httpClient: HttpClient,
) : TcgDexClient {
    override suspend fun ping(): Result<Boolean> = Result.success(true)

    override suspend fun getSets(
        language: String,
        page: Int,
        pageSize: Int,
    ): Result<TcgdxSetResponse> =
        runCatching {
            val all: List<TcgdxSet> = httpClient.get("https://api.tcgdex.net/v2/$language/sets").body()
            val start = (page - 1) * pageSize
            val end = minOf(start + pageSize, all.size)
            val pageItems = if (start < all.size) all.subList(start, end) else emptyList()
            TcgdxSetResponse(data = pageItems, total = all.size, page = page, pageSize = pageSize)
        }

    override suspend fun getSetById(language: String, setId: String): Result<TcgdxSet> = runCatching {
        httpClient.get("https://api.tcgdex.net/v2/$language/sets/$setId").body()
    }

    override suspend fun getCardById(language: String, cardId: String): Result<TcgdxCard> = runCatching {
        httpClient.get("https://api.tcgdex.net/v2/$language/cards/$cardId").body()
    }

    override suspend fun searchCardsByName(
        language: String,
        query: String,
        page: Int,
        pageSize: Int,
    ): Result<TcgdxSearchResponse> =
        runCatching {
            val arr: JsonArray = httpClient.get("https://api.tcgdex.net/v2/$language/cards") {
                url { parameters.append("name", query) }
            }.body()
            val items: List<MinimalCard> = arr.mapNotNull { el ->
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
            val cards: List<TcgdxCard> = slice.mapNotNull { minimal ->
                runCatching { httpClient.get("https://api.tcgdex.net/v2/$language/cards/${minimal.id}").body<TcgdxCard>() }.getOrNull()
            }
            TcgdxSearchResponse(data = cards, total = total, page = page, pageSize = pageSize)
        }

    override suspend fun getCardsBySet(
        language: String,
        setId: String,
        page: Int,
        pageSize: Int,
    ): Result<TcgdxSearchResponse> =
        runCatching {
            val arr: JsonArray = httpClient.get("https://api.tcgdex.net/v2/$language/cards") {
                url { parameters.append("set", setId) }
            }.body()
            val items: List<MinimalCard> = arr.mapNotNull { el ->
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
            val cards: List<TcgdxCard> = slice.mapNotNull { minimal ->
                runCatching { httpClient.get("https://api.tcgdex.net/v2/$language/cards/${minimal.id}").body<TcgdxCard>() }.getOrNull()
            }
            TcgdxSearchResponse(data = cards, total = total, page = page, pageSize = pageSize)
        }
}

object TcgDex {
    fun from(httpClient: HttpClient = createPlatformHttpClient()): TcgDexClient = TcgDexClientImpl(httpClient)
}


