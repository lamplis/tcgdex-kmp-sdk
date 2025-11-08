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
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.nio.file.FileVisitOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class SeriesOut(val id: String, val name: String, val releaseDate: String? = null)

@Serializable
data class SetOut(
    val id: String,
    val name: String,
    val releaseDate: String? = null,
    val logo: String? = null,
    val symbol: String? = null,
    val serieId: String? = null,
    val official: Int? = null,
    val total: Int? = null,
)

@Serializable
data class CardDetailOut(
    val id: String,            // "sv01-001"
    val number: String,        // "001"
    val name: String,          // localized name
    val illustrator: String?,  // may be null
    val imageBase: String,     // "https://assets.tcgdex.net/<lang>/<serieId>/<setId>/<num>"
)

@Serializable
data class SetDetailsOut(
    val id: String,
    val serieId: String,
    val name: String,
    val releaseDate: String? = null,
    val cardCount: Int? = null,
    val logo: String? = null,
    val symbol: String? = null,
    val cards: List<CardDetailOut> = emptyList(),
)

@Serializable
data class IllustratorSetCount(val setId: String, val count: Int)

@Serializable
data class IllustratorIndexEntry(
    val name: String,
    val total: Int,
    val sets: List<IllustratorSetCount>,
    // last3 card IDs like "sv01-001" (latest set of latest serie, top 3 highest local numbers)
    val last3: List<String> = emptyList(),
)

@Serializable
data class IllustratorIndexPayload(val version: Int = 1, val language: String, val generatedAt: Long, val artists: List<IllustratorIndexEntry>)

private data class ISet(val setId: String, var count: Int)
private data class Acc(var total: Int, val perSet: MutableMap<String, ISet>)

private data class CardRef(
    val setId: String,
    val number: String?,
    val seriesRelease: String?,
    val setRelease: String?,
    val numSort: Int,
)

// Local TS scan data (language-agnostic)
private data class SeriesScan(
    val id: String,
    val folderName: String,
    val names: Map<String, String>,
    val earliestReleaseDate: String?,
    val latestReleaseDate: String?
)
private data class SetScan(
    val id: String,
    val seriesId: String,
    val fileBase: String,
    val names: Map<String, String>,
    val releaseDate: String?,
    val official: Int?,
    val total: Int?,
)
private data class CardParsed(
    val number: String,
    val illustrator: String?,
    val names: Map<String, String>,
)
private data class LocalScan(
    val series: List<SeriesScan>,
    val sets: List<SetScan>,
    val illustrators: MutableMap<String, Acc>,
    // full list of CardRef per artist (no trimming) to compute last3 properly
    val artistCards: Map<String, List<CardRef>>,
    // discovery helpers to avoid re-scanning filesystem
    val setIdToCardsFolder: Map<String, Path>,
    val setIdToCards: Map<String, List<CardParsed>>,
)

// ===== Illustrator collections (language-agnostic) =====
@Serializable
data class IllustratorCardOut(
    val id: String,           // TCGdx id like "sv03.5-001"
    val number: String,       // local number string (may contain suffix)
    val imageUrl: String? = null,
)

@Serializable
data class IllustratorSetOut(
    val id: String,
    val name: String,
    val releaseDate: String? = null,
    val cards: List<IllustratorCardOut> = emptyList(),
)

@Serializable
data class IllustratorSerieOut(
    val id: String,
    val name: String,
    // Oldest set release date in the serie
    val releaseDate: String? = null,
    val sets: List<IllustratorSetOut> = emptyList(),
)

@Serializable
data class IllustratorCollectionOut(
    val illustrator: String,
    val series: List<IllustratorSerieOut> = emptyList(),
)

private fun toKotlinTripleQuoted(s: String): String {
    val sanitized = s.replace("$", "\${'$'}")
    // Return a string that contains Kotlin triple quotes surrounding the payload
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

    // Setup log file to observe progress post-run
    val logsDir = Path.of(System.getProperty("user.dir")).resolve("build/logs")
    Files.createDirectories(logsDir)
    val ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
    val logFile = logsDir.resolve("generate-embedded-catalog-" + ts + ".log")
    fun log(msg: String) {
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val line = "[gen] " + time + " " + msg
        println(line)
        Files.writeString(
            logFile,
            line + "\n",
            StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE,
        )
    }

    val sdk = TcgDex.from()
    val http = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }
    }
    // Supported languages (extend as needed); can be overridden via -Dlangs="en,fr"
    val defaultLangs = listOf("en","fr","de","es","it","pt","ja","ko","zh")
    val langsProp = System.getProperty("langs")?.trim()?.lowercase()
    val languages =
        langsProp
            ?.split(',')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.ifEmpty { null }
            ?: defaultLangs

    // Optional tuning flags for faster debug runs
    val limitSets: Int = System.getProperty("limitSets")?.toIntOrNull() ?: 0
    val progressEvery: Int = System.getProperty("progressEvery")?.toIntOrNull() ?: 10
    val skipDetails: Boolean = (System.getProperty("skipDetails") ?: "").equals("true", ignoreCase = true)
    val verbose: Boolean = (System.getProperty("verbose") ?: "").equals("true", ignoreCase = true)

    // Local DB flags and mode
    val localDbPathStr: String? = System.getProperty("localDbPath") ?: System.getenv("TCGDEX_LOCAL_DB")
    val offlineOnly: Boolean = (System.getProperty("offlineOnly") ?: "").equals("true", ignoreCase = true)
    val localDbPath: Path? = localDbPathStr?.let { Path.of(it) }
    val useLocal: Boolean = localDbPath != null && Files.isDirectory(localDbPath)
    val seriesFilter: String? = System.getProperty("seriesFilter")?.trim()?.takeIf { it.isNotEmpty() }

    log("Output directory: " + outBase)
    log("Log file: " + logFile)
    log("Languages: " + languages)
    log("limitSets=" + limitSets + ", progressEvery=" + progressEvery + ", skipDetails=" + skipDetails + ", verbose=" + verbose)
    log("localDbPath=" + (localDbPathStr ?: "<none>") + ", offlineOnly=" + offlineOnly + ", useLocal=" + useLocal)
    log("MODE: " + (if (useLocal) "LOCAL_TS" else "REMOTE_API"))

    if (offlineOnly && !useLocal) {
        log("[x] offlineOnly=true but localDbPath is invalid or missing; aborting")
        return@runBlocking
    }

    val seriesMap = linkedMapOf<String, String>()
    val setsMap = linkedMapOf<String, String>()
    // Old: language-specific index payloads (kept for backward compat during transition)
    val illustratorsMap = linkedMapOf<String, String>()
    // New: language-agnostic illustrators assets
    val illustratorFiles: MutableMap<String, String> = linkedMapOf() // key: sanitized artist name -> json content
    var illustratorsIndexGlobal: String? = null

    // Preload series-with-sets mapping (language-agnostic)
    val seriesWithSets = runCatching { TcgDex.from().getSeriesWithSetsGraphQL().getOrElse { emptyList() } }
        .onFailure { log("[warn] series-with-sets GraphQL failed: ${'$'}{it.message}") }
        .getOrElse { emptyList() }
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

    fun safeReadJson(path: Path): JsonElement? =
        runCatching { json.parseToJsonElement(Files.readString(path)) }.getOrNull()

    fun candidateDir(vararg paths: Path): Path? = paths.firstOrNull { Files.isDirectory(it) }

    fun findFirstFile(root: Path, fileName: String, maxDepth: Int = 5): Path? =
        runCatching {
            Files.walk(root, maxDepth, FileVisitOption.FOLLOW_LINKS)
                .filter { Files.isRegularFile(it) && it.fileName.toString().equals(fileName, ignoreCase = true) }
                .findFirst()
                .orElse(null)
        }.getOrNull()

    fun findFirstDir(root: Path, dirName: String, maxDepth: Int = 5): Path? =
        runCatching {
            Files.walk(root, maxDepth, FileVisitOption.FOLLOW_LINKS)
                .filter { Files.isDirectory(it) && it.fileName.toString().equals(dirName, ignoreCase = true) }
                .findFirst()
                .orElse(null)
        }.getOrNull()

    fun extractNameForLang(ts: String, lang: String): String? {
        val key = Regex.escape(lang)
        val r1 = Regex("name\\s*:\\s*\\{[\\s\\S]*?$key\\s*:\\s*\\\"([^\\\"]+)\\\"", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        val r2 = Regex("name\\s*:\\s*\\{[\\s\\S]*?$key\\s*:\\s*'([^']+)'", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        return r1.find(ts)?.groupValues?.getOrNull(1)
            ?: r2.find(ts)?.groupValues?.getOrNull(1)
    }

    fun extractStringField(ts: String, field: String): String? {
        val patterns = listOf(
            Regex("\\b$field\\s*:\\s*\\\"([^\\\"]+)\\\"", RegexOption.IGNORE_CASE),
            Regex("\\b$field\\s*:\\s*'([^']+)'", RegexOption.IGNORE_CASE),
            Regex("(?m)^\\s*$field\\s*:\\s*\\\"([^\\\"]+)\\\"", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)),
            Regex("(?m)^\\s*$field\\s*:\\s*'([^']+)'", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)),
            Regex("$field\\s*:\\s*\\\"([^\\\"]+)\\\"", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)),
            Regex("$field\\s*:\\s*'([^']+)'", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)),
        )
        for (p in patterns) {
            val m = p.find(ts)
            if (m != null) return m.groupValues.getOrNull(1)
        }
        return null
    }

    fun extractIntFieldInObject(ts: String, obj: String, sub: String): Int? {
        val r = Regex("$obj\\s*:\\s*\\{[\\s\\S]*?$sub\\s*:\\s*(\\d+)", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        return r.find(ts)?.groupValues?.getOrNull(1)?.toIntOrNull()
    }

    fun isTcgp(id: String?): Boolean = id?.equals("tcgp", ignoreCase = true) == true

    fun extractNameMap(ts: String): Map<String, String> {
        val obj = Regex("name\\s*:\\s*\\{([\\s\\S]*?)\\}", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .find(ts)?.groupValues?.getOrNull(1) ?: return emptyMap()
        val rx = Regex("([A-Za-z-]+|\\\"[A-Za-z-]+\\\")\\s*:\\s*(\\\"([^\\\"]+)\\\"|'([^']+)')")
        val out = mutableMapOf<String, String>()
        rx.findAll(obj).forEach { m ->
            val rawKey = m.groupValues[1]
            val key = rawKey.trim('"')
            val val1 = m.groupValues.getOrNull(3)
            val val2 = m.groupValues.getOrNull(4)
            val value = if (!val1.isNullOrEmpty()) val1 else val2
            if (!value.isNullOrEmpty()) out[key] = value
        }
        return out
    }

    fun deriveSerieIdFromSetId(setId: String): String {
        val letters = setId.takeWhile { it.isLetter() }
        return when {
            letters.equals("sv", ignoreCase = true) -> "sv"
            letters.isNotEmpty() && letters.uppercase() == letters -> "tcgp"
            else -> letters.lowercase()
        }
    }
    fun isTcgpSetId(setId: String): Boolean = deriveSerieIdFromSetId(setId) == "tcgp"

    fun scanLocalTsDatabase(dataRoot: Path, seriesFilter: String?, limitSets: Int): LocalScan {
        val t0 = System.nanoTime()
        val seriesFiles = mutableListOf<Path>()
        Files.newDirectoryStream(dataRoot).use { ds ->
            for (p in ds) if (Files.isRegularFile(p) && p.toString().endsWith(".ts")) seriesFiles.add(p)
        }
        val filteredSeriesFiles = seriesFiles.filter { sf ->
            val base = sf.fileName.toString().removeSuffix(".ts")
            seriesFilter == null || base.contains(seriesFilter, ignoreCase = true)
        }
        println("[gen] scanning TS DB under " + dataRoot + " seriesFiles=" + seriesFiles.size + " filtered=" + filteredSeriesFiles.size)
        val seriesScans = mutableListOf<SeriesScan>()
        val setScans = mutableListOf<SetScan>()
        val illustratorsAcc: MutableMap<String, Acc> = linkedMapOf()
        val artistCardsAcc: MutableMap<String, MutableList<CardRef>> = linkedMapOf()
        val setIdToCardsFolder: MutableMap<String, Path> = linkedMapOf()
        val setIdToCards: MutableMap<String, MutableList<CardParsed>> = linkedMapOf()
        var processedSets = 0
        var processedCards = 0
        for (sf in filteredSeriesFiles) {
            val sContent = runCatching { Files.readString(sf) }.getOrNull() ?: continue
            val serieId = extractStringField(sContent, "id") ?: continue
            val names = extractNameMap(sContent)
            val seriesFolder = dataRoot.resolve(sf.fileName.toString().removeSuffix(".ts"))
            if (!Files.isDirectory(seriesFolder)) continue
            var earliest: String? = null
            var latest: String? = null
            val setFiles = mutableListOf<Path>()
            Files.newDirectoryStream(seriesFolder).use { ds ->
                for (p in ds) if (Files.isRegularFile(p) && p.toString().endsWith(".ts")) setFiles.add(p)
            }
            val totalSets = if (limitSets > 0) kotlin.math.min(limitSets - processedSets, setFiles.size) else setFiles.size
            println("[gen] serie=" + serieId + " sets=" + setFiles.size + " processing=" + (if (limitSets > 0) totalSets else setFiles.size))
            for (setTs in setFiles.take(if (limitSets > 0) totalSets else setFiles.size)) {
                val c = runCatching { Files.readString(setTs) }.getOrNull() ?: continue
                val setId = extractStringField(c, "id") ?: continue
                val setNames = extractNameMap(c)
                val releaseDate = extractStringField(c, "releaseDate")
                if (releaseDate != null) {
                    earliest = listOfNotNull(earliest, releaseDate).minOrNull()
                    latest = listOfNotNull(latest, releaseDate).maxOrNull()
                }
                val official = extractIntFieldInObject(c, "cardCount", "official")
                val total = extractIntFieldInObject(c, "cardCount", "total") ?: official
                setScans += SetScan(
                    id = setId,
                    seriesId = serieId,
                    fileBase = setTs.fileName.toString().removeSuffix(".ts"),
                    names = setNames,
                    releaseDate = releaseDate,
                    official = official,
                    total = total,
                )
                // Resolve cards folder with multiple candidates: fileBase, setId, english/fr names
                val fileBase = setTs.fileName.toString().removeSuffix(".ts")
                val folderCandidates = listOfNotNull(
                    fileBase,
                    setId,
                    setNames["en"],
                    setNames["fr"],
                ).distinct()
                val cardsFolder = folderCandidates
                    .asSequence()
                    .map { nm -> seriesFolder.resolve(nm) }
                    .firstOrNull { Files.isDirectory(it) }
                if (cardsFolder != null) {
                    println("[gen] set=" + setId + " cardsFolder=" + cardsFolder.fileName)
                    setIdToCardsFolder[setId] = cardsFolder
                    val parsedList = setIdToCards.getOrPut(setId) { mutableListOf() }
                    Files.newDirectoryStream(cardsFolder).use { cds ->
                        for (cp in cds) {
                            if (!Files.isRegularFile(cp) || !cp.toString().endsWith(".ts")) continue
                            val cardTs = runCatching { Files.readString(cp) }.getOrNull() ?: continue
                            val illustrator = extractStringField(cardTs, "illustrator")?.trim()?.replace("\\s+".toRegex(), " ")
                            if (!illustrator.isNullOrEmpty()) {
                                val acc = illustratorsAcc.getOrPut(illustrator) { Acc(0, linkedMapOf()) }
                                acc.total += 1
                                val entry = acc.perSet.getOrPut(setId) { ISet(setId = setId, count = 0) }
                                entry.count += 1
                                // Track top-3 latest cards per illustrator
                                val number = extractStringField(cardTs, "number") ?: cp.fileName.toString().removeSuffix(".ts")
                                val numSort = number?.takeWhile { it.isDigit() }?.toIntOrNull() ?: Int.MIN_VALUE
                                val cref = CardRef(
                                    setId = setId,
                                    number = number,
                                    seriesRelease = latest ?: releaseDate,
                                    setRelease = releaseDate,
                                    numSort = numSort,
                                )
                                val list = artistCardsAcc.getOrPut(illustrator) { mutableListOf() }
                                list.add(cref)
                            }
                            // Store multilingual card info (number, illustrator, names)
                            val numberForCard = extractStringField(cardTs, "number") ?: cp.fileName.toString().removeSuffix(".ts")
                            val namesMap = extractNameMap(cardTs)
                            parsedList.add(CardParsed(number = numberForCard, illustrator = illustrator, names = namesMap))
                            processedCards++
                            if (processedCards % kotlin.math.max(1, progressEvery) == 0) {
                                println("[gen] cards parsed=" + processedCards + " (set=" + setId + ")")
                            }
                        }
                    }
                    println("[gen] set=" + setId + " parsedCards=" + parsedList.size)
                } else {
                    println("[gen] set=" + setId + " cardsFolder=<none>")
                }
                processedSets++
                if (processedSets % kotlin.math.max(1, progressEvery) == 0) {
                    println("[gen] sets processed=" + processedSets)
                }
                if (limitSets > 0 && processedSets >= limitSets) break
            }
            seriesScans += SeriesScan(id = serieId, folderName = seriesFolder.fileName.toString(), names = names, earliestReleaseDate = earliest, latestReleaseDate = latest)
            if (limitSets > 0 && processedSets >= limitSets) break
        }
        val t1 = System.nanoTime()
        println("[gen] scanLocalTsDatabase done in " + ((t1 - t0) / 1_000_000) + " ms; series=" + seriesScans.size + " sets=" + setScans.size + " cardsParsed=" + processedCards)
        return LocalScan(
            series = seriesScans,
            sets = setScans,
            illustrators = illustratorsAcc,
            artistCards = artistCardsAcc.mapValues { it.value.toList() },
            setIdToCardsFolder = setIdToCardsFolder.toMap(),
            setIdToCards = setIdToCards.mapValues { it.value.toList() },
        )
    }

    fun writeOutputsForLangFromScan(
        lang: String,
        scan: LocalScan,
        outBasePath: Path,
        seriesMap: MutableMap<String, String>,
        setsMap: MutableMap<String, String>,
        illustratorsMap: MutableMap<String, String>,
    ) {
        fun releaseKey(date: String?): Int = date?.replace("-", "")?.toIntOrNull() ?: 0
        fun parseLocalNumber(num: String?): Int = num?.takeWhile { it.isDigit() }?.toIntOrNull() ?: Int.MAX_VALUE
        fun numPath(num: String): String = num.takeWhile { it.isDigit() }.ifBlank { num }

        val filteredSeries = scan.series.filter { !isTcgp(it.id) }
        val filteredSets = scan.sets.filter { !isTcgp(it.seriesId) && !isTcgpSetId(it.id) }
        val seriesOut = filteredSeries.map { s ->
            val name = s.names[lang] ?: s.names["en"] ?: s.folderName
            SeriesOut(id = s.id, name = name, releaseDate = s.earliestReleaseDate)
        }
        val setsOut = filteredSets.map { s ->
            val name = s.names[lang] ?: s.names["en"] ?: s.fileBase
            val assetBase = "https://assets.tcgdex.net/" + lang.lowercase() + "/" + s.seriesId + "/" + s.id
            SetOut(
                id = s.id,
                name = name,
                releaseDate = s.releaseDate,
                logo = assetBase + "/logo",
                symbol = assetBase + "/symbol",
                serieId = s.seriesId,
                official = s.official,
                total = s.total,
            )
        }
        // Phase B: emit detailed set files per language in a single pass using cached scan data
        val langDir = outBasePath.resolve(lang)
        val langSetsDir = langDir.resolve("sets")
        Files.createDirectories(langSetsDir)
        // Cleanup any legacy tcgp set files in this language output
        runCatching {
            if (Files.isDirectory(langSetsDir)) {
                Files.newDirectoryStream(langSetsDir).use { ds ->
                    for (p in ds) {
                        if (Files.isRegularFile(p) && p.fileName.toString().endsWith(".json")) {
                            val sid = p.fileName.toString().removeSuffix(".json")
                            if (isTcgpSetId(sid)) {
                                runCatching { Files.deleteIfExists(p) }
                            }
                        }
                    }
                }
            }
        }
        val tWrite0 = System.nanoTime()
        // Order: series by earliest release asc, sets per series by release asc then id
        val orderedSeries = filteredSeries.sortedBy { releaseKey(it.earliestReleaseDate) }
        var writtenSets = 0
        var writtenCards = 0
        for (serie in orderedSeries) {
            val setsOfSerie = filteredSets
                .asSequence()
                .filter { it.seriesId == serie.id }
                .sortedWith(compareBy<SetScan>({ releaseKey(it.releaseDate) }, { it.id }))
                .toList()
            for (s in setsOfSerie) {
                val cardsParsed = scan.setIdToCards[s.id].orEmpty()
                val setName = s.names[lang] ?: s.names["en"] ?: s.fileBase
                val cardsForLang = cardsParsed
                    .map { cp ->
                        val nm = cp.names[lang] ?: cp.names["en"] ?: ("#" + cp.number)
                        CardDetailOut(
                            id = s.id + "-" + cp.number,
                            number = cp.number,
                            name = nm,
                            illustrator = cp.illustrator,
                            imageBase = "https://assets.tcgdex.net/" + lang.lowercase() + "/" + s.seriesId + "/" + s.id + "/" + numPath(cp.number),
                        )
                    }
                    .sortedBy { parseLocalNumber(it.number) }
                val details = SetDetailsOut(
                    id = s.id,
                    serieId = s.seriesId,
                    name = setName,
                    releaseDate = s.releaseDate,
                    cardCount = s.total ?: s.official,
                    logo = "https://assets.tcgdex.net/" + lang.lowercase() + "/" + s.seriesId + "/" + s.id + "/logo",
                    symbol = "https://assets.tcgdex.net/" + lang.lowercase() + "/" + s.seriesId + "/" + s.id + "/symbol",
                    cards = cardsForLang,
                )
                Files.writeString(
                    langSetsDir.resolve(s.id + ".json"),
                    json.encodeToString(details),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
                )
                writtenSets++
                writtenCards += cardsForLang.size
                if (writtenSets % kotlin.math.max(1, progressEvery) == 0) {
                    println("[gen] lang=" + lang + " wrote sets=" + writtenSets + " cards=" + writtenCards)
                }
            }
        }
        val tWrite1 = System.nanoTime()
        println("[gen] lang=" + lang + " phaseB write: sets=" + writtenSets + " cards=" + writtenCards + " in " + ((tWrite1 - tWrite0) / 1_000_000) + " ms")
        val artists = scan.illustrators.entries
            .sortedByDescending { it.value.total }
            .map { (artist, acc) ->
                val setsList = acc.perSet.values
                    .filter { !isTcgpSetId(it.setId) }
                    .map { IllustratorSetCount(setId = it.setId, count = it.count) }
                    .sortedByDescending { it.count }

                // Compute last3 across ALL series: walk series newest->older, and within each series walk sets newest->older until 3 cards are collected
                val allRefs = scan.artistCards[artist]
                    .orEmpty()
                    .filter { deriveSerieIdFromSetId(it.setId) != "tcgp" }
                    .filter { !it.number.isNullOrBlank() }
                // Series ordered by seriesRelease desc (empty last)
                val seriesOrder: List<String> =
                    allRefs
                        .map { it.seriesRelease ?: "" }
                        .distinct()
                        .sortedByDescending { it }
                val picked = LinkedHashSet<String>(3)
                for (serieKey in seriesOrder) {
                    if (picked.size >= 3) break
                    val inSerie = allRefs.filter { (it.seriesRelease ?: "") == serieKey }
                    if (inSerie.isEmpty()) continue
                    // Order sets by setRelease desc then setId desc (stable)
                    val setOrder =
                        inSerie
                            .groupBy { it.setId }
                            .mapValues { (_, v) -> v.maxOfOrNull { it.setRelease ?: "" } ?: "" }
                            .toList()
                            .sortedWith(compareByDescending<Pair<String, String>> { it.second }.thenByDescending { it.first })
                            .map { it.first }
                    for (sid in setOrder) {
                        if (picked.size >= 3) break
                        inSerie
                            .asSequence()
                            .filter { it.setId == sid }
                            .filter { !it.number.isNullOrBlank() }
                            .sortedByDescending { it.numSort }
                            .forEach { r ->
                                if (picked.size < 3) picked += (r.setId + "-" + r.number)
                            }
                    }
                }
                val last3Ids = picked.toList()
                if (last3Ids.size < 3) println("[gen] warn: last3 shortfall artist=\"" + artist + "\" picked=" + last3Ids.size)

                IllustratorIndexEntry(name = artist, total = acc.total, sets = setsList, last3 = last3Ids)
            }
        val payload = IllustratorIndexPayload(language = lang.lowercase(), generatedAt = System.currentTimeMillis(), artists = artists)

        Files.createDirectories(langDir)
        Files.writeString(langDir.resolve("series.json"), json.encodeToString(seriesOut), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)
        Files.writeString(langDir.resolve("sets.json"), json.encodeToString(setsOut), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)
        // Do not generate per-language illustrators-index.json (language-agnostic index is written under /illustrators)
        runCatching { Files.deleteIfExists(langDir.resolve("illustrators-index.json")) }

        seriesMap[lang.lowercase()] = json.encodeToString(seriesOut)
        setsMap[lang.lowercase()] = json.encodeToString(setsOut)
        illustratorsMap[lang.lowercase()] = json.encodeToString(payload)
        println("[gen] lang=" + lang + " wrote series=" + seriesOut.size + " sets=" + setsOut.size + " artists=" + artists.size)

        // Generate language-agnostic illustrator collections and global index once (first processed language)
        if (illustratorsIndexGlobal == null) {
            // Scope subfolder under illustrators (default: "global"), artists are nested under "<scope>/artists/"
            val illustratorsScope = System.getProperty("illustratorsScope")
                ?: System.getenv("TCGDEX_ILLUSTRATORS_SCOPE")
                ?: "global"
            val illustratorsBaseDir = Path.of(System.getProperty("user.dir")).resolve("src/commonMain/resources/illustrators")
            val scopeDir = illustratorsBaseDir.resolve(illustratorsScope)
            val artistsDir = scopeDir.resolve("artists")
            Files.createDirectories(artistsDir)
            // Cleanup any legacy root-level artist JSONs in base illustrators folder (keep any index files)
            runCatching {
                Files.newDirectoryStream(illustratorsBaseDir).use { ds ->
                    for (p in ds) {
                        if (Files.isRegularFile(p)) {
                            val name = p.fileName.toString()
                            if (name.endsWith(".json") && !name.equals("illustrators-index.json", ignoreCase = true)) {
                                runCatching { Files.deleteIfExists(p) }
                            }
                        }
                    }
                }
            }

            fun sanitize(name: String): String = name
                .replace("\\s+".toRegex(), "_")
                .replace("[^A-Za-z0-9_\\-]".toRegex(), "")
                .take(200)
                .ifBlank { "unknown" }

            // Build per-artist collections from scan
            val setIdToScan = scan.sets.associateBy { it.id }
            val serieIdToScan = scan.series.associateBy { it.id }

            // Helper to sort sets and cards
            fun releaseKey(date: String?): Int = date?.replace("-", "")?.toIntOrNull() ?: 0
            fun parseLocalNumber(num: String?): Int = num?.takeWhile { it.isDigit() }?.toIntOrNull() ?: Int.MIN_VALUE
            fun imageUrlFor(langCode: String, serieId: String, setId: String, number: String): String =
                "https://assets.tcgdex.net/" + langCode + "/" + serieId + "/" + setId + "/" + (number.takeWhile { it.isDigit() }.ifBlank { number }) + "/low.png"

            val globalIndexArtists = mutableListOf<IllustratorIndexEntry>()

            // Prepare counts for index from previously computed artists
            val indexCounts = scan.illustrators.mapValues { acc ->
                val perSet = acc.value.perSet.values
                    .filter { !isTcgpSetId(it.setId) }
                    .map { IllustratorSetCount(setId = it.setId, count = it.count) }
                    .sortedByDescending { it.count }
                acc.value.total to perSet
            }

            for ((artist, counts) in indexCounts) {
                val (total, perSet) = counts
                // Compute last3 latest cards across all series/sets for this artist (as string IDs "setId-number")
                val allRefs = scan.artistCards[artist]
                    .orEmpty()
                    .filter { deriveSerieIdFromSetId(it.setId) != "tcgp" }
                    .filter { !it.number.isNullOrBlank() }
                val seriesOrder: List<String> =
                    allRefs
                        .map { it.seriesRelease ?: "" }
                        .distinct()
                        .sortedByDescending { it }
                val picked = LinkedHashSet<String>(3)
                for (serieKey in seriesOrder) {
                    if (picked.size >= 3) break
                    val inSerie = allRefs.filter { (it.seriesRelease ?: "") == serieKey }
                    if (inSerie.isEmpty()) continue
                    val setOrder =
                        inSerie
                            .groupBy { it.setId }
                            .mapValues { (_, v) -> v.maxOfOrNull { it.setRelease ?: "" } ?: "" }
                            .toList()
                            .sortedWith(compareByDescending<Pair<String, String>> { it.second }.thenByDescending { it.first })
                            .map { it.first }
                    for (sid in setOrder) {
                        if (picked.size >= 3) break
                        inSerie
                            .asSequence()
                            .filter { it.setId == sid }
                            .filter { !it.number.isNullOrBlank() }
                            .sortedByDescending { it.numSort }
                            .forEach { r ->
                                if (picked.size < 3) picked += (r.setId + "-" + r.number)
                            }
                    }
                }
                val last3Ids = picked.toList()
                globalIndexArtists += IllustratorIndexEntry(name = artist, total = total, sets = perSet, last3 = last3Ids)

                // Build collection JSON for this artist
                val bySerie: MutableMap<String, MutableList<IllustratorSetOut>> = linkedMapOf()
                // For each set that this artist contributed to, gather cards
                val setsForArtist = perSet.map { it.setId }
                for (setId in setsForArtist) {
                    val setScan = setIdToScan[setId] ?: continue
                    val serieId = setScan.seriesId
                    val serieScan = serieIdToScan[serieId]
                    val setName = setScan.names[lang] ?: setScan.names["en"] ?: setId
                    val cardsParsed = scan.setIdToCards[setId].orEmpty()
                        .filter { it.illustrator?.trim()?.replace("\\s+".toRegex(), " ") == artist }
                        .map { cp ->
                            val num = cp.number
                            IllustratorCardOut(
                                id = setId + "-" + num,
                                number = num,
                                imageUrl = null, // language-agnostic; URL can be resolved at runtime when needed
                            )
                        }
                        .sortedByDescending { parseLocalNumber(it.number) }
                    if (cardsParsed.isEmpty()) continue
                    val setOut = IllustratorSetOut(
                        id = setId,
                        name = setName,
                        releaseDate = setScan.releaseDate,
                        cards = cardsParsed,
                    )
                    bySerie.getOrPut(serieId) { mutableListOf() }.add(setOut)
                }
                // Build ordered series list: newest sets first within each serie; series order newest first by earliestReleaseDate
                val serieEntries = bySerie.entries.mapNotNull { (serieId, setsList) ->
                    val serieScan = serieIdToScan[serieId]
                    val serieName = serieScan?.names?.get(lang) ?: serieScan?.names?.get("en") ?: serieId
                    val serieRelease = serieScan?.earliestReleaseDate
                    val orderedSets = setsList.sortedWith(compareByDescending<IllustratorSetOut> { releaseKey(it.releaseDate) }.thenByDescending { it.id })
                    IllustratorSerieOut(
                        id = serieId,
                        name = serieName,
                        releaseDate = serieRelease,
                        sets = orderedSets,
                    )
                }.sortedWith(compareByDescending<IllustratorSerieOut> { releaseKey(it.releaseDate) }.thenByDescending { it.id })

                val coll = IllustratorCollectionOut(illustrator = artist, series = serieEntries)
                val payload = json.encodeToString(coll)
                val fileName = sanitize(artist) + ".json"
                Files.writeString(
                    artistsDir.resolve(fileName),
                    payload,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
                )
                // Use scoped path as the logical key for embedded access
                illustratorFiles[illustratorsScope + "/artists/" + fileName] = payload
            }

            // Write global illustrators-index.json (language-agnostic) alongside artist files
            val globalIndex = mapOf(
                "version" to JsonPrimitive(1),
                "generatedAt" to JsonPrimitive(System.currentTimeMillis()),
                "artists" to Json.parseToJsonElement(json.encodeToString(globalIndexArtists)),
            )
            val globalIndexJson = Json.encodeToString(JsonObject(globalIndex))
            Files.writeString(
                scopeDir.resolve("illustrators-index.json"),
                globalIndexJson,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
            )
            illustratorsIndexGlobal = globalIndexJson
            println("[gen] wrote global illustrators index and collections: artists=" + globalIndexArtists.size)
        }
    }

    var totalSetsAll = 0
    // Cache a single local TS DB scan for all languages
    var cachedLocalScan: LocalScan? = null
    for (lang in languages) {
        log("Generating catalog for lang=" + lang + " …")
        if (useLocal) {
            try {
                if (cachedLocalScan == null) {
                    val dataRoot = listOf(
                        localDbPath!!.resolve("data"),
                        localDbPath
                    ).firstOrNull { Files.isDirectory(it) } ?: localDbPath
                    log("lang=" + lang + ": single-pass scan under " + dataRoot)
                    val tScan0 = System.nanoTime()
                    cachedLocalScan = scanLocalTsDatabase(dataRoot, seriesFilter, limitSets)
                    val tScan1 = System.nanoTime()
                    log("scan completed in " + ((tScan1 - tScan0) / 1_000_000) + " ms")
                }
                val tLang0 = System.nanoTime()
                writeOutputsForLangFromScan(
                    lang = lang,
                    scan = cachedLocalScan!!,
                    outBasePath = outBasePath,
                    seriesMap = seriesMap,
                    setsMap = setsMap,
                    illustratorsMap = illustratorsMap,
                )
                val tLang1 = System.nanoTime()
                log("lang=" + lang + ": wrote series=" + (Json { }.decodeFromString<List<SeriesOut>>(seriesMap[lang.lowercase()]!!).size) +
                        " sets=" + (Json { }.decodeFromString<List<SetOut>>(setsMap[lang.lowercase()]!!).size) +
                        " artists=" + (Json { }.decodeFromString<IllustratorIndexPayload>(illustratorsMap[lang.lowercase()]!!).artists.size) +
                        " in " + ((tLang1 - tLang0) / 1_000_000) + " ms")
                continue
            } catch (t: Throwable) {
                log("[x] Local generation failed for lang=" + lang + ": " + (t.message ?: "unknown"))
                if (offlineOnly) continue else log("[~] Falling back to remote for lang=" + lang)
            }
            try {
                val candidateLangRoots = listOf(
                    localDbPath!!.resolve("tcgdex").resolve(lang),
                    localDbPath.resolve("data").resolve("tcgdex").resolve(lang),
                    localDbPath.resolve("data").resolve(lang),
                    localDbPath.resolve(lang),
                )
                val langRoot = candidateLangRoots.firstOrNull { Files.isDirectory(it) } ?: localDbPath!!.resolve(lang)
                log("lang=" + lang + ": using langRoot=" + langRoot)
                val setsDir = candidateDir(
                    langRoot.resolve("sets"),
                    langRoot.resolve("Sets"),
                    langRoot.resolve("data").resolve("sets"),
                    langRoot.resolve("tcgdex").resolve("sets"),
                )
                val setsJsonPath = listOf(
                    langRoot.resolve("sets.json"),
                    langRoot.resolve("Sets.json"),
                    langRoot.resolve("data").resolve("sets.json"),
                    langRoot.resolve("tcgdex").resolve("sets.json"),
                ).firstOrNull { Files.isRegularFile(it) }
                if (setsDir == null && setsJsonPath == null) {
                    log("[i] No immediate local sets found (dir or sets.json). Scanning recursively under " + langRoot)
                    val foundSetsJson = findFirstFile(langRoot, "sets.json", maxDepth = 10)
                    val foundSetsDir = findFirstDir(langRoot, "sets", maxDepth = 10)
                    val finalSetsJson = foundSetsJson
                    val finalSetsDir = foundSetsDir
                    if (finalSetsJson != null) log("[ok] Found sets.json at " + finalSetsJson)
                    if (finalSetsDir != null) log("[ok] Found sets directory at " + finalSetsDir)
                    if (finalSetsJson == null && finalSetsDir == null) {
                        // Local TS database structure (authoritative): <root>/data/{Series}.ts and <root>/data/{Series}/{Set}.ts
                        val dataRoot = listOf(
                            localDbPath.resolve("data"),
                            localDbPath
                        ).firstOrNull { Files.isDirectory(it) } ?: localDbPath
                        if (dataRoot != null) {
                            log("lang=" + lang + ": scanning TS DB under " + dataRoot)
                            // Discover series files: all *.ts directly under dataRoot
                            val seriesFiles = mutableListOf<Path>()
                            Files.newDirectoryStream(dataRoot).use { ds ->
                                for (p in ds) if (Files.isRegularFile(p) && p.toString().endsWith(".ts")) seriesFiles.add(p)
                            }
                            log("lang=" + lang + ": series files found=" + seriesFiles.size)
                            // Optional narrowing by seriesFilter (matches series file base name)
                            val filteredSeriesFiles = seriesFiles.filter { sf ->
                                val base = sf.fileName.toString().removeSuffix(".ts")
                                seriesFilter == null || base.contains(seriesFilter, ignoreCase = true)
                            }
                            log("lang=" + lang + ": filtered series files=" + filteredSeriesFiles.size + (if (seriesFilter != null) " filter='" + seriesFilter + "'" else ""))
                            val langDir = outBasePath.resolve(lang)
                            Files.createDirectories(langDir)
                            val detailed = mutableListOf<SetOut>()
                            val seriesOut = mutableListOf<SeriesOut>()
                            val langIllustratorAcc: MutableMap<String, Acc> = linkedMapOf()
                            var processedSets = 0
                            for (sf in filteredSeriesFiles) {
                                val sContent = runCatching { Files.readString(sf) }.getOrNull() ?: continue
                                val serieId = extractStringField(sContent, "id")
                                val serieName = extractNameForLang(sContent, lang) ?: extractNameForLang(sContent, "en") ?: sf.fileName.toString().removeSuffix(".ts")
                                log("lang=" + lang + ": series file=" + sf.fileName + " -> id=" + (serieId ?: "<none>") + ", name=" + serieName)
                                if (serieId == null) continue
                                var earliestForSerie: String? = null

                                // The sets for this series are the *.ts files in the folder named after the series display name (EN),
                                // fallback to the series TS filename base when localized name folder is absent
                                val seriesFolder = listOf(
                                    dataRoot.resolve(serieName),
                                    dataRoot.resolve(sf.fileName.toString().removeSuffix(".ts"))
                                ).firstOrNull { Files.isDirectory(it) } ?: continue
                                log("lang=" + lang + ": scanning sets in " + seriesFolder)
                                val setFiles = mutableListOf<Path>()
                                Files.newDirectoryStream(seriesFolder).use { ds ->
                                    for (p in ds) if (Files.isRegularFile(p) && p.toString().endsWith(".ts")) setFiles.add(p)
                                }
                                log("lang=" + lang + ": set files found=" + setFiles.size)
                                val totalSets = if (limitSets > 0) kotlin.math.min(limitSets - processedSets, setFiles.size) else setFiles.size
                                for (setTs in setFiles.take(if (limitSets > 0) totalSets else setFiles.size)) {
                                    val c = runCatching { Files.readString(setTs) }.getOrNull() ?: continue
                                    val setId = extractStringField(c, "id") ?: continue
                                    val setName = extractNameForLang(c, lang) ?: extractNameForLang(c, "en") ?: setTs.fileName.toString().removeSuffix(".ts")
                                    val releaseDate = extractStringField(c, "releaseDate")
                                    if (releaseDate != null) {
                                        earliestForSerie = listOfNotNull(earliestForSerie, releaseDate).minOrNull()
                                    }
                                    var logo = extractStringField(c, "logo")
                                    var symbol = extractStringField(c, "symbol")
                                    if (logo.isNullOrBlank() || symbol.isNullOrBlank()) {
                                        val assetBase = "https://assets.tcgdex.net/" + lang.lowercase() + "/" + serieId + "/" + setId
                                        if (logo.isNullOrBlank()) logo = assetBase + "/logo"
                                        if (symbol.isNullOrBlank()) symbol = assetBase + "/symbol"
                                    }
                                    val official = extractIntFieldInObject(c, "cardCount", "official")
                                    val total = extractIntFieldInObject(c, "cardCount", "total") ?: official
                                    detailed += SetOut(
                                        id = setId,
                                        name = setName,
                                        releaseDate = releaseDate,
                                        logo = logo,
                                        symbol = symbol,
                                        serieId = serieId,
                                        official = official,
                                        total = total,
                                    )

                                    // Aggregate illustrators for this set from card TS files in folder named after the set file base
                                    val cardsFolder = seriesFolder.resolve(setTs.fileName.toString().removeSuffix(".ts"))
                                    if (Files.isDirectory(cardsFolder)) {
                                        Files.newDirectoryStream(cardsFolder).use { cds ->
                                            for (cp in cds) {
                                                if (!Files.isRegularFile(cp) || !cp.toString().endsWith(".ts")) continue
                                                val cardTs = runCatching { Files.readString(cp) }.getOrNull() ?: continue
                                                val illustrator = extractStringField(cardTs, "illustrator")
                                                    ?.trim()?.replace("\\s+".toRegex(), " ")
                                                if (!illustrator.isNullOrEmpty()) {
                                                    val acc = langIllustratorAcc.getOrPut(illustrator) { Acc(0, linkedMapOf()) }
                                                    acc.total += 1
                                                    val entry = acc.perSet.getOrPut(setId) { ISet(setId = setId, count = 0) }
                                                    entry.count += 1
                                                }
                                            }
                                        }
                                    }
                                    processedSets++
                                    if (processedSets % kotlin.math.max(1, progressEvery) == 0) {
                                        log("lang=" + lang + ": progress " + processedSets)
                                    }
                                    if (limitSets > 0 && processedSets >= limitSets) break
                                }
                                // After scanning sets for this series, append seriesOut with earliest release date
                                seriesOut += SeriesOut(id = serieId, name = serieName, releaseDate = earliestForSerie)
                                if (limitSets > 0 && processedSets >= limitSets) break
                            }
                            // Write outputs (illustrator index will be handled elsewhere)
                            val filteredSeries = seriesOut.distinctBy { it.id }.filter { !isTcgp(it.id) }
                            val filteredSets = detailed.filter { !isTcgp(it.serieId) }
                            Files.writeString(
                                langDir.resolve("series.json"),
                                json.encodeToString(filteredSeries),
                                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
                            )
                            Files.writeString(
                                langDir.resolve("sets.json"),
                                json.encodeToString(filteredSets),
                                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
                            )
            // Build illustrators index for potential diagnostics only (not written per-language)
            val artists: List<IllustratorIndexEntry> = langIllustratorAcc.entries
                .sortedByDescending { it.value.total }
                .map { (artist, acc) ->
                    val setsList = acc.perSet.values.map { IllustratorSetCount(setId = it.setId, count = it.count) }
                        .sortedByDescending { it.count }
                    IllustratorIndexEntry(name = artist, total = acc.total, sets = setsList)
                }
            val payload = IllustratorIndexPayload(language = lang.lowercase(), generatedAt = System.currentTimeMillis(), artists = artists)
            // Ensure no legacy per-language file remains
            runCatching { Files.deleteIfExists(langDir.resolve("illustrators-index.json")) }
            seriesMap[lang.lowercase()] = json.encodeToString(filteredSeries)
            setsMap[lang.lowercase()] = json.encodeToString(filteredSets)
            illustratorsMap[lang.lowercase()] = json.encodeToString(payload)
            log("lang=" + lang + ": wrote series=" + filteredSeries.size + " sets=" + filteredSets.size + " artists=" + langIllustratorAcc.size)
                            continue
                        }
                        // Fallback: cards-dir-driven discovery under langRoot
                        val cardsRoot = findFirstDir(langRoot, "cards", maxDepth = 10)
                        if (cardsRoot == null) {
                            log("[x] No local sets or cards directory found for lang=" + lang)
                            if (offlineOnly) { continue } else {
                                log("[~] Falling back to remote for lang=" + lang)
                                // fallthrough to remote
                            }
                        } else {
                            log("[ok] Found cards directory at " + cardsRoot + "; deriving sets from subdirectories")
                            val setDirs = mutableListOf<Path>()
                            Files.newDirectoryStream(cardsRoot).use { sds ->
                                for (d in sds) if (Files.isDirectory(d)) setDirs.add(d)
                            }
                            val totalSets = if (limitSets > 0) kotlin.math.min(limitSets, setDirs.size) else setDirs.size
                            log("lang=" + lang + ": card-set dirs=" + setDirs.size + " (processing=" + totalSets + ")")
                            val langDir = outBasePath.resolve(lang)
                            Files.createDirectories(langDir)
                            val detailed = mutableListOf<SetOut>()
                            val langIllustratorAcc: MutableMap<String, Acc> = linkedMapOf()
                            var processed = 0
                            for (sd in setDirs.take(totalSets)) {
                                val setId = sd.fileName.toString()
                                var setNameGuess = setId
                                Files.newDirectoryStream(sd).use { cds ->
                                    for (cp in cds) {
                                        if (!cp.toString().endsWith(".json")) continue
                                        val cobj = safeReadJson(cp)?.jsonObject ?: continue
                                        val sObj = cobj["set"] as? JsonObject
                                        val nm = sObj?.get("name")?.jsonPrimitive?.content
                                        if (!nm.isNullOrBlank()) { setNameGuess = nm }
                                        val illustrator = cobj["illustrator"]?.jsonPrimitive?.content
                                            ?.trim()?.replace("\\s+".toRegex(), " ")
                                        if (!illustrator.isNullOrEmpty()) {
                                            val acc = langIllustratorAcc.getOrPut(illustrator) { Acc(0, linkedMapOf()) }
                                            acc.total += 1
                                            val entry = acc.perSet.getOrPut(setId) { ISet(setId = setId, count = 0) }
                                            entry.count += 1
                                        }
                                    }
                                }
                                detailed += SetOut(
                                    id = setId,
                                    name = setNameGuess,
                                    releaseDate = null,
                                    logo = null,
                                    symbol = null,
                                    serieId = extractSerieIdFromSetId(setId),
                                    official = null,
                                    total = null,
                                )
                                processed++
                                if (processed % kotlin.math.max(1, progressEvery) == 0 || processed == totalSets) {
                                    log("lang=" + lang + ": progress " + processed + "/" + totalSets)
                                }
                            }
                            // Filter out tcgp sets
                            val detailedFiltered = detailed.filter { !isTcgpSetId(it.id) && !isTcgp(it.serieId) }
                            // Write outputs
                            val series = detailedFiltered
                                .mapNotNull { it.serieId?.let { id -> id to (serieIdToName[id] ?: id) } }
                                .distinctBy { it.first }
                                .map { SeriesOut(it.first, it.second) }
                            Files.writeString(
                                langDir.resolve("series.json"),
                                json.encodeToString(series),
                                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
                            )
                            Files.writeString(
                                langDir.resolve("sets.json"),
                                json.encodeToString(detailedFiltered),
                                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
                            )
                            // Do not generate per-language illustrators-index.json (language-agnostic index is written under /illustrators)
                            val artists = langIllustratorAcc.entries
                                .sortedByDescending { it.value.total }
                                .map { (artist, acc) ->
                                    val setsList = acc.perSet.values
                                        .filter { !isTcgpSetId(it.setId) }
                                        .map { IllustratorSetCount(setId = it.setId, count = it.count) }
                                        .sortedByDescending { it.count }
                                    IllustratorIndexEntry(name = artist, total = acc.total, sets = setsList)
                                }
                            val payload = IllustratorIndexPayload(language = lang.lowercase(), generatedAt = System.currentTimeMillis(), artists = artists)
                            runCatching { Files.deleteIfExists(langDir.resolve("illustrators-index.json")) }
                            seriesMap[lang.lowercase()] = json.encodeToString(series)
                            setsMap[lang.lowercase()] = json.encodeToString(detailedFiltered)
                            illustratorsMap[lang.lowercase()] = json.encodeToString(payload)
                            log("lang=" + lang + ": wrote series=" + series.size + " sets=" + detailedFiltered.size + " artists=" + langIllustratorAcc.size)
                            continue
                        }
                    } else if (finalSetsDir != null) {
                        // Shadow local variable for simplicity
                        val setsDir = finalSetsDir
                        val setFiles = mutableListOf<Path>()
                        Files.newDirectoryStream(setsDir).use { ds ->
                            for (p in ds) {
                                if (p.toString().endsWith(".json")) setFiles.add(p)
                            }
                        }
                        val totalSets = if (limitSets > 0) kotlin.math.min(limitSets, setFiles.size) else setFiles.size
                        log("lang=" + lang + ": sets=" + setFiles.size + " (processing=" + totalSets + ")")
                        val langDir = outBasePath.resolve(lang)
                        Files.createDirectories(langDir)
                        val detailed = mutableListOf<SetOut>()
                        var processed = 0
                        for (setPath in setFiles.take(totalSets)) {
                            val obj = safeReadJson(setPath)?.jsonObject ?: JsonObject(emptyMap())
                            val fileIdGuess = setPath.fileName.toString().removeSuffix(".json")
                            val setId = obj["id"]?.jsonPrimitive?.content ?: fileIdGuess
                            val setName = obj["name"]?.jsonPrimitive?.content ?: setId
                            val releaseDate = obj["releaseDate"]?.jsonPrimitive?.content
                            val logo = obj["logo"]?.jsonPrimitive?.content
                            val symbol = obj["symbol"]?.jsonPrimitive?.content
                            val serieId = (obj["serie"] as? JsonObject)?.get("id")?.jsonPrimitive?.content
                                ?: obj["serieId"]?.jsonPrimitive?.content
                                ?: extractSerieIdFromSetId(setId)
                            val official = obj["cardCount"]?.jsonObject?.get("official")?.jsonPrimitive?.content?.toIntOrNull()
                            val total = obj["cardCount"]?.jsonObject?.get("total")?.jsonPrimitive?.content?.toIntOrNull()
                                ?: official
                            detailed += SetOut(
                                id = setId,
                                name = setName,
                                releaseDate = releaseDate,
                                logo = logo,
                                symbol = symbol,
                                serieId = serieId,
                                official = official,
                                total = total,
                            )
                            processed++
                            if (processed % kotlin.math.max(1, progressEvery) == 0 || processed == totalSets) {
                                log("lang=" + lang + ": progress " + processed + "/" + totalSets)
                            }
                        }
                        // Filter out tcgp sets
                        val detailedFiltered = detailed.filter { !isTcgpSetId(it.id) && !isTcgp(it.serieId) }
                        // Write outputs minimal (illustrators scanning happens later)
                        Files.writeString(
                            langDir.resolve("sets.json"),
                            json.encodeToString(detailedFiltered),
                            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
                        )
                        // Build series list from embedded names if possible
                        val series = detailedFiltered
                            .mapNotNull { it.serieId?.let { id -> id to (serieIdToName[id] ?: id) } }
                            .distinctBy { it.first }
                            .map { SeriesOut(it.first, it.second) }
                        Files.writeString(
                            langDir.resolve("series.json"),
                            json.encodeToString(series),
                            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
                        )
                        // fall through to illustrator aggregation below (common path uses 'detailed')
                        // Replace current branch state by emulating original variables
                        // NOTE: We cannot easily jump; so let it drop to later branches via 'continue' after full local flow
                        // To keep patch simple, we will proceed to illustrator aggregation after this main branch
                    } else {
                        // sets.json found
                        // We will handle in the dedicated else-if block below
                    }
                } else if (setsDir != null) {
                    val setFiles = mutableListOf<Path>()
                    Files.newDirectoryStream(setsDir).use { ds ->
                        for (p in ds) {
                            if (p.toString().endsWith(".json")) setFiles.add(p)
                        }
                    }
                    val totalSets = if (limitSets > 0) kotlin.math.min(limitSets, setFiles.size) else setFiles.size
                    log("lang=" + lang + ": sets=" + setFiles.size + " (processing=" + totalSets + ")")

                    val langDir = outBasePath.resolve(lang)
                    Files.createDirectories(langDir)

                    val detailed = mutableListOf<SetOut>()

                    var processed = 0
                    for (setPath in setFiles.take(totalSets)) {
                        val obj = safeReadJson(setPath)?.jsonObject ?: JsonObject(emptyMap())
                        val fileIdGuess = setPath.fileName.toString().removeSuffix(".json")
                        val setId = obj["id"]?.jsonPrimitive?.content ?: fileIdGuess
                        val setName = obj["name"]?.jsonPrimitive?.content ?: setId
                        val releaseDate = obj["releaseDate"]?.jsonPrimitive?.content
                        val logo = obj["logo"]?.jsonPrimitive?.content
                        val symbol = obj["symbol"]?.jsonPrimitive?.content
                        val serieId = (obj["serie"] as? JsonObject)?.get("id")?.jsonPrimitive?.content
                            ?: obj["serieId"]?.jsonPrimitive?.content
                            ?: extractSerieIdFromSetId(setId)
                        val official = obj["cardCount"]?.jsonObject?.get("official")?.jsonPrimitive?.content?.toIntOrNull()
                        val total = obj["cardCount"]?.jsonObject?.get("total")?.jsonPrimitive?.content?.toIntOrNull()
                            ?: official

                        detailed += SetOut(
                            id = setId,
                            name = setName,
                            releaseDate = releaseDate,
                            logo = logo,
                            symbol = symbol,
                            serieId = serieId,
                            official = official,
                            total = total,
                        )

                        // Scan local cards for illustrators
                        val cardsDir = candidateDir(
                            langRoot.resolve("cards").resolve(setId),
                            langRoot.resolve("data").resolve("cards").resolve(setId),
                            langRoot.resolve("tcgdex").resolve("cards").resolve(setId),
                        )
                        val illustratorAcc: MutableMap<String, Acc> = linkedMapOf()
                        if (cardsDir != null) {
                            Files.newDirectoryStream(cardsDir).use { cds ->
                                for (cp in cds) {
                                    if (!cp.toString().endsWith(".json")) continue
                                    val cobj = safeReadJson(cp)?.jsonObject ?: continue
                                    val illustrator = cobj["illustrator"]?.jsonPrimitive?.content
                                        ?.trim()?.replace("\\s+".toRegex(), " ")
                                    if (!illustrator.isNullOrEmpty()) {
                                        val acc = illustratorAcc.getOrPut(illustrator) { Acc(0, linkedMapOf()) }
                                        acc.total += 1
                                        val entry = acc.perSet.getOrPut(setId) { ISet(setId = setId, count = 0) }
                                        entry.count += 1
                                    }
                                }
                            }
                        } else {
                            log("[warn] No cards directory for set=" + setId + " lang=" + lang)
                        }

                        processed++
                        if (processed % kotlin.math.max(1, progressEvery) == 0 || processed == totalSets) {
                            log("lang=" + lang + ": progress " + processed + "/" + totalSets)
                        }

                        // After processing, write illustrator index incrementally? we aggregate after loop per language
                        // Move illustratorAcc outside loop if needed
                    }
                    totalSetsAll += totalSets

                    // Series names: try local series.json; else fallbacks
                    val seriesNames = mutableMapOf<String, String>()
                    val seriesJsonPath = listOf(
                        langRoot.resolve("series.json"),
                        langRoot.resolve("Series.json"),
                        langRoot.resolve("data").resolve("series.json"),
                    ).firstOrNull { Files.isRegularFile(it) }
                    if (seriesJsonPath != null) {
                        val arr = safeReadJson(seriesJsonPath)
                        val arrList = arr?.jsonArray ?: emptyList()
                        for (je in arrList) {
                            val o = (je as? JsonObject) ?: continue
                            val sid = o["id"]?.jsonPrimitive?.content ?: continue
                            val sname = o["name"]?.jsonPrimitive?.content ?: sid
                            seriesNames[sid] = sname
                        }
                    }

                    val filteredDetailed = detailed.filter { !isTcgpSetId(it.id) && !isTcgp(it.serieId) }
                    val series: List<SeriesOut> = filteredDetailed
                        .mapNotNull { it.serieId?.let { id -> id to (seriesNames[id] ?: (serieIdToName[id] ?: id)) } }
                        .distinctBy { it.first }
                        .map { SeriesOut(id = it.first, name = it.second) }

                    Files.writeString(
                        langDir.resolve("series.json"),
                        json.encodeToString(series),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
                    )
                    Files.writeString(
                        langDir.resolve("sets.json"),
                        json.encodeToString(filteredDetailed),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
                    )

                    // Aggregate illustrators for language from detailed by re-scanning cards dirs
                    val langIllustratorAcc: MutableMap<String, Acc> = linkedMapOf()
                    for (set in filteredDetailed) {
                        val cardsDir = candidateDir(
                            langRoot.resolve("cards").resolve(set.id),
                            langRoot.resolve("data").resolve("cards").resolve(set.id),
                            langRoot.resolve("tcgdex").resolve("cards").resolve(set.id),
                        )
                        if (cardsDir == null) continue
                        Files.newDirectoryStream(cardsDir).use { cds ->
                            for (cp in cds) {
                                if (!cp.toString().endsWith(".json")) continue
                                val cobj = safeReadJson(cp)?.jsonObject ?: continue
                                val illustrator = cobj["illustrator"]?.jsonPrimitive?.content
                                    ?.trim()?.replace("\\s+".toRegex(), " ")
                                if (!illustrator.isNullOrEmpty()) {
                                    val acc = langIllustratorAcc.getOrPut(illustrator) { Acc(0, linkedMapOf()) }
                                    acc.total += 1
                                    val entry = acc.perSet.getOrPut(set.id) { ISet(setId = set.id, count = 0) }
                                    entry.count += 1
                                }
                            }
                        }
                    }

                    val artists = langIllustratorAcc.entries
                        .sortedByDescending { it.value.total }
                        .map { (artist, acc) ->
                            val setsList = acc.perSet.values.map { IllustratorSetCount(setId = it.setId, count = it.count) }.sortedByDescending { it.count }
                            IllustratorIndexEntry(name = artist, total = acc.total, sets = setsList)
                        }
                    val payload = IllustratorIndexPayload(language = lang.lowercase(), generatedAt = System.currentTimeMillis(), artists = artists)

                    // Remove legacy per-language file if present
                    runCatching { Files.deleteIfExists(langDir.resolve("illustrators-index.json")) }

                    seriesMap[lang.lowercase()] = json.encodeToString(series)
                    setsMap[lang.lowercase()] = json.encodeToString(detailed)
                    illustratorsMap[lang.lowercase()] = json.encodeToString(payload)
                    log("lang=" + lang + ": wrote series=" + series.size + " sets=" + detailed.size + " artists=" + langIllustratorAcc.size)
                    continue
                }
                else if (setsJsonPath != null) {
                    log("lang=" + lang + ": using sets.json at " + setsJsonPath)
                    val arr = safeReadJson(setsJsonPath)?.jsonArray ?: emptyList()
                    val allPairs = arr.mapNotNull { je ->
                        val o = (je as? JsonObject) ?: return@mapNotNull null
                        val sid = o["id"]?.jsonPrimitive?.content ?: return@mapNotNull null
                        val sname = o["name"]?.jsonPrimitive?.content ?: sid
                        sid to sname
                    }
                    val totalSets = if (limitSets > 0) kotlin.math.min(limitSets, allPairs.size) else allPairs.size
                    log("lang=" + lang + ": sets-json entries=" + allPairs.size + " (processing=" + totalSets + ")")
                    val langDir = outBasePath.resolve(lang)
                    Files.createDirectories(langDir)
                    val detailed = mutableListOf<SetOut>()
                    var processed = 0
                    for ((sid, sname) in allPairs.take(totalSets)) {
                        if (verbose) log("lang=" + lang + ": set=" + sid + " name=\"" + sname + "\"")
                        detailed += SetOut(
                            id = sid,
                            name = sname,
                            releaseDate = null,
                            logo = null,
                            symbol = null,
                            serieId = extractSerieIdFromSetId(sid),
                            official = null,
                            total = null,
                        )
                        processed++
                        if (processed % kotlin.math.max(1, progressEvery) == 0 || processed == totalSets) {
                            log("lang=" + lang + ": progress " + processed + "/" + totalSets)
                        }
                    }
                    // Write series/sets
                    Files.writeString(
                        langDir.resolve("sets.json"),
                        json.encodeToString(detailed),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
                    )
                    // Series: attempt local series.json for names
                    val seriesNames = mutableMapOf<String, String>()
                    val seriesJsonPath = listOf(
                        langRoot.resolve("series.json"),
                        langRoot.resolve("Series.json"),
                        langRoot.resolve("data").resolve("series.json"),
                    ).firstOrNull { Files.isRegularFile(it) }
                    if (seriesJsonPath != null) {
                        val sarr = safeReadJson(seriesJsonPath)?.jsonArray ?: emptyList()
                        for (je in sarr) {
                            val o = (je as? JsonObject) ?: continue
                            val id = o["id"]?.jsonPrimitive?.content ?: continue
                            val nm = o["name"]?.jsonPrimitive?.content ?: id
                            seriesNames[id] = nm
                        }
                    }
                    val series = detailed
                        .mapNotNull { it.serieId?.let { id -> id to (seriesNames[id] ?: (serieIdToName[id] ?: id)) } }
                        .distinctBy { it.first }
                        .map { SeriesOut(it.first, it.second) }
                    Files.writeString(
                        langDir.resolve("series.json"),
                        json.encodeToString(series),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
                    )
                    // Build illustrators index by scanning cards dirs
                    val langIllustratorAcc: MutableMap<String, Acc> = linkedMapOf()
                    for (set in detailed) {
                        val cardsDir = candidateDir(
                            langRoot.resolve("cards").resolve(set.id),
                            langRoot.resolve("data").resolve("cards").resolve(set.id),
                            langRoot.resolve("tcgdex").resolve("cards").resolve(set.id),
                        )
                        if (cardsDir == null) {
                            if (verbose) log("lang=" + lang + ": no cards dir for set=" + set.id)
                            continue
                        }
                        Files.newDirectoryStream(cardsDir).use { cds ->
                            for (cp in cds) {
                                if (!cp.toString().endsWith(".json")) continue
                                val cobj = safeReadJson(cp)?.jsonObject ?: continue
                                val illustrator = cobj["illustrator"]?.jsonPrimitive?.content
                                    ?.trim()?.replace("\\s+".toRegex(), " ")
                                if (!illustrator.isNullOrEmpty()) {
                                    val acc = langIllustratorAcc.getOrPut(illustrator) { Acc(0, linkedMapOf()) }
                                    acc.total += 1
                                    val entry = acc.perSet.getOrPut(set.id) { ISet(setId = set.id, count = 0) }
                                    entry.count += 1
                                }
                            }
                        }
                    }
                    val artists = langIllustratorAcc.entries
                        .sortedByDescending { it.value.total }
                        .map { (artist, acc) ->
                            val setsList = acc.perSet.values.map { IllustratorSetCount(setId = it.setId, count = it.count) }.sortedByDescending { it.count }
                            IllustratorIndexEntry(name = artist, total = acc.total, sets = setsList)
                        }
                    val payload = IllustratorIndexPayload(language = lang.lowercase(), generatedAt = System.currentTimeMillis(), artists = artists)
                    // Remove legacy per-language file if present
                    runCatching { Files.deleteIfExists(langDir.resolve("illustrators-index.json")) }
                    seriesMap[lang.lowercase()] = json.encodeToString(series)
                    setsMap[lang.lowercase()] = json.encodeToString(detailed)
                    illustratorsMap[lang.lowercase()] = json.encodeToString(payload)
                    log("lang=" + lang + ": wrote series=" + series.size + " sets=" + detailed.size + " artists=" + langIllustratorAcc.size)
                    continue
                }
            } catch (t: Throwable) {
                log("[x] Local generation failed for lang=" + lang + ": " + (t.message ?: "unknown"))
                if (offlineOnly) continue else log("[~] Falling back to remote for lang=" + lang)
            }
        }
        try {
            val setsResp = sdk.getSets(language = lang, page = 1, pageSize = 2000).getOrThrow()
            val fullSets = setsResp.data.orEmpty()
            val totalSets = if (limitSets > 0) kotlin.math.min(limitSets, fullSets.size) else fullSets.size
            log("lang=" + lang + ": sets=" + fullSets.size + " (processing=" + totalSets + ")")
            val langDir = outBasePath.resolve(lang)
            Files.createDirectories(langDir)
            val detailed = mutableListOf<SetOut>()
            // Build illustrator index: artist -> (total count, per-set counts)
            val illustratorAcc: MutableMap<String, Acc> = linkedMapOf()
            var processed = 0
            for (s in fullSets.take(totalSets)) {
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
                    total = full.cardCount?.total ?: full.cardCount?.official,
                )
                // Also persist full REST detail JSON for this set for offline enrichment (unless skipped)
                if (!skipDetails) {
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
                        log("[warn] Failed detail write " + lang + ":" + full.id + ": " + (t.message ?: "unknown"))
                    }
                }

                // Build illustrator counts for this set using SDK cards listing
                try {
                    var page = 1
                    val pageSize = 250
                    while (true) {
                        val resp = sdk.getCardsBySet(language = lang, setId = s.id, page = page, pageSize = pageSize).getOrThrow()
                        val cards = resp.data.orEmpty()
                        if (cards.isEmpty()) break
                        for (c in cards) {
                            val name = c.illustrator?.trim().orEmpty()
                            if (name.isEmpty()) continue
                            val acc = illustratorAcc.getOrPut(name) { Acc(0, linkedMapOf()) }
                            acc.total += 1
                            val entry = acc.perSet.getOrPut(s.id) { ISet(setId = s.id, count = 0) }
                            entry.count += 1
                        }
                        val total = resp.total ?: (page * pageSize)
                        if ((page * pageSize) >= total) break
                        page++
                    }
                } catch (t: Throwable) {
                    log("[warn] Skipping illustrator scan for set=" + s.id + " lang=" + lang + ": " + (t.message ?: "unknown"))
                }
                processed++
                if (processed % kotlin.math.max(1, progressEvery) == 0 || processed == totalSets) {
                    log("lang=" + lang + ": progress " + processed + "/" + totalSets)
                }
            }
            totalSetsAll += totalSets
            val filteredDetailed = detailed.filter { !isTcgpSetId(it.id) && !isTcgp(it.serieId) }
            val series: List<SeriesOut> = filteredDetailed
                .mapNotNull { it.serieId?.let { id -> id to (fullSets.firstOrNull { b -> b.serie?.id == id }?.serie?.name ?: (serieIdToName[id] ?: id)) } }
                .distinctBy { it.first }
                .map { SeriesOut(id = it.first, name = it.second) }

            Files.writeString(
                langDir.resolve("series.json"),
                json.encodeToString(series),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
            )
            Files.writeString(
                langDir.resolve("sets.json"),
                json.encodeToString(filteredDetailed),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
            )

            // Write illustrators index to resources and embed to Kotlin
            val artists: List<IllustratorIndexEntry> = illustratorAcc.entries
                .sortedByDescending { it.value.total }
                .map { (artist, acc) ->
                    val setsList = acc.perSet.values
                        .filter { !isTcgpSetId(it.setId) }
                        .map { IllustratorSetCount(setId = it.setId, count = it.count) }
                        .sortedByDescending { it.count }
                    IllustratorIndexEntry(name = artist, total = acc.total, sets = setsList)
                }
            val payload = IllustratorIndexPayload(language = lang.lowercase(), generatedAt = System.currentTimeMillis(), artists = artists)

            // Remove legacy per-language file if present
            runCatching { Files.deleteIfExists(langDir.resolve("illustrators-index.json")) }

            // Accumulate for Kotlin embedding
            seriesMap[lang.lowercase()] = json.encodeToString(series)
            setsMap[lang.lowercase()] = json.encodeToString(filteredDetailed)
            illustratorsMap[lang.lowercase()] = json.encodeToString(payload)
            log("lang=" + lang + ": wrote series=" + series.size + " sets=" + detailed.size + " artists=" + illustratorAcc.size)
        } catch (t: Throwable) {
            log("[x] Skipping lang=" + lang + " due to error: " + (t.message ?: "unknown"))
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
        // New language-agnostic illustrators assets
        appendLine("  private val illustratorsByArtist: Map<String, String> = mapOf(")
        illustratorFiles.forEach { (artistFile, payload) ->
            appendLine("    \"$artistFile\" to ${toKotlinTripleQuoted(payload)},")
        }
        appendLine("  )")
        appendLine("  private val illustratorsIndex: String? = ${illustratorsIndexGlobal?.let { toKotlinTripleQuoted(it) } ?: "null"}")
        appendLine("  fun seriesJson(lang: String): String? = seriesByLang[lang.lowercase()]")
        appendLine("  fun setsJson(lang: String): String? = setsByLang[lang.lowercase()]")
        appendLine("  fun illustratorsIndexJson(): String? = illustratorsIndex")
        appendLine("  fun illustratorCollectionJson(artistFileName: String): String? = illustratorsByArtist[artistFileName]")
        appendLine("  fun illustratorArtistFiles(): List<String> = illustratorsByArtist.keys.toList()")
        appendLine("}")
        appendLine()
    }
    Files.writeString(
        targetDir.resolve("EmbeddedCatalogData.kt"),
        kt,
        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
    )
    val embeddedPath = targetDir.resolve("EmbeddedCatalogData.kt")
    log("Completed. Embedded Kotlin regenerated at: " + embeddedPath)
    log("Summary: total sets processed ~ " + totalSetsAll + " across " + languages.size + " languages")
}
}
}



