package app.cardium.tcgdex.sdk.model

import app.cardium.tcgdex.sdk.TcgDexClient
import kotlinx.datetime.Clock

/**
 * Registry for identifying Pocket (tcgp) set ids per language.
 * Fetches via GraphQL series->sets when available, falling back to REST sets filtering by serie.id=="tcgp".
 * Results are cached in-memory for 24 hours per language.
 */
object PocketRegistry {
    private data class Cache(val ids: Set<String>, val fetchedAtMs: Long)
    private val cacheByLanguage: MutableMap<String, Cache> = mutableMapOf()
    private const val TTL_MS: Long = 24L * 60L * 60L * 1000L

    suspend fun getTcgpSetIds(client: TcgDexClient, language: String): Set<String> {
        val lang = language.lowercase()
        val now = Clock.System.now().toEpochMilliseconds()
        val cached = cacheByLanguage[lang]
        if (cached != null && (now - cached.fetchedAtMs) < TTL_MS) return cached.ids

        // Prefer GraphQL series -> sets
        val gql = runCatching { client.getSeriesWithSetsGraphQL().getOrNull() }.getOrNull().orEmpty()
        val fromGql: Set<String>? = gql.firstOrNull { it.id.equals("tcgp", ignoreCase = true) }
            ?.sets
            ?.map { it.id.lowercase() }
            ?.toSet()

        val ids: Set<String> = if (fromGql != null && fromGql.isNotEmpty()) fromGql else run {
            // Fallback to REST sets by language, filter serie.id == tcgp
            val sets = runCatching { client.getSets(language = lang, page = 1, pageSize = 2000).getOrNull()?.data.orEmpty() }.getOrElse { emptyList() }
            sets.filter { it.serie?.id?.equals("tcgp", ignoreCase = true) == true }
                .map { it.id.lowercase() }
                .toSet()
        }

        val stored = ids.ifEmpty { emptySet() }
        cacheByLanguage[lang] = Cache(stored, now)
        return stored
    }

    /** Clear cached tcgp set ids (all languages or a specific language). */
    fun clear(language: String? = null) {
        if (language == null) {
            cacheByLanguage.clear()
        } else {
            cacheByLanguage.remove(language.lowercase())
        }
    }
}


