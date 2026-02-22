@file:Suppress("ktlint:standard:filename")

package app.cardium.kmptcgdexsdk.generator

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cardium.tcgdex.db.TcgdexDatabase
import app.cardium.tcgdex.sdk.storage.TcgdexDatabaseInstaller
import java.io.File
import java.net.URI
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.Serializable

/**
 * Cardmarket price entry for a single product.
 * Mapped from price_guide_6.json structure.
 */
data class CardmarketPrice(
    val trendPrice: Double?,
    val averageSellPrice: Double?,
    val lowPrice: Double?,
    val updatedIso: String,
    val unit: String = "EUR",
)

data class CardmarketExportPrice(
    val medianPrice: Double?,
    val avgPrice: Double?,
    val minPrice: Double?,
    val maxPrice: Double?,
    val recommendedPrice: Double?,
    val availableCount: Int?,
    val currency: String?,
)

data class CardmarketExportVariant(
    val version: String?,
    val productId: Int?,
    val label: String?,
    // prices[priceLanguage][sellerCountry] -> CardmarketExportPrice
    val prices: Map<String, Map<String, CardmarketExportPrice>>,
)

data class CardmarketExportCard(
    val tcgdexCardId: String,
    val name: String?,
    val variants: List<CardmarketExportVariant>,
)

data class CardmarketExportPrices(
    val updatedIso: String,
    val cards: Map<String, CardmarketExportCard>,
)

/**
 * Pokémon species data with localized names and evolution chain.
 */
data class PokemonSpeciesData(
    val dexId: Int,
    val names: Map<String, String>,
    val evolvesFrom: Int?,
    val evolvesTo: List<Int>,
)

/**
 * Database generator for the offline TCGdex SQLite database.
 *
 * Reads JSON files from the cards-database server/generated directory
 * and populates a SQLite database file. Fetches and embeds Cardmarket EUR
 * pricing at build time.
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
    val defaultPokepediaFile = config.pokepediaMissingFile
        ?: resolvePokepediaMissingTree(config.datasetDir)?.absolutePath
    val pokepediaFallbacks = loadPokepediaFallbacks(
        missingFilePath = defaultPokepediaFile,
        json = json,
    )

    // Track unique illustrators and rarities (language-agnostic)
    val illustrators = mutableMapOf<String, String>() // id -> name
    val rarities = mutableMapOf<String, String>() // id -> name

    // Load Pokémon species data for canonical names
    // Map: dexId -> Map<language, name>
    val pokemonSpecies = loadPokemonSpecies(config.datasetDir, json)
    println("[Tcgdex] Loaded ${pokemonSpecies.size} Pokémon species entries")
    val pokemonNameIndex = buildPokemonNameIndex(pokemonSpecies)

    // Load Cardmarket price guide for EUR pricing
    // Map: idProduct (Int) -> CardmarketPrice
    val cardmarketPrices = loadCardmarketPrices(json)
    println("[Tcgdex] Loaded ${cardmarketPrices.size} Cardmarket price entries")

    // Load internal Cardmarket export (language-specific)
    val exportPricingData = loadCardmarketExportPrices(config.cardmarketExportFile, json)
    val cardmarketExportCards = exportPricingData?.cards ?: emptyMap()
    val exportUpdatedIso = exportPricingData?.updatedIso?.takeIf { it.isNotBlank() }
    if (cardmarketExportCards.isNotEmpty()) {
        println("[Tcgdex] Loaded ${cardmarketExportCards.size} Cardmarket export cards")
    }

    // Track missing images for Pokecardex fallback index
    val missingImages = mutableListOf<MissingImagesIndexGenerator.MissingImageEntry>()
    // Track all cards per language for cross-language comparison
    val cardsByLanguage = mutableMapOf<String, MutableSet<String>>() // language -> set of card IDs
    val englishCardCache = mutableMapOf<String, JsonObject>()
    
    // Valid dex IDs from pokemon-species.json (used for validation)
    val validDexIds = pokemonSpecies.keys

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
        val fallbackImageUrl =
            if (language.equals("fr", ignoreCase = true)) {
                pokepediaFallbacks[id]
            } else {
                null
            }
        val fallbackImageSource = fallbackImageUrl?.let { POKEPEDIA_SOURCE }

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

        val types = card.getStringArray("types")?.joinToString(",")
        val category = card.getString("category")
        val supertype = card.getString("supertype")
        val regulationMark = card.getString("regulationMark")
        val localNumberSort = extractNumericPart(localId)

        val resolvedDexIds = if (category == "Pokemon" && name.isNotBlank() && isMultiPokemonName(name)) {
            val expectedSpecies = countDistinctSpeciesFromName(name)
            val actualDexIds = dexIds?.toSet() ?: emptySet()
            val inferredDexIds = inferDexIdsFromName(
                name = name,
                language = language,
                pokemonNameIndex = pokemonNameIndex,
            ).toSet()
            val mergedDexIds = (actualDexIds + inferredDexIds).toList()
            if (expectedSpecies > 1 && mergedDexIds.toSet().size < expectedSpecies) {
                throw IllegalArgumentException(
                    """
                    [Tcgdex][x] INCOMPLETE MULTI-POKÉMON DEX IDS - BUILD FAILED
                    Card: $id (set: $setId, language: $language)
                    Name: $name
                    Expected $expectedSpecies dexId entries, found ${actualDexIds.size} (current: ${dexIds?.joinToString(", ") ?: "none"})
                    
                    Update libs/cards-database data so every Pokémon listed in the card name appears in dexId.
                    """.trimIndent(),
                )
            }
            if (mergedDexIds.isNotEmpty() && mergedDexIds.toSet().size > actualDexIds.size) {
                println("[Tcgdex][!] Inferred missing dexId entries for $id: ${mergedDexIds.joinToString(", ")}")
            }
            if (mergedDexIds.isNotEmpty()) mergedDexIds else dexIds
        } else {
            dexIds
        }

        // Get Cardmarket price using internal export (variant + country + price-language),
        // fall back to S3 price guide.
        //
        // Legacy columns in cards table store the default seller-country recommended price (FR),
        // so existing UI stays compatible and shows the recommended price by default.
        val exportLanguage = language.lowercase()
        val defaultSellerCountry = "FR"
        val exportCard = cardmarketExportCards[id]

        fun selectDefaultVariant(variants: List<CardmarketExportVariant>): CardmarketExportVariant? =
            variants.firstOrNull { it.version?.equals("Normal", ignoreCase = true) == true } ?: variants.firstOrNull()

        fun selectLegacyExportPrice(card: CardmarketExportCard): CardmarketExportPrice? {
            val variant = selectDefaultVariant(card.variants) ?: return null
            val byLang = variant.prices[exportLanguage] ?: variant.prices["fr"] ?: return null
            return byLang[defaultSellerCountry] ?: byLang.entries.firstOrNull()?.value
        }

        val exportLegacyPrice = exportCard?.let(::selectLegacyExportPrice)
        val cardmarketId = card.getNestedInt("thirdParty", "cardmarket")
        val s3Pricing = cardmarketId?.let { cardmarketPrices[it] }
        val pricing = if (exportLegacyPrice != null) {
            CardmarketPrice(
                trendPrice = exportLegacyPrice.recommendedPrice
                    ?: exportLegacyPrice.medianPrice
                    ?: exportLegacyPrice.avgPrice
                    ?: exportLegacyPrice.minPrice,
                averageSellPrice = exportLegacyPrice.avgPrice ?: exportLegacyPrice.medianPrice,
                lowPrice = exportLegacyPrice.minPrice,
                updatedIso = exportUpdatedIso ?: "",
                unit = exportLegacyPrice.currency ?: "EUR",
            )
        } else {
            s3Pricing
        }

        db.tcgdexQueries.insertCard(
            id = id,
            language = language,
            localId = localId,
            localNumberSort = localNumberSort.toLong(),
            setId = setId,
            rarityId = rarityId,
            illustratorId = illustratorId,
            name = name,
            imageUrl = imageUrl,
            fallbackImageUrl = fallbackImageUrl,
            fallbackImageSource = fallbackImageSource,
            originLanguage = originLanguage,
            category = category,
            types = types,
            supertype = supertype,
            regulationMark = regulationMark,
            priceCardmarketTrend = pricing?.trendPrice,
            priceCardmarketAvg = pricing?.averageSellPrice,
            priceCardmarketLow = pricing?.lowPrice,
            priceUpdatedIso = pricing?.updatedIso,
            priceUnit = pricing?.unit,
        )

        // Persist all export prices (all variants, all price languages, all seller countries).
        // This enables user-level seller-country selection in the app.
        if (exportCard != null) {
            for (variant in exportCard.variants) {
                val variantKey = variant.version?.takeIf { it.isNotBlank() }
                    ?: variant.label?.takeIf { it.isNotBlank() }
                    ?: ""
                for ((priceLang, byCountry) in variant.prices) {
                    for ((sellerCountry, price) in byCountry) {
                        db.tcgdexQueries.insertCardPrice(
                            cardId = id,
                            cardLanguage = language,
                            variant = variantKey,
                            priceLanguage = priceLang,
                            sellerCountry = sellerCountry,
                            currency = price.currency ?: "EUR",
                            minPrice = price.minPrice,
                            avgPrice = price.avgPrice,
                            medianPrice = price.medianPrice,
                            maxPrice = price.maxPrice,
                            recommendedPrice = price.recommendedPrice,
                            availableCount = price.availableCount?.toLong(),
                            productId = variant.productId?.toLong(),
                        )
                    }
                }
            }
        }

        // Insert card-Pokémon relationships for ALL dex IDs (supports multi-Pokémon cards)
        // VALIDATION: Ensure all dex IDs exist in pokemon-species.json to prevent phantom Pokédex entries
        // Skip validation if pokemon-species.json was not loaded (validDexIds empty)
        val normalizedDexIds = resolvedDexIds?.mapNotNull { dexId ->
            normalizeDexId(dexId, validDexIds)
        }

        if (validDexIds.isNotEmpty()) {
            val invalidDexIds = resolvedDexIds?.filter { dexId ->
                normalizeDexId(dexId, validDexIds) == null
            }.orEmpty()
            if (invalidDexIds.isNotEmpty()) {
                throw IllegalArgumentException(
                    """
                    [Tcgdex][x] INVALID DEX ID DETECTED - BUILD FAILED
                    Card: $id (set: $setId, language: $language)
                    Invalid dexId: ${invalidDexIds.joinToString(", ")}
                    
                    This dex ID does not exist in pokemon-species.json.
                    Possible causes:
                    1. Typo in the card data (e.g., 9012 instead of 912)
                    2. Missing species in pokemon-species.json (run 'bun run download-pokedex' in workdir)
                    
                    Fix the card data in libs/cards-database/data/... and regenerate.
                    """.trimIndent()
                )
            }
        }

        normalizedDexIds?.forEach { dexId ->
            db.tcgdexQueries.insertCardPokemon(
                cardId = id,
                language = language,
                pokemonDexId = dexId.toLong(),
            )
        }
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

    // Insert Pokémon species canonical names for ALL available languages in pokemon-species.json
    // This includes: en, fr, de, es, it, ja, ko, zh-cn, zh-tw, pt-br (and any others in the file)
    // We insert for all languages, not just config.languages, so Pokédex works in any locale
    println("[Tcgdex] Inserting Pokémon species names with evolution chains for all languages...")
    val allSpeciesLanguages = pokemonSpecies.values
        .flatMap { it.names.keys }
        .toSet()
        .sorted()
    println("[Tcgdex]   Available species languages: $allSpeciesLanguages")
    
    for (language in allSpeciesLanguages) {
        var speciesInserted = 0
        for ((dexId, species) in pokemonSpecies) {
            // Use localized name if available, otherwise fall back to English
            val name = species.names[language] ?: species.names["en"] ?: continue
            val evolvesToStr = species.evolvesTo.joinToString(",")
            db.tcgdexQueries.insertPokemonSpecies(
                dexId = dexId.toLong(),
                language = language,
                name = name,
                evolvesFrom = species.evolvesFrom?.toLong(),
                evolvesTo = evolvesToStr.ifEmpty { null },
            )
            speciesInserted++
        }
        println("[Tcgdex]   $language: $speciesInserted species")
    }
    
    // Also insert for pt (Portuguese) mapped from pt-br if pt-br exists
    // TCGdex uses "pt" but pokemon-species.json uses "pt-br"
    if ("pt-br" in allSpeciesLanguages && "pt" !in allSpeciesLanguages) {
        var speciesInserted = 0
        for ((dexId, species) in pokemonSpecies) {
            val name = species.names["pt-br"] ?: species.names["en"] ?: continue
            val evolvesToStr = species.evolvesTo.joinToString(",")
            db.tcgdexQueries.insertPokemonSpecies(
                dexId = dexId.toLong(),
                language = "pt",
                name = name,
                evolvesFrom = species.evolvesFrom?.toLong(),
                evolvesTo = evolvesToStr.ifEmpty { null },
            )
            speciesInserted++
        }
        println("[Tcgdex]   pt (from pt-br): $speciesInserted species")
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

    // Ensure database is in a portable state for iOS compatibility:
    // 1. Set journal_mode to DELETE (not WAL) for bundled databases
    // 2. Run integrity check to verify database is valid
    // 3. VACUUM to compact and ensure clean state
    driver.execute(null, "PRAGMA journal_mode = DELETE", 0)
    driver.execute(null, "VACUUM", 0)
    println("[Tcgdex] Database vacuumed for iOS compatibility")

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
    
    // Regenerate unresolved JSON file from tree file
    if (defaultPokepediaFile != null) {
        val treeFile = File(defaultPokepediaFile)
        if (treeFile.exists()) {
            regenerateUnresolvedFile(treeFile, json)
        }
    }
}

private data class Config(
    val datasetDir: String,
    val languages: List<String>,
    val outputFile: String,
    val force: Boolean,
    val pokepediaMissingFile: String?,
    val cardmarketExportFile: String?,
)

private fun parseArgs(args: Array<String>): Config {
    var datasetDir = ""
    var languages = listOf("en", "fr")
    var outputFile = "tcgdex.db"
    var force = false
    var pokepediaMissingFile: String? = null
    var cardmarketExportFile: String? = null

    for (arg in args) {
        when {
            arg.startsWith("--dataset=") -> datasetDir = arg.removePrefix("--dataset=")
            arg.startsWith("--languages=") -> languages = arg.removePrefix("--languages=").split(",").map { it.trim() }
            arg.startsWith("--output=") -> outputFile = arg.removePrefix("--output=")
            arg.startsWith("--force=") -> force = arg.removePrefix("--force=").toBoolean()
            arg.startsWith("--pokepedia-missing=") -> pokepediaMissingFile = arg.removePrefix("--pokepedia-missing=")
            arg.startsWith("--cardmarket-export=") -> cardmarketExportFile = arg.removePrefix("--cardmarket-export=")
        }
    }

    require(datasetDir.isNotBlank()) { "Missing required argument: --dataset=/path/to/generated" }
    return Config(datasetDir, languages, outputFile, force, pokepediaMissingFile, cardmarketExportFile)
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

private fun JsonObject.getDouble(key: String): Double? {
    val element = this[key] ?: return null
    if (element is JsonNull) return null
    return element.jsonPrimitive.doubleOrNull
}

private fun JsonObject.getIntArray(key: String): List<Int>? {
    val element = this[key] ?: return null
    if (element is JsonNull) return null
    if (element !is JsonArray) return null
    return element.mapNotNull { it.jsonPrimitive.intOrNull }
}

private val CARD_SUFFIXES = listOf(
    " EX",
    " ex",
    "-EX",
    "-ex",
    " GX",
    "-GX",
    " V",
    "-V",
    " VMAX",
    " VSTAR",
    " V-UNION",
    " BREAK",
    " LV.X",
    " Prime",
    " PRIME",
    " SP",
    " FB",
    " GL",
    " C",
    " G",
    " E4",
    " δ",
    " Star",
    " ☆",
    "★",
    " LEGEND",
    "-LEGEND",
)

private val REGIONAL_PATTERNS = listOf(
    Regex("^Alolan ", RegexOption.IGNORE_CASE),
    Regex("^Alola ", RegexOption.IGNORE_CASE),
    Regex("^Galarian ", RegexOption.IGNORE_CASE),
    Regex("^Galar ", RegexOption.IGNORE_CASE),
    Regex("^Hisuian ", RegexOption.IGNORE_CASE),
    Regex("^Hisui ", RegexOption.IGNORE_CASE),
    Regex("^Paldean ", RegexOption.IGNORE_CASE),
    Regex("^Paldea ", RegexOption.IGNORE_CASE),
    Regex("d'Alola$", RegexOption.IGNORE_CASE),
    Regex("de Galar$", RegexOption.IGNORE_CASE),
    Regex("de Hisui$", RegexOption.IGNORE_CASE),
    Regex("de Paldea$", RegexOption.IGNORE_CASE),
    Regex("^Alola-", RegexOption.IGNORE_CASE),
    Regex("^Galar-", RegexOption.IGNORE_CASE),
    Regex("^Hisui-", RegexOption.IGNORE_CASE),
    Regex("^Paldea-", RegexOption.IGNORE_CASE),
)

private val MEGA_PATTERNS = listOf(
    Regex("^Mega ", RegexOption.IGNORE_CASE),
    Regex("^M ", RegexOption.IGNORE_CASE),
    Regex("^Méga-", RegexOption.IGNORE_CASE),
    Regex("^Méga ", RegexOption.IGNORE_CASE),
    Regex("^M-", RegexOption.IGNORE_CASE),
)

private val SPECIAL_FORM_PATTERNS = listOf(
    Regex("^Primal ", RegexOption.IGNORE_CASE),
    Regex("^Primo-", RegexOption.IGNORE_CASE),
    Regex("^Origin Forme ", RegexOption.IGNORE_CASE),
    Regex("^Altered Forme ", RegexOption.IGNORE_CASE),
    Regex("^Sky Forme ", RegexOption.IGNORE_CASE),
    Regex("^Land Forme ", RegexOption.IGNORE_CASE),
    Regex("^Therian Forme ", RegexOption.IGNORE_CASE),
    Regex("^Incarnate Forme ", RegexOption.IGNORE_CASE),
    Regex("^Black Kyurem", RegexOption.IGNORE_CASE),
    Regex("^White Kyurem", RegexOption.IGNORE_CASE),
    Regex("^Dusk Mane ", RegexOption.IGNORE_CASE),
    Regex("^Dawn Wings ", RegexOption.IGNORE_CASE),
    Regex("^Ultra ", RegexOption.IGNORE_CASE),
    Regex("^Crowned ", RegexOption.IGNORE_CASE),
    Regex("^Ice Rider ", RegexOption.IGNORE_CASE),
    Regex("^Shadow Rider ", RegexOption.IGNORE_CASE),
    Regex("^Single Strike ", RegexOption.IGNORE_CASE),
    Regex("^Rapid Strike ", RegexOption.IGNORE_CASE),
    Regex("^Bloodmoon ", RegexOption.IGNORE_CASE),
    Regex("^Rocket's ", RegexOption.IGNORE_CASE),
    Regex("^Dark ", RegexOption.IGNORE_CASE),
    Regex("^Light ", RegexOption.IGNORE_CASE),
    Regex("^Shining ", RegexOption.IGNORE_CASE),
    Regex("^_____'s ", RegexOption.IGNORE_CASE),
    Regex("^Radiant ", RegexOption.IGNORE_CASE),
    Regex(" with .+$", RegexOption.IGNORE_CASE),
    Regex("^Surfing ", RegexOption.IGNORE_CASE),
    Regex("^Flying ", RegexOption.IGNORE_CASE),
    Regex("^Detective ", RegexOption.IGNORE_CASE),
)

private val TAG_TEAM_SEPARATORS = listOf(" & ", " et ", " und ", " e ", " y ")

private fun isMultiPokemonName(name: String): Boolean {
    return TAG_TEAM_SEPARATORS.any { name.contains(it) }
}

private fun splitMultiPokemonName(name: String): List<String> {
    for (separator in TAG_TEAM_SEPARATORS) {
        if (name.contains(separator)) {
            return name.split(separator).map { it.trim() }
        }
    }
    return listOf(name)
}

private fun countDistinctSpeciesFromName(name: String): Int {
    val parts = splitMultiPokemonName(name)
    val normalized = parts.map { normalizePokemonName(it) }.filter { it.isNotBlank() }
    val distinct = normalized.toSet().size
    return if (distinct > 0) distinct else parts.size
}

private fun normalizePokemonName(rawName: String): String {
    var normalized = rawName.trim()
    normalized = normalized.replace('\u2019', '\'')

    CARD_SUFFIXES.forEach { suffix ->
        if (normalized.endsWith(suffix)) {
            normalized = normalized.dropLast(suffix.length).trim()
        }
    }

    REGIONAL_PATTERNS.forEach { pattern ->
        normalized = normalized.replace(pattern, "").trim()
    }

    MEGA_PATTERNS.forEach { pattern ->
        normalized = normalized.replace(pattern, "").trim()
    }

    SPECIAL_FORM_PATTERNS.forEach { pattern ->
        normalized = normalized.replace(pattern, "").trim()
    }

    normalized = normalized.replace(Regex(" [XY]$", RegexOption.IGNORE_CASE), "").trim()

    return normalized.lowercase()
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

private fun buildPokemonNameIndex(
    pokemonSpecies: Map<Int, PokemonSpeciesData>,
): Map<String, Map<String, Int>> {
    val index = mutableMapOf<String, MutableMap<String, Int>>()
    for ((dexId, species) in pokemonSpecies) {
        for ((language, name) in species.names) {
            val normalized = normalizePokemonName(name)
            if (normalized.isBlank()) continue
            val langIndex = index.getOrPut(language.lowercase()) { mutableMapOf() }
            langIndex.putIfAbsent(normalized, dexId)
        }
    }
    return index
}

private fun inferDexIdsFromName(
    name: String,
    language: String,
    pokemonNameIndex: Map<String, Map<String, Int>>,
): List<Int> {
    val parts = splitMultiPokemonName(name)
    val langIndex = pokemonNameIndex[language.lowercase()]
    val enIndex = pokemonNameIndex["en"]
    return parts.mapNotNull { part ->
        val normalized = normalizePokemonName(part)
        langIndex?.get(normalized) ?: enIndex?.get(normalized)
    }
}

private fun normalizeDexId(dexId: Int, validDexIds: Set<Int>): Int? {
    if (validDexIds.isEmpty()) {
        return dexId
    }
    if (dexId in validDexIds) {
        return dexId
    }
    val dexString = dexId.toString()
    if (dexString.startsWith("90") && dexString.length >= 3) {
        val normalized = (dexString.first() + dexString.drop(2)).toIntOrNull()
        if (normalized != null && normalized in validDexIds) {
            println("[Tcgdex][!] Normalized invalid dexId $dexId -> $normalized")
            return normalized
        }
    }
    return null
}

private fun extractNumericPart(localId: String): Int {
    // Extract leading numeric part for sorting (e.g., "001" -> 1, "TG01" -> 1, "SWSH001" -> 1)
    val numericPart = localId.filter { it.isDigit() }
    return numericPart.toIntOrNull() ?: 0
}

/**
 * Loads Pokémon species data from the pokemon-species.json file.
 *
 * This data is used to populate the `pokemon_species` table with canonical species names
 * from the official Pokédex. This fixes the issue where TAG TEAM card names (e.g.,
 * "Celebi et Florizarre GX") would appear in the Pokédex list instead of the canonical
 * species name (e.g., "Florizarre" / "Venusaur").
 *
 * The file is located in the workdir directory relative to the dataset directory:
 * `libs/cards-database/workdir/pokemon-species.json`
 *
 * This file is downloaded from PokeAPI using `bun run download-pokedex` in the workdir.
 *
 * @param datasetDir Path to the dataset directory (e.g., libs/cards-database/server/generated)
 * @param json JSON parser instance
 * @return Map of dexId -> Map<language, name> for canonical species names
 *
 * @see docs/POKEDEX_DATA_REMEDIATION.md for more details
 */
private fun resolvePokemonSpeciesFile(datasetDir: String): File? {
    val repositoryRoot = File(datasetDir).parentFile?.parentFile
    val candidates = listOfNotNull(
        repositoryRoot?.resolve("workdir")?.resolve("pokemon-species.json"),
        repositoryRoot?.resolve("scripts")?.resolve("pokedexIdFixer")?.resolve("pokemon-species.json"),
    )

    for (candidate in candidates) {
        if (candidate.exists()) {
            return candidate
        }
    }
    return null
}

private fun resolvePokepediaMissingTree(datasetDir: String): File? {
    val projectRoot = File(datasetDir)
        .parentFile // server
        ?.parentFile // cards-database
        ?.parentFile // libs
        ?.parentFile // project root
    val candidate = projectRoot
        ?.resolve("libs")
        ?.resolve("tcgdex-kmp-sdk")
        ?.resolve("resources")
        ?.resolve("pokepedia")
        ?.resolve("missing-fr-card-images-tree.json")
    return candidate?.takeIf { it.exists() }
}

private fun loadPokemonSpecies(datasetDir: String, json: Json): Map<Int, PokemonSpeciesData> {
    // pokemon-species.json is expected in libs/cards-database/workdir/, but we also
    // support the monorepo layout where the tools live under scripts/pokedexIdFixer.
    val speciesFile = resolvePokemonSpeciesFile(datasetDir)

    if (speciesFile == null || !speciesFile.exists()) {
        println("[Tcgdex][!] pokemon-species.json not found. Looked under workdir/ and scripts/pokedexIdFixer/")
        return emptyMap()
    }

    println("[Tcgdex] Loading pokemon-species.json from ${speciesFile.absolutePath}")

    val result = mutableMapOf<Int, PokemonSpeciesData>()

    try {
        val speciesArray = json.parseToJsonElement(speciesFile.readText()).jsonArray
        for (element in speciesArray) {
            val obj = element.jsonObject
            val dexId = obj["dexId"]?.jsonPrimitive?.intOrNull ?: continue
            val englishName = obj["englishName"]?.jsonPrimitive?.contentOrNull
            val namesObj = obj["names"]?.jsonObject

            val names = mutableMapOf<String, String>()

            // Add English name as fallback
            if (englishName != null) {
                names["en"] = englishName
            }

            // Add localized names
            namesObj?.forEach { (lang, nameElement) ->
                val name = nameElement.jsonPrimitive.contentOrNull
                if (name != null) {
                    names[lang] = name
                }
            }

            // Load evolution data
            val evolvesFrom = obj["evolvesFrom"]?.let {
                if (it is JsonNull) null else it.jsonPrimitive.intOrNull
            }
            val evolvesTo = obj["evolvesTo"]?.jsonArray?.mapNotNull {
                it.jsonPrimitive.intOrNull
            } ?: emptyList()

            if (names.isNotEmpty()) {
                result[dexId] = PokemonSpeciesData(
                    dexId = dexId,
                    names = names,
                    evolvesFrom = evolvesFrom,
                    evolvesTo = evolvesTo,
                )
            }
        }
    } catch (e: Exception) {
        println("[Tcgdex][x] Error loading pokemon-species.json: ${e.message}")
    }

    return result
}

/**
 * Loads Cardmarket price guide data from the official price_guide_6.json endpoint.
 *
 * The price guide is fetched at build time to embed pricing into the offline database.
 * This allows price hydration without runtime network calls.
 *
 * Price guide URL: https://downloads.s3.cardmarket.com/productCatalog/priceGuide/price_guide_6.json
 *
 * The response has this structure:
 * ```json
 * {
 *   "version": 1,
 *   "createdAt": "2025-06-05T12:00:00.000Z",
 *   "priceGuides": [
 *     {
 *       "idProduct": 123456,
 *       "idCategory": 6,
 *       "avg": 1.50,
 *       "low": 0.50,
 *       "trend": 1.75,
 *       ...
 *     }
 *   ]
 * }
 * ```
 *
 * @param json JSON parser instance
 * @return Map of idProduct -> CardmarketPrice
 */
private fun loadCardmarketPrices(json: Json): Map<Int, CardmarketPrice> {
    val priceGuideUrl = "https://downloads.s3.cardmarket.com/productCatalog/priceGuide/price_guide_6.json"
    val result = mutableMapOf<Int, CardmarketPrice>()

    try {
        println("[Tcgdex] Fetching Cardmarket price guide...")
        val connection = URI(priceGuideUrl).toURL().openConnection()
        connection.connectTimeout = 30_000
        connection.readTimeout = 60_000
        val responseText = connection.getInputStream().bufferedReader().readText()

        val root = json.parseToJsonElement(responseText).jsonObject

        val version = root["version"]?.jsonPrimitive?.intOrNull
        if (version != 1) {
            println("[Tcgdex][!] Unexpected price guide version: $version (expected 1)")
        }

        val createdAt = root["createdAt"]?.jsonPrimitive?.contentOrNull ?: ""
        val priceGuides = root["priceGuides"]?.jsonArray ?: JsonArray(emptyList())

        for (element in priceGuides) {
            val pg = element.jsonObject
            val idProduct = pg["idProduct"]?.jsonPrimitive?.intOrNull ?: continue

            val trendPrice = pg["trend"]?.jsonPrimitive?.doubleOrNull
            val avgPrice = pg["avg"]?.jsonPrimitive?.doubleOrNull
            val lowPrice = pg["low"]?.jsonPrimitive?.doubleOrNull

            // Skip entries with no pricing data
            if (trendPrice == null && avgPrice == null && lowPrice == null) continue

            result[idProduct] = CardmarketPrice(
                trendPrice = trendPrice,
                averageSellPrice = avgPrice,
                lowPrice = lowPrice,
                updatedIso = createdAt,
                unit = "EUR",
            )
        }

        println("[Tcgdex] Price guide updated: $createdAt")
    } catch (e: Exception) {
        println("[Tcgdex][!] Failed to fetch Cardmarket prices: ${e.message}")
        println("[Tcgdex][!] Continuing without pricing data...")
    }

    return result
}

private fun loadCardmarketExportPrices(
    exportFilePath: String?,
    json: Json,
): CardmarketExportPrices? {
    if (exportFilePath.isNullOrBlank()) {
        println("[Tcgdex][i] No Cardmarket export file provided, skipping export import")
        return null
    }

    val file = File(exportFilePath)
    if (!file.exists()) {
        println("[Tcgdex][!] Cardmarket export file not found: $exportFilePath")
        return null
    }

    return runCatching {
        fun JsonObject.stringOrNull(key: String): String? =
            (this[key] as? JsonPrimitive)?.contentOrNull

        fun JsonObject.intOrNullSafe(key: String): Int? =
            (this[key] as? JsonPrimitive)?.intOrNull

        fun JsonObject.doubleOrNullSafe(key: String): Double? =
            (this[key] as? JsonPrimitive)?.doubleOrNull

        val root = json.parseToJsonElement(file.readText()).jsonObject
        val exportDate = root.stringOrNull("exportDate") ?: ""
        val series = (root["series"] as? JsonArray) ?: JsonArray(emptyList())

        val cardsById = mutableMapOf<String, CardmarketExportCard>()
        val languages = mutableSetOf<String>()
        val countries = mutableSetOf<String>()
        var cardCount = 0
        var variantCount = 0
        var pricedVariantCount = 0
        var priceEntryCount = 0

        for (seriesElement in series) {
            val seriesObj = seriesElement as? JsonObject ?: continue
            val sets = (seriesObj["sets"] as? JsonArray) ?: JsonArray(emptyList())
            for (setElement in sets) {
                val setObj = setElement as? JsonObject ?: continue
                val cards = (setObj["cards"] as? JsonArray) ?: JsonArray(emptyList())
                for (cardElement in cards) {
                    val cardObj = cardElement as? JsonObject ?: continue
                    val cardId = cardObj.stringOrNull("tcgdexCardId")?.trim()
                    if (cardId.isNullOrBlank()) continue
                    cardCount++

                    val cardName = cardObj.stringOrNull("name")
                    val variantsArray = (cardObj["variants"] as? JsonArray) ?: JsonArray(emptyList())
                    if (variantsArray.isEmpty()) continue

                    val variants = buildList {
                        for (variantElement in variantsArray) {
                            val variantObj = variantElement as? JsonObject ?: continue
                            val version = variantObj.stringOrNull("version")?.trim()
                            val productId = variantObj.intOrNullSafe("productId")
                            val label = variantObj.stringOrNull("label")?.trim()

                            val pricesObj = variantObj["prices"] as? JsonObject
                            if (pricesObj == null || pricesObj.isEmpty()) continue

                            val prices: MutableMap<String, MutableMap<String, CardmarketExportPrice>> = mutableMapOf()
                            var hasAnyPrice = false

                            for ((priceLangRaw, priceLangElement) in pricesObj) {
                                val priceLang = priceLangRaw.lowercase().trim()
                                val byCountryObj = priceLangElement as? JsonObject ?: continue

                                for ((countryRaw, countryElement) in byCountryObj) {
                                    val country = countryRaw.uppercase().trim()
                                    val priceObj = countryElement as? JsonObject ?: continue

                                    val exportPrice = CardmarketExportPrice(
                                        medianPrice = priceObj.doubleOrNullSafe("medianPrice"),
                                        avgPrice = priceObj.doubleOrNullSafe("avgPrice"),
                                        minPrice = priceObj.doubleOrNullSafe("minPrice"),
                                        maxPrice = priceObj.doubleOrNullSafe("maxPrice"),
                                        recommendedPrice = priceObj.doubleOrNullSafe("recommendedPrice"),
                                        availableCount = priceObj.intOrNullSafe("availableCount"),
                                        currency = priceObj.stringOrNull("currency"),
                                    )

                                    // Skip entries with absolutely no numeric pricing data
                                    if (
                                        exportPrice.recommendedPrice == null &&
                                        exportPrice.medianPrice == null &&
                                        exportPrice.avgPrice == null &&
                                        exportPrice.minPrice == null &&
                                        exportPrice.maxPrice == null
                                    ) {
                                        continue
                                    }

                                    val langMap = prices.getOrPut(priceLang) { mutableMapOf() }
                                    langMap[country] = exportPrice
                                    languages.add(priceLang)
                                    countries.add(country)
                                    priceEntryCount++
                                    hasAnyPrice = true
                                }
                            }

                            if (!hasAnyPrice) continue

                            variantCount++
                            pricedVariantCount++
                            add(
                                CardmarketExportVariant(
                                    version = version,
                                    productId = productId,
                                    label = label,
                                    prices = prices,
                                ),
                            )
                        }
                    }

                    if (variants.isEmpty()) continue

                    cardsById[cardId] = CardmarketExportCard(
                        tcgdexCardId = cardId,
                        name = cardName,
                        variants = variants,
                    )
                }
            }
        }

        println(
            "[Tcgdex] Cardmarket export loaded: cards=$cardCount cardsWithPrices=${cardsById.size} " +
                "variants=$variantCount pricedVariants=$pricedVariantCount entries=$priceEntryCount " +
                "languages=${languages.sorted()} countries=${countries.sorted()} updated=$exportDate",
        )

        CardmarketExportPrices(
            updatedIso = exportDate,
            cards = cardsById,
        )
    }.onFailure {
        println("[Tcgdex][!] Failed to load Cardmarket export: ${it.message}")
    }.getOrNull()
}

// =============================================================================
// Poképedia fallback harvesting
// =============================================================================

private const val POKEPEDIA_SOURCE = "pokepedia"

@Serializable
private data class MissingFrRoot(
    val generatedAt: String? = null,
    val databasePath: String? = null,
    val language: String? = null,
    val totalCards: Int? = null,
    val resolvedCards: Int? = null,
    val unresolvedCards: Int? = null,
    val unresolvedReasons: Map<String, Int> = emptyMap(),
    val series: List<MissingFrSeries> = emptyList(),
)

@Serializable
private data class MissingFrSeries(
    val seriesId: String? = null,
    val name: String? = null,
    val position: Int? = null,
    val sets: List<MissingFrSet> = emptyList(),
)

@Serializable
private data class MissingFrSet(
    val setId: String? = null,
    val name: String? = null,
    val releaseDate: String? = null,
    val cardCountTotal: Int? = null,
    val pokepediaSetUrl: String? = null,
    val cards: List<JsonObject> = emptyList(),
)

private fun MissingFrRoot.flattenCards(): List<JsonObject> =
    series.flatMap { serie -> serie.sets }.flatMap { it.cards }

private fun loadPokepediaFallbacks(
    missingFilePath: String?,
    json: Json,
): Map<String, String> {
    if (missingFilePath.isNullOrBlank()) {
        println("[Tcgdex][i] Pokepedia missing tree not provided, skipping fallback import")
        return emptyMap()
    }

    val file = File(missingFilePath)
    if (!file.exists()) {
        println("[Tcgdex][!] Pokepedia missing tree not found: $missingFilePath")
        return emptyMap()
    }

    return runCatching {
        val root = json.decodeFromString<MissingFrRoot>(file.readText())
        val cards = root.flattenCards()

        if (cards.isEmpty()) {
            println("[Tcgdex][i] Pokepedia missing tree contained no cards")
            return emptyMap()
        }

        val resolved = mutableMapOf<String, String>()
        var skipped = 0

        for (entry in cards) {
            val cardId = entry["cardId"]?.jsonPrimitive?.contentOrNull?.trim()
            if (cardId.isNullOrBlank()) continue

            val reason = entry["reason"]?.jsonPrimitive?.contentOrNull
            if (reason.equals("POKEPEDIA_THUMBNAIL_MISSING", ignoreCase = true)) {
                skipped++
                continue
            }

            // Use pre-resolved URLs from the tree (pokepediaHdUrl or pokepediaThumbnailUrl)
            val preResolvedUrl = entry["pokepediaHdUrl"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
                ?: entry["pokepediaThumbnailUrl"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }

            if (preResolvedUrl != null) {
                resolved[cardId] = preResolvedUrl
            }
        }

        println("[Tcgdex][i] Pokepedia fallbacks loaded: total=${resolved.size}, skipped=$skipped")
        resolved
    }.onFailure {
        println("[Tcgdex][x] Failed to load Pokepedia fallback data: ${it.message}")
    }.getOrDefault(emptyMap())
}

/**
 * Regenerates the unresolved JSON file from the tree file by filtering only unresolved cards.
 * This ensures both files stay synchronized after database generation.
 */
private fun regenerateUnresolvedFile(treeFile: File, json: Json) {
    runCatching {
        val root = json.decodeFromString<MissingFrRoot>(treeFile.readText())

        val unresolvedSeries = root.series.mapNotNull { series ->
            val unresolvedSets = series.sets.mapNotNull { set ->
                val unresolvedCards = set.cards.filter { card ->
                    val status = card["resolutionStatus"]?.jsonPrimitive?.contentOrNull
                    status.equals("unresolved", ignoreCase = true)
                }

                if (unresolvedCards.isNotEmpty()) {
                    set.copy(
                        cards = unresolvedCards,
                        cardCountTotal = unresolvedCards.size,
                    )
                } else {
                    null
                }
            }

            if (unresolvedSets.isNotEmpty()) {
                series.copy(sets = unresolvedSets)
            } else {
                null
            }
        }

        val unresolvedCards = unresolvedSeries.flatMap { it.sets }.flatMap { it.cards }
        val unresolvedCount = unresolvedCards.size
        val unresolvedReasons = unresolvedCards.mapNotNull { card ->
            card["reason"]?.jsonPrimitive?.contentOrNull
        }.groupingBy { it }.eachCount()

        val unresolvedRoot = root.copy(
            totalCards = unresolvedCount,
            resolvedCards = 0,
            unresolvedCards = unresolvedCount,
            unresolvedReasons = unresolvedReasons,
            series = unresolvedSeries,
        )

        val outputFile = treeFile.resolveSibling("missing-fr-card-images-unresolved.json")
        outputFile.writeText(json.encodeToString(unresolvedRoot))
        println("[Tcgdex] Regenerated unresolved file: ${outputFile.absolutePath} ($unresolvedCount cards)")
    }.onFailure {
        println("[Tcgdex][!] Failed to regenerate unresolved file: ${it.message}")
    }
}

