package tools

import app.cardium.tcgdex.sdk.TcgDex
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

@Serializable
data class SeriesOut(val id: String, val name: String)

@Serializable
data class SetOut(
    val id: String,
    val name: String,
    val releaseDate: String? = null,
    val logo: String? = null,
    val symbol: String? = null,
    val serieId: String? = null,
    val official: Int? = null,
)

private fun toKotlinTripleQuoted(s: String): String {
    val sanitized = s.replace("$", "\${'$'}")
    return "\"\"\"\n$sanitized\n\"\"\""
}

object GenerateEmbeddedCatalog {
@JvmStatic
fun main(args: Array<String>) {
runBlocking {
    val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    val outBase = System.getProperty("outputDir")
        ?: (System.getenv("TCGDEX_EMBED_OUT") ?: "${System.getProperty("user.dir")}/src/commonMain/resources/tcgdex")
    val outBasePath = Path.of(outBase)
    Files.createDirectories(outBasePath)

    val sdk = TcgDex.from()
    val http = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }
    }
    // Supported languages (extend as needed)
    val languages = listOf("en","fr","de","es","it","pt","ja","ko","zh")

    val seriesMap = linkedMapOf<String, String>()
    val setsMap = linkedMapOf<String, String>()

    // Preload series-with-sets mapping (language-agnostic)
    val seriesWithSets = runCatching { TcgDex.from().getSeriesWithSetsGraphQL().getOrElse { emptyList() } }.getOrElse { emptyList() }
    val setIdToSerieId = mutableMapOf<String, String>()
    val serieIdToName = mutableMapOf<String, String>()
    seriesWithSets.forEach { s ->
        serieIdToName[s.id] = s.name
        s.sets.forEach { ref -> setIdToSerieId[ref.id] = s.id }
    }

    fun extractSerieIdFromSetId(setId: String): String? {
        val letters = setId.takeWhile { it.isLetter() || it == '-' }
        return letters.ifBlank { null }
    }

    for (lang in languages) {
        println("[gen] Generating catalog for lang=$lang …")
        try {
            val setsResp = sdk.getSets(language = lang, page = 1, pageSize = 2000).getOrThrow()
            val baseSets = setsResp.data.orEmpty()
            val langDir = outBasePath.resolve(lang)
            Files.createDirectories(langDir)
            val detailed = mutableListOf<SetOut>()
            for (s in baseSets) {
                val full = sdk.getSetById(language = lang, setId = s.id).getOrThrow()
                val serieId = full.serie?.id ?: setIdToSerieId[s.id] ?: extractSerieIdFromSetId(s.id)
                detailed += SetOut(
                    id = full.id,
                    name = full.name,
                    releaseDate = full.releaseDate,
                    logo = full.logo,
                    symbol = full.symbol,
                    serieId = serieId,
                    official = full.cardCount?.official,
                )
                // Also persist full REST detail JSON for this set for offline enrichment
                try {
                    val raw = http.get("https://api.tcgdex.net/v2/$lang/sets/${full.id}").bodyAsText()
                    val setDir = langDir.resolve("sets")
                    Files.createDirectories(setDir)
                    Files.writeString(
                        setDir.resolve("${full.id}.json"),
                        raw,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
                    )
                } catch (t: Throwable) {
                    System.err.println("[gen] Failed to write detail for ${'$'}{lang}:${'$'}{full.id} - ${'$'}{t.message}")
                }
            }
            val series: List<SeriesOut> = detailed
                .mapNotNull { it.serieId?.let { id -> id to (baseSets.firstOrNull { b -> b.serie?.id == id }?.serie?.name ?: (serieIdToName[id] ?: id)) } }
                .distinctBy { it.first }
                .map { SeriesOut(id = it.first, name = it.second) }

            Files.writeString(
                langDir.resolve("series.json"),
                json.encodeToString(series),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
            )
            Files.writeString(
                langDir.resolve("sets.json"),
                json.encodeToString(detailed),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
            )

            // Accumulate for Kotlin embedding
            seriesMap[lang.lowercase()] = json.encodeToString(series)
            setsMap[lang.lowercase()] = json.encodeToString(detailed)
            println("[gen] Wrote ${series.size} series and ${detailed.size} sets for lang=$lang")
        } catch (t: Throwable) {
            System.err.println("[gen] Skipping lang=$lang due to error: ${t.message}")
        }
    }

    // Also write a Kotlin source with embedded data to keep loading platform-agnostic
    val pkg = "app.cardium.tcgdex.sdk.embedded"
    val targetDir = Path.of(System.getProperty("user.dir"))
        .resolve("src/commonMain/kotlin/${pkg.replace('.', '/')} ".trim())
    Files.createDirectories(targetDir)
    val kt = buildString {
        appendLine("package $pkg")
        appendLine()
        appendLine("object EmbeddedCatalogData {")
        appendLine("  private val seriesByLang: Map<String, String> = mapOf(")
        seriesMap.forEach { (lang, payload) ->
            appendLine("    \"$lang\" to ${toKotlinTripleQuoted(payload)},")
        }
        appendLine("  )")
        appendLine("  private val setsByLang: Map<String, String> = mapOf(")
        setsMap.forEach { (lang, payload) ->
            appendLine("    \"$lang\" to ${toKotlinTripleQuoted(payload)},")
        }
        appendLine("  )")
        appendLine("  fun seriesJson(lang: String): String? = seriesByLang[lang.lowercase()]")
        appendLine("  fun setsJson(lang: String): String? = setsByLang[lang.lowercase()]")
        appendLine("}")
        appendLine()
    }
    Files.writeString(
        targetDir.resolve("EmbeddedCatalogData.kt"),
        kt,
        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
    )
}
}
}


