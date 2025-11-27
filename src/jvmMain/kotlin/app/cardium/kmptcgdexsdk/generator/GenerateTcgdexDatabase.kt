@file:Suppress("ktlint:standard:filename")

package app.cardium.kmptcgdexsdk.generator

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cardium.tcgdex.db.TcgdexDatabase
import app.cardium.tcgdex.sdk.storage.TcgdexDatabaseInstaller
import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Database generator for the offline TCGdex SQLite database.
 *
 * Reads JSON files from the cards-database server/generated directory
 * and populates a SQLite database file.
 *
 * Usage: java -jar ... --dataset=/path/to/generated --languages=en,fr --output=/path/to/tcgdex.db
 */
fun main(args: Array<String>) {
    val config = parseArgs(args)
    println("[Tcgdex] Starting database generation...")
    println("[Tcgdex] Dataset: ${config.datasetDir}")
    println("[Tcgdex] Languages: ${config.languages}")
    println("[Tcgdex] Output: ${config.outputFile}")
    println("[Tcgdex] Force: ${config.force}")

    val outputFile = File(config.outputFile)
    if (outputFile.exists()) {
        if (config.force) {
            println("[Tcgdex] Deleting existing database (force=true)")
            outputFile.delete()
        } else {
            println("[Tcgdex] Database already exists, skipping generation")
            return
        }
    }

    outputFile.parentFile?.mkdirs()

    val driver = JdbcSqliteDriver("jdbc:sqlite:${outputFile.absolutePath}")
    TcgdexDatabase.Schema.create(driver)
    val db = TcgdexDatabase(driver)

    val json = Json { ignoreUnknownKeys = true }

    // Track unique illustrators and rarities (language-agnostic)
    val illustrators = mutableMapOf<String, String>() // id -> name
    val rarities = mutableMapOf<String, String>() // id -> name
    
    // Track missing images for Pokecardex fallback index
    val missingImages = mutableListOf<MissingImagesIndexGenerator.MissingImageEntry>()
    // Track all cards per language for cross-language comparison
    val cardsByLanguage = mutableMapOf<String, MutableSet<String>>() // language -> set of card IDs
    val englishCardCache = mutableMapOf<String, JsonObject>()

    fun insertCard(
        language: String,
        card: JsonObject,
        originLanguage: String = language,
    ) {
        val langCardIds = cardsByLanguage.getOrPut(language) { mutableSetOf() }
        val id = card.getString("id") ?: return
        if (!langCardIds.add(id)) {
            return
        }

        val localId = card.getString("localId") ?: id.substringAfterLast("-")
        val setId = card.getNestedString("set", "id") ?: return
        val name = card.getString("name") ?: id
        val imageUrl = card.getString("image")

        if (imageUrl.isNullOrBlank()) {
            val pokecardexUrl = MissingImagesIndexGenerator.buildPokecardexUrl(
                setId = setId,
                localId = localId,
                language = language,
                hd = false,
            )
            missingImages.add(
                MissingImagesIndexGenerator.MissingImageEntry(
                    language = language,
                    cardId = id,
                    setId = setId,
                    localId = localId,
                    tcgdexUrl = null,
                    pokecardexUrl = pokecardexUrl,
                ),
            )
        }

        val illustratorName = card.getString("illustrator")
        val illustratorId = if (illustratorName != null) {
            val slug = slugify(illustratorName)
            illustrators[slug] = illustratorName
            slug
        } else {
            null
        }

        val rarityName = card.getString("rarity")
        val rarityId = if (rarityName != null) {
            val slug = slugify(rarityName)
            rarities[slug] = rarityName
            slug
        } else {
            null
        }

        val dexIds = card.getIntArray("dexId")
        val pokemonDexId = dexIds?.firstOrNull()

        val types = card.getStringArray("types")?.joinToString(",")
        val category = card.getString("category")
        val supertype = card.getString("supertype")
        val regulationMark = card.getString("regulationMark")
        val localNumberSort = extractNumericPart(localId)

        db.tcgdexQueries.insertCard(
            id = id,
            language = language,
            localId = localId,
            localNumberSort = localNumberSort.toLong(),
            setId = setId,
            pokemonDexId = pokemonDexId?.toLong(),
            rarityId = rarityId,
            illustratorId = illustratorId,
            name = name,
            imageUrl = imageUrl,
            originLanguage = originLanguage,
            category = category,
            types = types,
            supertype = supertype,
            regulationMark = regulationMark,
        )
    }

    for (language in config.languages) {
        val langDir = File(config.datasetDir, language)
        if (!langDir.exists()) {
            println("[Tcgdex] Warning: Language directory not found: $langDir")
            continue
        }

        println("[Tcgdex] Processing language: $language")

        // Load series
        val seriesFile = File(langDir, "series.json")
        if (seriesFile.exists()) {
            val seriesJson = json.parseToJsonElement(seriesFile.readText()).jsonArray
            var position = 0
            for (serieElement in seriesJson) {
                val serie = serieElement.jsonObject
                val id = serie.getString("id") ?: continue
                val name = serie.getString("name") ?: id
                db.tcgdexQueries.insertSerie(id, language, name, position.toLong())
                position++
            }
            println("[Tcgdex]   Series: ${seriesJson.size}")
        }

        // Load sets
        val setsFile = File(langDir, "sets.json")
        if (setsFile.exists()) {
            val setsJson = json.parseToJsonElement(setsFile.readText()).jsonArray
            for (setElement in setsJson) {
                val set = setElement.jsonObject
                val id = set.getString("id") ?: continue
                val serieId = set.getNestedString("serie", "id") ?: continue
                val name = set.getString("name") ?: id
                val logoUrl = set.getString("logo")
                val symbolUrl = set.getString("symbol")
                val cardCountTotal = set.getNestedInt("cardCount", "total") ?: 0
                val cardCountOfficial = set.getNestedInt("cardCount", "official") ?: cardCountTotal
                val releaseDate = set.getString("releaseDate")

                db.tcgdexQueries.insertSet(
                    id = id,
                    language = language,
                    serieId = serieId,
                    name = name,
                    logoUrl = logoUrl,
                    symbolUrl = symbolUrl,
                    cardCountTotal = cardCountTotal.toLong(),
                    cardCountOfficial = cardCountOfficial.toLong(),
                    releaseDate = releaseDate,
                )
            }
            println("[Tcgdex]   Sets: ${setsJson.size}")
        }

        // Load cards
        val cardsFile = File(langDir, "cards.json")
        if (cardsFile.exists()) {
            val cardsJson = json.parseToJsonElement(cardsFile.readText()).jsonArray
            for (cardElement in cardsJson) {
                val card = cardElement.jsonObject
                insertCard(language, card, originLanguage = language)
                if (language == "en") {
                    val id = card.getString("id")
                    if (id != null) {
                        englishCardCache[id] = card
                    }
                }
            }
            println("[Tcgdex]   Cards: ${cardsJson.size}")
        }
    }

    // Backfill missing French cards with English data when available
    val frenchIds = cardsByLanguage["fr"] ?: emptySet()
    val englishOnlyIds = englishCardCache.keys - frenchIds
    if (englishOnlyIds.isNotEmpty()) {
        println("[Tcgdex][!] Adding ${englishOnlyIds.size} English fallbacks to French dataset")
        englishOnlyIds.sorted().forEach { id ->
            val card = englishCardCache[id] ?: return@forEach
            insertCard(language = "fr", card = card, originLanguage = "en")
        }
    }

    // Insert illustrators and rarities (language-agnostic)
    println("[Tcgdex] Inserting ${illustrators.size} illustrators...")
    for ((id, name) in illustrators) {
        db.tcgdexQueries.insertIllustrator(id, name)
    }

    println("[Tcgdex] Inserting ${rarities.size} rarities...")
    for ((id, name) in rarities) {
        db.tcgdexQueries.insertRarity(id, name)
    }

    // Set the logical database version for runtime installation guards.
    // This must stay in sync with TcgdexDatabaseInstaller.DATABASE_USER_VERSION.
    driver.execute(null, "PRAGMA user_version = ${TcgdexDatabaseInstaller.DATABASE_USER_VERSION}", 0)
    println("[Tcgdex] Set user_version = ${TcgdexDatabaseInstaller.DATABASE_USER_VERSION}")

    driver.close()
    println("[Tcgdex] Database generation complete: ${outputFile.absolutePath}")
    println("[Tcgdex] File size: ${outputFile.length() / 1024 / 1024} MB")
    
    // Generate missing images CSV index
    if (missingImages.isNotEmpty()) {
        val outputDir = outputFile.parentFile ?: File(".")
        MissingImagesIndexGenerator.generateCsv(missingImages, outputDir)
        
        // Log summary by language
        val byLanguage = missingImages.groupBy { it.language }
        for ((lang, entries) in byLanguage.toSortedMap()) {
            val withFallback = entries.count { it.pokecardexUrl != null }
            println("[Tcgdex]   $lang: ${entries.size} missing (${withFallback} with Pokecardex fallback)")
        }
    } else {
        println("[Tcgdex] No missing images detected")
    }
}

private data class Config(
    val datasetDir: String,
    val languages: List<String>,
    val outputFile: String,
    val force: Boolean,
)

private fun parseArgs(args: Array<String>): Config {
    var datasetDir = ""
    var languages = listOf("en", "fr")
    var outputFile = "tcgdex.db"
    var force = false

    for (arg in args) {
        when {
            arg.startsWith("--dataset=") -> datasetDir = arg.removePrefix("--dataset=")
            arg.startsWith("--languages=") -> languages = arg.removePrefix("--languages=").split(",").map { it.trim() }
            arg.startsWith("--output=") -> outputFile = arg.removePrefix("--output=")
            arg.startsWith("--force=") -> force = arg.removePrefix("--force=").toBoolean()
        }
    }

    require(datasetDir.isNotBlank()) { "Missing required argument: --dataset=/path/to/generated" }
    return Config(datasetDir, languages, outputFile, force)
}

private fun JsonObject.getString(key: String): String? {
    val element = this[key] ?: return null
    if (element is JsonNull) return null
    return element.jsonPrimitive.contentOrNull
}

private fun JsonObject.getNestedString(vararg keys: String): String? {
    var current: JsonElement = this
    for (key in keys) {
        current = (current as? JsonObject)?.get(key) ?: return null
        if (current is JsonNull) return null
    }
    return current.jsonPrimitive.contentOrNull
}

private fun JsonObject.getNestedInt(vararg keys: String): Int? {
    var current: JsonElement = this
    for (key in keys) {
        current = (current as? JsonObject)?.get(key) ?: return null
        if (current is JsonNull) return null
    }
    return current.jsonPrimitive.intOrNull
}

private fun JsonObject.getIntArray(key: String): List<Int>? {
    val element = this[key] ?: return null
    if (element is JsonNull) return null
    if (element !is JsonArray) return null
    return element.mapNotNull { it.jsonPrimitive.intOrNull }
}

private fun JsonObject.getStringArray(key: String): List<String>? {
    val element = this[key] ?: return null
    if (element is JsonNull) return null
    if (element !is JsonArray) return null
    return element.mapNotNull { it.jsonPrimitive.contentOrNull }
}

private fun slugify(text: String): String {
    return text
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
}

private fun extractNumericPart(localId: String): Int {
    // Extract leading numeric part for sorting (e.g., "001" -> 1, "TG01" -> 1, "SWSH001" -> 1)
    val numericPart = localId.filter { it.isDigit() }
    return numericPart.toIntOrNull() ?: 0
}

