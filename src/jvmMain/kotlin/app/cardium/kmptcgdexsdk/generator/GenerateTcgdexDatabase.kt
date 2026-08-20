@file:Suppress("ktlint:standard:filename")

package app.cardium.kmptcgdexsdk.generator

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.cardium.tcgdex.db.TcgdexDatabase
import app.cardium.tcgdex.sdk.storage.TcgdexDatabaseInstaller
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDatabaseType
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDriver
import java.io.File
import java.net.URI
import java.text.Normalizer
import kotlinx.coroutines.runBlocking
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

/**
 * Pokémon species data with localized names and evolution chain.
 */
data class PokemonSpeciesData(
    val dexId: Int,
    val names: Map<String, String>,
    val evolvesFrom: Int?,
    val evolvesTo: List<Int>,
)

@Serializable
private data class SetAliasConfigFile(
    val version: Int = 1,
    val languages: List<String> = listOf("en", "fr"),
    val seriesPrefixAliases: Map<String, SeriesPrefixAliasConfig> = emptyMap(),
    val tokenization: SetAliasTokenizationConfig = SetAliasTokenizationConfig(),
    val setOverrides: Map<String, SetAliasOverrideConfig> = emptyMap(),
)

@Serializable
private data class SeriesPrefixAliasConfig(
    val en: List<String> = emptyList(),
    val fr: List<String> = emptyList(),
    val allowTrailingFiveDecimal: Boolean = false,
)

@Serializable
private data class SetAliasTokenizationConfig(
    val minTokenLength: Int = 3,
    val stopwords: Map<String, List<String>> = emptyMap(),
)

@Serializable
private data class SetAliasOverrideConfig(
    val extraAliases: Map<String, List<String>> = emptyMap(),
)

private data class ParsedSetAliasSource(
    val setId: String,
    val releaseDate: String?,
    val enName: String?,
    val frName: String?,
    val officialAbbreviation: String?,
    val frenchAbbreviation: String?,
    val tcgOnline: String?,
    val cardmarketExpansionId: Int?,
)

private data class GeneratedSetAliasSeed(
    val setId: String,
    val releaseDate: String?,
    val enName: String?,
    val frName: String?,
    val officialAbbreviation: String?,
    val frenchAbbreviation: String?,
    val tcgOnline: String?,
    val enExtraAliases: List<String>,
    val frExtraAliases: List<String>,
    val enSeriesAliases: List<String>,
    val frSeriesAliases: List<String>,
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
fun main(args: Array<String>) = runBlocking {
    val config = parseArgs(args)
    val projectRoot = resolveProjectRoot(config.datasetDir)
    val setAliasesConfigFile = resolveSetAliasesConfigFile(projectRoot, config.setAliasesConfigFile)
    val datasetsByLanguage = config.languages.associateWith { language ->
        resolveLanguageDataset(config.datasetDir, language)
    }

    println("[Tcgdex] Starting database generation...")
    println("[Tcgdex] Dataset: ${config.datasetDir}")
    println("[Tcgdex] Languages: ${config.languages}")
    println("[Tcgdex] Output: ${config.outputFile}")
    println("[Tcgdex] Force: ${config.force}")
    println("[Tcgdex] Set aliases config: ${setAliasesConfigFile.absolutePath}")
    println("[Tcgdex] Validated generated datasets for languages: ${datasetsByLanguage.keys.sorted()}")
    assertCameoJsonMatchesSource(File(config.datasetDir), config.languages)
    if (!config.cardmarketExpansionsFile.isNullOrBlank()) {
        println("[Tcgdex] Cardmarket expansions source: ${config.cardmarketExpansionsFile}")
    }

    generateSetAliasIndex(
        projectRoot = projectRoot,
        configFile = setAliasesConfigFile,
        cardmarketExpansionsFile = config.cardmarketExpansionsFile,
    )

    val outputFile = File(config.outputFile)
    if (outputFile.exists()) {
        if (config.force) {
            println("[Tcgdex] Deleting existing database (force=true)")
            outputFile.delete()
        } else {
            println("[Tcgdex] Database already exists, skipping generation")
            return@runBlocking
        }
    }

    outputFile.parentFile?.mkdirs()

    val driver = AndroidxSqliteDriver(
        driver = BundledSQLiteDriver(),
        databaseType = AndroidxSqliteDatabaseType.File(outputFile.absolutePath),
        schema = TcgdexDatabase.Schema,
    )
    val db = TcgdexDatabase(driver)

    val json = Json { ignoreUnknownKeys = true }
    val defaultPokepediaFile = config.pokepediaMissingFile
        ?: resolvePokepediaMissingTree(config.datasetDir)?.absolutePath
    if (defaultPokepediaFile != null) {
        println("[Tcgdex] Pokepedia missing tree source: $defaultPokepediaFile")
    }
    val pokepediaFallbacks = loadPokepediaFallbacks(
        missingFilePath = defaultPokepediaFile,
        json = json,
    )

    // CDN assets manifest, used to confirm parent-folder image URLs for sub-sets
    // (same existence source as the compiler's getCardPictures).
    val assetsManifest = loadAssetsManifest(
        manifestFileOverride = config.assetsManifestFile,
        json = json,
    )
    println("[Tcgdex] Loaded assets manifest: ${assetsManifest.size} languages")

    // Track unique illustrators and rarities (language-agnostic)
    val illustrators = mutableMapOf<String, String>() // id -> name
    val rarities = mutableMapOf<String, String>() // id -> name

    // Load Pokémon species data for canonical names
    // Map: dexId -> Map<language, name>
    val pokemonSpecies = loadPokemonSpecies(config.datasetDir, json)
    println("[Tcgdex] Loaded ${pokemonSpecies.size} Pokémon species entries")
    val pokemonNameIndex = buildPokemonNameIndex(pokemonSpecies)

    // Map localized rarity names (e.g., FR "Méga Attaque Rare") back to the English rarity
    // name (e.g., "Mega Attack Rare") so rarity IDs are always English slugs.
    val rarityReverseTranslations = loadRarityReverseTranslations(
        datasetDir = config.datasetDir,
        languages = config.languages,
        json = json,
    )

    // Load Cardmarket price guide for EUR pricing
    // Map: idProduct (Int) -> CardmarketPrice
    val cardmarketPrices = loadCardmarketPrices(json)
    println("[Tcgdex] Loaded ${cardmarketPrices.size} Cardmarket price entries")

    // Load internal Cardmarket export (language-specific)
    val exportPricingData = loadCardmarketExportPrices(
        projectRoot = projectRoot,
        exportFilePath = config.cardmarketExportFile,
        json = json,
    )
    val cardmarketExportCards = exportPricingData?.cards ?: emptyMap()
    val exportUpdatedIso = exportPricingData?.updatedIso?.takeIf { it.isNotBlank() }
    if (cardmarketExportCards.isNotEmpty()) {
        println("[Tcgdex] Loaded ${cardmarketExportCards.size} Cardmarket export cards")
    }

    val recognitionVectors = loadRecognitionVectors(
        projectRoot = projectRoot,
        vectorsFilePath = config.recognitionVectorsFile,
        json = json,
    )
    if (recognitionVectors != null) {
        println(
            "[Tcgdex] Loaded recognition vectors: cards=${recognitionVectors.rowsByCardLanguage.size} " +
                "missing=${recognitionVectors.missingCardIds.size}",
        )
        require(recognitionVectors.missingCardIds.isEmpty()) {
            "[Tcgdex][x] Recognition vectors contain missingCardIds: ${recognitionVectors.missingCardIds.joinToString(", ")}"
        }
    }

    // Track missing images for Pokecardex fallback index
    val missingImages = mutableListOf<MissingImagesIndexGenerator.MissingImageEntry>()
    val importedRecognitionKeys = mutableSetOf<String>()
    // Track all cards per language for cross-language comparison
    val cardsByLanguage = mutableMapOf<String, MutableSet<String>>() // language -> set of card IDs
    val englishCardCache = mutableMapOf<String, JsonObject>()
    val frenchCardCache = mutableMapOf<String, JsonObject>()
    // language -> subSetId -> (serieId, parentSetId) for CDN parent-folder image fallback
    val subSetParentsByLanguage = mutableMapOf<String, Map<String, Pair<String, String>>>()
    
    // Valid dex IDs from pokemon-species.json (used for validation)
    val validDexIds = pokemonSpecies.keys

    suspend fun insertCard(
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
        // Fallback only: never overwrite an image provided by the TS scan. Known
        // sub-sets (e.g. swsh10tg) get a manifest-confirmed URL under the parent
        // set folder (e.g. https://assets.tcgdex.net/fr/swsh/swsh10/TG28).
        val imageUrl = card.getString("image")?.takeIf { it.isNotBlank() }
            ?: synthesizeSubSetImageUrl(
                assetsManifest = assetsManifest,
                subSetParents = subSetParentsByLanguage[language].orEmpty(),
                language = language,
                setId = setId,
                localId = localId,
            )
        val fallbackImage =
            if (imageUrl.isNullOrBlank()) {
                // When there is no TCGdex CDN image at all, apply Pokepedia fallback for
                // every language so non-French users don't see "Picture missing".
                pokepediaFallbacks[id]
            } else if (language.equals("fr", ignoreCase = true)) {
                // When TCGdex images exist, Pokepedia is only needed as a backup for French
                // (in case the French TCGdex image URL fails at runtime).
                pokepediaFallbacks[id]
            } else {
                null
            }
        val fallbackImageUrl = fallbackImage?.url
        val fallbackImageSource = fallbackImage?.source

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
            val langKey = originLanguage.lowercase()
            val englishName =
                if (langKey == "en") {
                    rarityName
                } else {
                    rarityReverseTranslations[langKey]?.get(rarityName) ?: rarityName
                }

            val slug = slugify(englishName)
            rarities[slug] = englishName
            slug
        } else {
            null
        }

        val dexIds = card.getIntArray("dexId")
        val cameoDexIds = card.getIntArray("cameoDexIds")

        val types = card.getStringArray("types")?.joinToString(",")
        val category = card.getString("category")
        val supertype = card.getString("supertype")
        val regulationMark = card.getString("regulationMark")
        val hp = card["hp"]?.let { element -> (element as? JsonPrimitive)?.intOrNull }
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

        // Pricing: store exact data only; fallback is handled at runtime.
        //
        // Flat columns in the cards table store the price-guide (GLOBAL) baseline.
        // The card_prices table stores:
        //   - poke-browser export rows with real seller countries (exact per-country data)
        //   - price-guide row with seller_country = 'GLOBAL' (language-agnostic baseline)
        val exportCard = cardmarketExportCards[id]
        val cardmarketId = resolveMarketplaceId(card, "cardmarket")
        val s3Pricing = cardmarketId?.let { cardmarketPrices[it] }

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
            hp = hp?.toLong(),
            priceCardmarketTrend = s3Pricing?.trendPrice,
            priceCardmarketAvg = s3Pricing?.averageSellPrice,
            priceCardmarketLow = s3Pricing?.lowPrice,
            priceUpdatedIso = s3Pricing?.updatedIso,
            priceUnit = s3Pricing?.unit,
        )

        val recognitionKey = "$id::$language"
        val recognitionRows = recognitionVectors?.rowsByCardLanguage?.get(recognitionKey).orEmpty()
        for (row in recognitionRows) {
            db.tcgdexQueries.insertCardRecognitionHash(
                cardId = id,
                language = language,
                imageSource = row.imageSource,
                imageUrl = row.imageUrl,
                lighting = row.lighting,
                rotation = row.rotation.toLong(),
                dhash = row.dhash,
                phash = row.phash,
            )
            importedRecognitionKeys.add(recognitionKey)
        }

        // Persist exact export prices, one row per (variant, price language, seller country, condition).
        // The generator never reduces condition buckets -- the app decides which condition to display.
        if (exportCard != null) {
            for (variant in exportCard.variants) {
                val variantKey = variant.version?.takeIf { it.isNotBlank() }
                    ?: variant.label?.takeIf { it.isNotBlank() }
                    ?: ""
                for ((priceLang, byCountry) in variant.prices) {
                    for ((sellerCountry, byCondition) in byCountry) {
                        for ((condition, price) in byCondition) {
                            db.tcgdexQueries.insertCardPrice(
                                cardId = id,
                                cardLanguage = language,
                                variant = variantKey,
                                priceLanguage = priceLang,
                                sellerCountry = sellerCountry,
                                condition = condition,
                                currency = price.currency ?: "EUR",
                                minPrice = price.minPrice,
                                avgPrice = price.avgPrice,
                                medianPrice = price.medianPrice,
                                maxPrice = price.maxPrice,
                                recommendedPrice = price.recommendedPrice,
                                availableCount = price.availableCount?.toLong(),
                                productId = variant.productId?.toLong(),
                                updatedIso = resolveExportPriceUpdatedIso(price.capturedAt, exportUpdatedIso),
                            )
                        }
                    }
                }
            }
        }

        // Insert price guide as GLOBAL baseline. This row is condition-agnostic (the
        // Cardmarket price guide aggregates across conditions); it uses condition = ''
        // as the sentinel to distinguish it from per-condition export rows.
        if (s3Pricing != null) {
            db.tcgdexQueries.insertCardPrice(
                cardId = id,
                cardLanguage = language,
                variant = "Normal",
                priceLanguage = "",
                sellerCountry = "GLOBAL",
                condition = "",
                currency = s3Pricing.unit,
                minPrice = s3Pricing.lowPrice,
                avgPrice = s3Pricing.averageSellPrice,
                medianPrice = null,
                maxPrice = null,
                recommendedPrice = s3Pricing.trendPrice,
                availableCount = null,
                productId = cardmarketId.toLong(),
                updatedIso = s3Pricing.updatedIso,
            )
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

        val normalizedCameoDexIds = cameoDexIds?.mapNotNull { dexId ->
            normalizeDexId(dexId, validDexIds)
        }

        if (validDexIds.isNotEmpty()) {
            val invalidCameoDexIds = cameoDexIds?.filter { dexId ->
                normalizeDexId(dexId, validDexIds) == null
            }.orEmpty()
            if (invalidCameoDexIds.isNotEmpty()) {
                throw IllegalArgumentException(
                    """
                    [Tcgdex][x] INVALID CAMEO DEX ID DETECTED - BUILD FAILED
                    Card: $id (set: $setId, language: $language)
                    Invalid cameoDexIds: ${invalidCameoDexIds.joinToString(", ")}
                    
                    This cameo dex ID does not exist in pokemon-species.json.
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
                isCameo = 0L,
            )
        }

        val mainDexIdSet = normalizedDexIds?.toSet().orEmpty()
        normalizedCameoDexIds
            ?.asSequence()
            ?.filterNot { it in mainDexIdSet }
            ?.distinct()
            ?.forEach { dexId ->
                db.tcgdexQueries.insertCardPokemon(
                    cardId = id,
                    language = language,
                    pokemonDexId = dexId.toLong(),
                    isCameo = 1L,
                )
            }
    }

    for (language in config.languages) {
        val dataset = datasetsByLanguage.getValue(language)

        println("[Tcgdex] Processing language: $language")

        // Load series
        val seriesJson = json.parseToJsonElement(dataset.seriesFile.readText()).jsonArray
        var position = 0
        for (serieElement in seriesJson) {
            val serie = serieElement.jsonObject
            val id = serie.getString("id") ?: continue
            val name = serie.getString("name") ?: id
            db.tcgdexQueries.insertSerie(id, language, name, position.toLong())
            position++
        }
        println("[Tcgdex]   Series: ${seriesJson.size}")

        // Load sets
        val setsJson = json.parseToJsonElement(dataset.setsFile.readText()).jsonArray
        val parsedSets = setsJson.map { rewriteHiddenFatesVaultSetJson(it.jsonObject) }
        val parentBySubSetId = deriveSubSetParents(parsedSets)
        val serieIdBySetId = parsedSets.mapNotNull { set ->
            val setId = set.getString("id") ?: return@mapNotNull null
            val serieId = set.getNestedString("serie", "id") ?: return@mapNotNull null
            setId to serieId
        }.toMap()
        val subSetParents = parentBySubSetId.mapValues { (subSetId, parentSetId) ->
            val serieId = serieIdBySetId[parentSetId]
                ?: error("[Tcgdex][x] Sub-set $subSetId parent $parentSetId has no serie id")
            serieId to parentSetId
        }
        subSetParentsByLanguage[language] = subSetParents
        if (parentBySubSetId.isNotEmpty()) {
            val summary = parentBySubSetId.entries.sortedBy { it.key }.joinToString(", ") { "${it.key}->${it.value}" }
            println("[Tcgdex]   Sub-sets: $summary")
        }

        for (set in parsedSets) {
            val id = set.getString("id") ?: continue
            val serieId = set.getNestedString("serie", "id") ?: continue
            val name = set.getString("name") ?: id
            val logoUrl = set.getString("logo")
            val symbolUrl = set.getString("symbol")
            val cardCountTotal = set.getNestedInt("cardCount", "total") ?: 0
            val cardCountOfficial = set.getNestedInt("cardCount", "official") ?: cardCountTotal
            val releaseDate = set.getString("releaseDate")
            val abbreviationOfficial = set.getNestedString("abbreviation", "official")

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
                abbreviationOfficial = abbreviationOfficial,
                parentSetId = parentBySubSetId[id],
            )
        }
        println("[Tcgdex]   Sets: ${setsJson.size}")

        // Load cards
        val cardsJson = json.parseToJsonElement(dataset.cardsFile.readText()).jsonArray
        for (cardElement in cardsJson) {
            val card = rewriteHiddenFatesVaultCardJson(cardElement.jsonObject)
            insertCard(language, card, originLanguage = language)
            if (language == "en") {
                val id = card.getString("id")
                if (id != null) {
                    englishCardCache[id] = card
                }
            } else if (language == "fr") {
                val id = card.getString("id")
                if (id != null) {
                    frenchCardCache[id] = card
                }
            }
        }
        println("[Tcgdex]   Cards: ${cardsJson.size}")
    }

    // Backfill missing French cards with English data when available
    val shouldBackfillFrenchFromEnglish = "fr" in datasetsByLanguage && "en" in datasetsByLanguage
    if (shouldBackfillFrenchFromEnglish) {
        val frenchIds = cardsByLanguage["fr"] ?: emptySet()
        val englishOnlyIds = englishCardCache.keys - frenchIds
        if (englishOnlyIds.isNotEmpty()) {
            println("[Tcgdex][!] Adding ${englishOnlyIds.size} English fallbacks to French dataset")
            englishOnlyIds.sorted().forEach { id ->
                val card = englishCardCache[id] ?: return@forEach
                insertCard(language = "fr", card = card, originLanguage = "en")
            }
        }

        val englishIds = cardsByLanguage["en"] ?: emptySet()
        val frenchOnlyIds = frenchCardCache.keys - englishIds
        if (frenchOnlyIds.isNotEmpty()) {
            println("[Tcgdex][!] Adding ${frenchOnlyIds.size} French fallbacks to English dataset")
            frenchOnlyIds.sorted().forEach { id ->
                val card = frenchCardCache[id] ?: return@forEach
                insertCard(language = "en", card = card, originLanguage = "fr")
            }
        }
    } else {
        println("[Tcgdex] Skipping French fallback because both en and fr were not requested")
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

    if (recognitionVectors != null) {
        val unresolvedRecognitionKeys = recognitionVectors.rowsByCardLanguage.keys - importedRecognitionKeys
        if (unresolvedRecognitionKeys.isNotEmpty()) {
            // The recognition manifest can lag behind dataset restructures (e.g. cel25
            // Classic Collection split removed cel25-*A ids). Stale rows are skipped
            // instead of failing the whole generation.
            println(
                "[Tcgdex][!] Recognition vectors reference ${unresolvedRecognitionKeys.size} unknown cards/languages (skipped): " +
                    unresolvedRecognitionKeys.sorted().take(10).joinToString(", ") +
                    if (unresolvedRecognitionKeys.size > 10) " ... (+${unresolvedRecognitionKeys.size - 10} more)" else "",
            )
        }
        println("[Tcgdex] Imported recognition vector rows for ${importedRecognitionKeys.size} cards")
    }

    // Rebuild FTS index from final cards snapshot.
    // We do this explicitly at the end to avoid stale entries when cards are replaced.
    driver.execute(null, "DELETE FROM cards_fts", 0).await()
    driver.execute(
        null,
        """
        INSERT INTO cards_fts(card_id, language, name)
        SELECT id, language, name
        FROM cards
        """.trimIndent(),
        0,
    ).await()
    println("[Tcgdex] Rebuilt cards_fts index")

    assertCameoRowsPersisted(config.languages) { language ->
        countCameoRows(driver, language)
    }

    // Set the logical database version for runtime installation guards.
    // This must stay in sync with TcgdexDatabaseInstaller.DATABASE_USER_VERSION.
    driver.execute(null, "PRAGMA user_version = ${TcgdexDatabaseInstaller.DATABASE_USER_VERSION}", 0).await()
    println("[Tcgdex] Set user_version = ${TcgdexDatabaseInstaller.DATABASE_USER_VERSION}")

    // Ensure database is in a portable state for iOS compatibility:
    // 1. Set journal_mode to DELETE (not WAL) for bundled databases
    // 2. Run integrity check to verify database is valid
    // 3. VACUUM to compact and ensure clean state
    driver.execute(null, "PRAGMA journal_mode = DELETE", 0).await()
    driver.execute(null, "VACUUM", 0).await()
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
    val recognitionVectorsFile: String?,
    val cardmarketExportFile: String?,
    val setAliasesConfigFile: String?,
    val cardmarketExpansionsFile: String?,
    val assetsManifestFile: String?,
)

private data class LanguageDatasetFiles(
    val language: String,
    val langDir: File,
    val seriesFile: File,
    val setsFile: File,
    val cardsFile: File,
)

private fun resolveLanguageDataset(datasetDir: String, language: String): LanguageDatasetFiles {
    val langDir = File(datasetDir, language)
    require(langDir.exists() && langDir.isDirectory) {
        "[Tcgdex][x] Missing generated language directory for '$language': ${langDir.absolutePath}\n" +
            "Run `cd libs/cards-database/server && bun run compile` before generating tcgdex.db."
    }

    val seriesFile = File(langDir, "series.json")
    val setsFile = File(langDir, "sets.json")
    val cardsFile = File(langDir, "cards.json")
    val missingOrEmptyFiles = listOf(seriesFile, setsFile, cardsFile)
        .filterNot { it.exists() && it.isFile && it.length() > 0L }

    require(missingOrEmptyFiles.isEmpty()) {
        buildString {
            append("[Tcgdex][x] Incomplete generated dataset for '$language'. Missing or empty files:")
            missingOrEmptyFiles.forEach { file ->
                append("\n - ${file.absolutePath}")
            }
            append("\nRun `cd libs/cards-database/server && bun run compile` before generating tcgdex.db.")
        }
    }

    return LanguageDatasetFiles(
        language = language,
        langDir = langDir,
        seriesFile = seriesFile,
        setsFile = setsFile,
        cardsFile = cardsFile,
    )
}

private fun parseArgs(args: Array<String>): Config {
    var datasetDir = ""
    var languages = listOf("en", "fr")
    var outputFile = "tcgdex.db"
    var force = false
    var pokepediaMissingFile: String? = null
    var recognitionVectorsFile: String? = null
    var cardmarketExportFile: String? = null
    var setAliasesConfigFile: String? = null
    var cardmarketExpansionsFile: String? = null
    var assetsManifestFile: String? = null

    for (arg in args) {
        when {
            arg.startsWith("--dataset=") -> datasetDir = arg.removePrefix("--dataset=")
            arg.startsWith("--languages=") -> languages = arg.removePrefix("--languages=").split(",").map { it.trim() }
            arg.startsWith("--output=") -> outputFile = arg.removePrefix("--output=")
            arg.startsWith("--force=") -> force = arg.removePrefix("--force=").toBoolean()
            arg.startsWith("--pokepedia-missing=") -> pokepediaMissingFile = arg.removePrefix("--pokepedia-missing=")
            arg.startsWith("--recognition-vectors=") -> recognitionVectorsFile = arg.removePrefix("--recognition-vectors=")
            arg.startsWith("--cardmarket-export=") -> cardmarketExportFile = arg.removePrefix("--cardmarket-export=")
            arg.startsWith("--set-aliases-config=") -> setAliasesConfigFile = arg.removePrefix("--set-aliases-config=")
            arg.startsWith("--cardmarket-expansions=") -> cardmarketExpansionsFile = arg.removePrefix("--cardmarket-expansions=")
            arg.startsWith("--assets-manifest=") -> assetsManifestFile = arg.removePrefix("--assets-manifest=")
        }
    }

    require(datasetDir.isNotBlank()) { "Missing required argument: --dataset=/path/to/generated" }
    return Config(
        datasetDir = datasetDir,
        languages = languages,
        outputFile = outputFile,
        force = force,
        pokepediaMissingFile = pokepediaMissingFile,
        recognitionVectorsFile = recognitionVectorsFile,
        cardmarketExportFile = cardmarketExportFile,
        setAliasesConfigFile = setAliasesConfigFile,
        cardmarketExpansionsFile = cardmarketExpansionsFile,
        assetsManifestFile = assetsManifestFile,
    )
}

private fun resolveProjectRoot(datasetDir: String): File {
    var current = File(datasetDir).absoluteFile
    while (true) {
        val hasComposeApp = File(current, "composeApp").exists()
        val hasTools = File(current, "tools").exists()
        if (hasComposeApp && hasTools) return current
        val parent = current.parentFile ?: break
        if (parent == current) break
        current = parent
    }
    error("Unable to resolve project root from dataset path: $datasetDir")
}

private fun resolveSetAliasesConfigFile(projectRoot: File, explicitPath: String?): File {
    val file =
        if (explicitPath.isNullOrBlank()) {
            File(projectRoot, "tools/set-aliases-config.json")
        } else {
            File(explicitPath)
        }
    require(file.exists()) {
        "Set aliases config file not found: ${file.absolutePath}"
    }
    return file
}

private fun loadCardmarketExpansionAbbreviations(
    projectRoot: File,
    explicitSource: String?,
    json: Json,
): Map<Int, String> {
    val defaultSources =
        listOf(
            "libs/cards-database/var/models/cardmarket/expansions_6.json",
            "libs/cards-database/var/models/cardmarket/expansions.json",
            "https://apiv2.cardmarket.com/ws/v2.0/games/6/expansions",
        )
    val sources = buildList {
        explicitSource?.takeIf { it.isNotBlank() }?.let { add(it.trim()) }
        addAll(defaultSources)
    }.distinct()

    for (source in sources) {
        val raw = readTextSource(projectRoot, source) ?: continue
        val parsed = parseCardmarketExpansionAbbreviations(raw, json)
        if (parsed.isNotEmpty()) {
            println("[Tcgdex] Cardmarket expansions loaded from: $source")
            return parsed
        }
    }

    return emptyMap()
}

private fun readTextSource(projectRoot: File, source: String): String? {
    return runCatching {
        val trimmed = source.trim()
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            val connection = URI(trimmed).toURL().openConnection()
            connection.connectTimeout = 15_000
            connection.readTimeout = 30_000
            connection.getInputStream().bufferedReader().readText()
        } else {
            val explicitFile = File(trimmed)
            val file =
                when {
                    explicitFile.isAbsolute -> explicitFile
                    explicitFile.exists() -> explicitFile
                    else -> File(projectRoot, trimmed)
                }
            if (!file.exists()) return null
            file.readText()
        }
    }.getOrNull()
}

private fun parseCardmarketExpansionAbbreviations(
    raw: String,
    json: Json,
): Map<Int, String> {
    val parsed = mutableMapOf<Int, String>()
    val root = runCatching { json.parseToJsonElement(raw) }.getOrNull() ?: return emptyMap()

    fun walk(element: JsonElement) {
        when (element) {
            is JsonObject -> {
                val expansionId = element["idExpansion"]?.jsonPrimitive?.intOrNull
                val abbreviation =
                    element["abbreviation"]
                        ?.jsonPrimitive
                        ?.contentOrNull
                        ?.trim()
                        ?.takeIf { it.isNotBlank() }
                if (expansionId != null && abbreviation != null) {
                    parsed[expansionId] = abbreviation
                }
                element.values.forEach(::walk)
            }

            is JsonArray -> element.forEach(::walk)
            else -> Unit
        }
    }

    walk(root)
    return parsed
}

private fun generateSetAliasIndex(
    projectRoot: File,
    configFile: File,
    cardmarketExpansionsFile: String? = null,
) {
    val json = Json { ignoreUnknownKeys = true }
    val aliasConfig = json.decodeFromString<SetAliasConfigFile>(configFile.readText())
    val cardmarketAbbreviationsByExpansionId =
        loadCardmarketExpansionAbbreviations(
            projectRoot = projectRoot,
            explicitSource = cardmarketExpansionsFile,
            json = json,
        )
    if (cardmarketAbbreviationsByExpansionId.isNotEmpty()) {
        println("[Tcgdex] Loaded ${cardmarketAbbreviationsByExpansionId.size} Cardmarket expansion abbreviations")
    } else {
        println("[Tcgdex][i] No Cardmarket expansion abbreviations loaded (continuing)")
    }
    val seeds = collectGeneratedSetAliasSeeds(projectRoot, aliasConfig, cardmarketAbbreviationsByExpansionId)
    val outputFile = File(projectRoot, "composeApp/src/commonMain/kotlin/app/cardium/search/generated/SetAliasIndex.kt")
    outputFile.parentFile.mkdirs()

    val source = buildSetAliasIndexSource(aliasConfig = aliasConfig, seeds = seeds)
    writeFileIfChanged(outputFile, source)
    println("[Tcgdex] SetAliasIndex generated for ${seeds.size} sets")
}

private fun collectGeneratedSetAliasSeeds(
    projectRoot: File,
    aliasConfig: SetAliasConfigFile,
    cardmarketAbbreviationsByExpansionId: Map<Int, String>,
): List<GeneratedSetAliasSeed> {
    val dataRoot = File(projectRoot, "libs/cards-database/data")
    require(dataRoot.exists()) {
        "cards-database data directory not found: ${dataRoot.absolutePath}"
    }

    val setFiles =
        dataRoot
            .walkTopDown()
            .filter { file ->
                file.isFile &&
                    file.extension == "ts" &&
                    file.relativeTo(dataRoot).invariantSeparatorsPath.contains("/")
            }.toList()
            .sortedBy { it.relativeTo(dataRoot).invariantSeparatorsPath }

    val overridesBySetId =
        aliasConfig.setOverrides.mapKeys { (setId, _) ->
            normalizeAliasForLookup(setId)
        }

    val mergedBySetId = linkedMapOf<String, GeneratedSetAliasSeed>()
    for (file in setFiles) {
        val content = file.readText()
        val parsed = parseSetAliasSourceFile(content) ?: continue

        val originalSetId = parsed.setId.trim()
        if (originalSetId.isBlank()) continue
        // TCG Pocket set IDs are uppercase (A1, A2, B1, P-A). They are forbidden in Cardium.
        if (originalSetId.any { it.isUpperCase() }) continue

        val setId = rewriteHiddenFatesVaultSetId(originalSetId)
        val officialAbbreviation = rewriteHiddenFatesVaultOfficial(originalSetId, parsed.officialAbbreviation)

        if (officialAbbreviation?.contains(':') == true && parsed.cardmarketExpansionId != null) {
            throw IllegalStateException(
                "[Tcgdex][x] Sub-set $setId ($officialAbbreviation) must not declare " +
                    "its own Cardmarket expansion id; Cardmarket only knows the parent set",
            )
        }

        val seriesKey =
            Regex("^[a-z]+")
                .find(setId.lowercase())
                ?.value
                ?: ""
        val seriesRule = aliasConfig.seriesPrefixAliases[seriesKey]
        val enSeriesAliases = buildSeriesAliasesForSetId(setId = setId, prefixes = seriesRule?.en.orEmpty(), seriesRule?.allowTrailingFiveDecimal == true)
        val frSeriesAliases = buildSeriesAliasesForSetId(setId = setId, prefixes = seriesRule?.fr.orEmpty(), seriesRule?.allowTrailingFiveDecimal == true)

        val override = overridesBySetId[normalizeAliasForLookup(setId)]
            ?: overridesBySetId[normalizeAliasForLookup(originalSetId)]
        val cardmarketAliases =
            buildList {
                parsed.cardmarketExpansionId?.let { add(it.toString()) }
                parsed.cardmarketExpansionId
                    ?.let { cardmarketAbbreviationsByExpansionId[it] }
                    ?.takeIf { it.isNotBlank() }
                    ?.let { add(it) }
            }
        val vaultAliases =
            if (setId == HIDDEN_FATES_VAULT_SET_ID) {
                listOf(HIDDEN_FATES_VAULT_SOURCE_SET_ID, "$HIDDEN_FATES_PARENT_SET_ID:sv")
            } else {
                emptyList()
            }
        val enExtraAliases = (override?.extraAliases?.get("en").orEmpty() + cardmarketAliases + vaultAliases).distinct()
        val frExtraAliases = override?.extraAliases?.get("fr").orEmpty().distinct()

        val candidate =
            GeneratedSetAliasSeed(
                setId = setId,
                releaseDate = parsed.releaseDate,
                enName = parsed.enName,
                frName = parsed.frName,
                officialAbbreviation = officialAbbreviation,
                frenchAbbreviation = parsed.frenchAbbreviation,
                tcgOnline = parsed.tcgOnline,
                enExtraAliases = enExtraAliases,
                frExtraAliases = frExtraAliases,
                enSeriesAliases = enSeriesAliases,
                frSeriesAliases = frSeriesAliases,
            )

        val existing = mergedBySetId[setId]
        mergedBySetId[setId] = if (existing == null) candidate else mergeGeneratedSetAliasSeed(existing, candidate)
    }

    inheritParentCardmarketAliases(mergedBySetId)

    return mergedBySetId
        .values
        .sortedBy { it.setId }
}

private fun inheritParentCardmarketAliases(
    seedsBySetId: MutableMap<String, GeneratedSetAliasSeed>,
) {
    val officialToSetIds = linkedMapOf<String, MutableList<String>>()
    for (seed in seedsBySetId.values) {
        val official = seed.officialAbbreviation?.takeIf { it.isNotBlank() } ?: continue
        officialToSetIds.getOrPut(official) { mutableListOf() }.add(seed.setId)
    }

    for (seed in seedsBySetId.values.toList()) {
        val official = seed.officialAbbreviation ?: continue
        if (':' !in official) continue
        val parentOfficial = official.substringBefore(':')
        val candidates = officialToSetIds[parentOfficial].orEmpty().distinct()
        if (candidates.size != 1) {
            error(
                "[Tcgdex][x] Cannot copy Cardmarket aliases for sub-set ${seed.setId} " +
                    "(official=$official, parentOfficial=$parentOfficial). " +
                    "Candidates: ${candidates.joinToString(",").ifBlank { "<none>" }}",
            )
        }
        val parent = seedsBySetId.getValue(candidates.single())
        seedsBySetId[seed.setId] = seed.copy(
            enExtraAliases = (seed.enExtraAliases + parent.enExtraAliases).distinct(),
        )
    }
}

private fun mergeGeneratedSetAliasSeed(
    existing: GeneratedSetAliasSeed,
    candidate: GeneratedSetAliasSeed,
): GeneratedSetAliasSeed {
    fun pick(existingValue: String?, candidateValue: String?): String? =
        if (!existingValue.isNullOrBlank()) existingValue else candidateValue

    return GeneratedSetAliasSeed(
        setId = existing.setId,
        releaseDate = pick(existing.releaseDate, candidate.releaseDate),
        enName = pick(existing.enName, candidate.enName),
        frName = pick(existing.frName, candidate.frName),
        officialAbbreviation = pick(existing.officialAbbreviation, candidate.officialAbbreviation),
        frenchAbbreviation = pick(existing.frenchAbbreviation, candidate.frenchAbbreviation),
        tcgOnline = pick(existing.tcgOnline, candidate.tcgOnline),
        enExtraAliases = (existing.enExtraAliases + candidate.enExtraAliases).distinct(),
        frExtraAliases = (existing.frExtraAliases + candidate.frExtraAliases).distinct(),
        enSeriesAliases = (existing.enSeriesAliases + candidate.enSeriesAliases).distinct(),
        frSeriesAliases = (existing.frSeriesAliases + candidate.frSeriesAliases).distinct(),
    )
}

private data class ParsedSeriesNumber(
    val majorRaw: String,
    val majorNoPad: String,
    val minor: String?,
    val suffixLetter: String?,
)

private fun buildSeriesAliasesForSetId(
    setId: String,
    prefixes: List<String>,
    allowTrailingFiveDecimal: Boolean,
): List<String> {
    if (prefixes.isEmpty()) return emptyList()

    val parsed = parseSeriesNumberFromSetId(setId, allowTrailingFiveDecimal) ?: return emptyList()
    val suffixLetter = parsed.suffixLetter.orEmpty()
    val aliases = linkedSetOf<String>()
    for (prefix in prefixes) {
        val cleanPrefix = prefix.trim()
        if (cleanPrefix.isEmpty()) continue
        if (parsed.minor == null) {
            val raw = "${parsed.majorRaw}$suffixLetter"
            val noPad = "${parsed.majorNoPad}$suffixLetter"
            aliases += "$cleanPrefix$raw"
            aliases += "$cleanPrefix$noPad"
            aliases += "$cleanPrefix $raw"
            aliases += "$cleanPrefix $noPad"
        } else {
            val dotRaw = "${parsed.majorRaw}.${parsed.minor}$suffixLetter"
            val dotNoPad = "${parsed.majorNoPad}.${parsed.minor}$suffixLetter"
            val commaRaw = "${parsed.majorRaw},${parsed.minor}$suffixLetter"
            val commaNoPad = "${parsed.majorNoPad},${parsed.minor}$suffixLetter"
            val compactRaw = "${parsed.majorRaw}${parsed.minor}$suffixLetter"
            val compactNoPad = "${parsed.majorNoPad}${parsed.minor}$suffixLetter"

            aliases += "$cleanPrefix$dotRaw"
            aliases += "$cleanPrefix$dotNoPad"
            aliases += "$cleanPrefix$commaRaw"
            aliases += "$cleanPrefix$commaNoPad"
            aliases += "$cleanPrefix$compactRaw"
            aliases += "$cleanPrefix$compactNoPad"
            aliases += "$cleanPrefix $dotRaw"
            aliases += "$cleanPrefix $dotNoPad"
        }
    }
    return aliases.toList()
}

private fun parseSeriesNumberFromSetId(
    setId: String,
    allowTrailingFiveDecimal: Boolean,
): ParsedSeriesNumber? {
    val normalized = setId.lowercase()
    val prefixMatch = Regex("^[a-z]+").find(normalized) ?: return null
    val suffixRaw = normalized.removePrefix(prefixMatch.value)
    if (suffixRaw.isBlank()) return null
    if (!suffixRaw.first().isDigit()) return null

    val suffixWithDot = suffixRaw.replace("pt", ".")
    val standardMatch = Regex("^(\\d+)(?:[._](\\d+))?([a-z])?$").matchEntire(suffixWithDot)
    if (standardMatch != null) {
        val majorDigits = standardMatch.groupValues[1]
        val explicitMinor = standardMatch.groupValues[2].ifBlank { null }
        val suffixLetter = standardMatch.groupValues[3].ifBlank { null }

        // Compact ".5" encoding (e.g., "sm115" -> 11.5) should only apply to
        // 3+ digit suffixes to avoid misclassifying regular padded set numbers
        // like "sv05" as "sv0.5".
        if (explicitMinor == null && allowTrailingFiveDecimal && majorDigits.length >= 3 && majorDigits.endsWith("5")) {
            val majorRaw = majorDigits.dropLast(1)
            if (majorRaw.isNotBlank()) {
                val majorNoPad = majorRaw.toIntOrNull()?.toString() ?: majorRaw.trimStart('0').ifBlank { "0" }
                return ParsedSeriesNumber(
                    majorRaw = majorRaw,
                    majorNoPad = majorNoPad,
                    minor = "5",
                    suffixLetter = suffixLetter,
                )
            }
        }

        val majorRaw = majorDigits
        val majorNoPad = majorRaw.toIntOrNull()?.toString() ?: majorRaw.trimStart('0').ifBlank { "0" }
        return ParsedSeriesNumber(
            majorRaw = majorRaw,
            majorNoPad = majorNoPad,
            minor = explicitMinor,
            suffixLetter = suffixLetter,
        )
    }

    return null
}

private fun parseSetAliasSourceFile(content: String): ParsedSetAliasSource? {
    val setId = extractStringProperty(content, "id") ?: return null
    val releaseDate = extractStringProperty(content, "releaseDate")
    val tcgOnline = extractStringProperty(content, "tcgOnline")

    val nameBlock = extractObjectBlock(content, "name")
    val enName = nameBlock?.let { extractStringProperty(it, "en") }
    val frName = nameBlock?.let { extractStringProperty(it, "fr") }

    val abbreviationsBlock = extractObjectBlock(content, "abbreviations")
    val officialAbbreviation = abbreviationsBlock?.let { extractStringProperty(it, "official") }
    val frenchAbbreviation = abbreviationsBlock?.let { extractStringProperty(it, "fr") }
    val thirdPartyBlock = extractObjectBlock(content, "thirdParty")
    val cardmarketExpansionId = thirdPartyBlock?.let { extractIntProperty(it, "cardmarket") }

    return ParsedSetAliasSource(
        setId = setId,
        releaseDate = releaseDate,
        enName = enName,
        frName = frName,
        officialAbbreviation = officialAbbreviation,
        frenchAbbreviation = frenchAbbreviation,
        tcgOnline = tcgOnline,
        cardmarketExpansionId = cardmarketExpansionId,
    )
}

private fun extractObjectBlock(
    source: String,
    propertyName: String,
): String? {
    val propertyRegex = Regex("\\b${Regex.escape(propertyName)}\\s*:\\s*\\{")
    val match = propertyRegex.find(source) ?: return null
    val openBraceIndex = source.indexOf('{', match.range.first)
    if (openBraceIndex < 0) return null

    var depth = 0
    var index = openBraceIndex
    var inSingleQuotes = false
    var inDoubleQuotes = false
    var escaped = false

    while (index < source.length) {
        val char = source[index]

        if (escaped) {
            escaped = false
            index++
            continue
        }

        if (char == '\\') {
            escaped = true
            index++
            continue
        }

        if (!inDoubleQuotes && char == '\'') {
            inSingleQuotes = !inSingleQuotes
            index++
            continue
        }

        if (!inSingleQuotes && char == '"') {
            inDoubleQuotes = !inDoubleQuotes
            index++
            continue
        }

        if (!inSingleQuotes && !inDoubleQuotes) {
            if (char == '{') depth++
            if (char == '}') {
                depth--
                if (depth == 0) {
                    return source.substring(openBraceIndex + 1, index)
                }
            }
        }

        index++
    }

    return null
}

private fun extractStringProperty(
    source: String,
    key: String,
): String? {
    val propertyRegex = Regex("\\b${Regex.escape(key)}\\s*:\\s*(\"(?:[^\"\\\\]|\\\\.)*\"|'(?:[^'\\\\]|\\\\.)*')")
    val match = propertyRegex.find(source) ?: return null
    val quotedLiteral = match.groupValues[1]
    if (quotedLiteral.length < 2) return null
    val rawValue = quotedLiteral.substring(1, quotedLiteral.length - 1)
    return decodeTsStringLiteral(rawValue).trim().ifBlank { null }
}

private fun extractIntProperty(
    source: String,
    key: String,
): Int? {
    val propertyRegex = Regex("\\b${Regex.escape(key)}\\s*:\\s*(-?\\d+)")
    val match = propertyRegex.find(source) ?: return null
    return match.groupValues[1].toIntOrNull()
}

private fun decodeTsStringLiteral(raw: String): String {
    return raw
        .replace("\\\"", "\"")
        .replace("\\'", "'")
        .replace("\\\\", "\\")
}

private fun normalizeAliasForLookup(value: String?): String {
    if (value.isNullOrBlank()) return ""
    val normalized = Normalizer.normalize(value.lowercase(), Normalizer.Form.NFD)
    return normalized.filter { ch -> Character.getType(ch) != Character.NON_SPACING_MARK.toInt() }
}

private fun buildSetAliasIndexSource(
    aliasConfig: SetAliasConfigFile,
    seeds: List<GeneratedSetAliasSeed>,
): String {
    val minTokenLength = aliasConfig.tokenization.minTokenLength.coerceAtLeast(1)

    val stopwordsByLanguage =
        aliasConfig.tokenization.stopwords
            .mapValues { (_, words) ->
                words
                    .map(::normalizeAliasForLookup)
                    .filter { it.isNotBlank() }
                    .distinct()
                    .sorted()
            }.toSortedMap()

    val stopwordsLiteral =
        if (stopwordsByLanguage.isEmpty()) {
            "emptyMap()"
        } else {
            stopwordsByLanguage.entries.joinToString(
                prefix = "mapOf(\n",
                postfix = "\n    )",
                separator = ",\n",
            ) { (language, words) ->
                "        ${toKotlinStringLiteral(language)} to ${toKotlinSetLiteral(words)}"
            }
        }

    val seedLiteral =
        if (seeds.isEmpty()) {
            "emptyList()"
        } else {
            seeds.joinToString(
                prefix = "listOf(\n",
                postfix = "\n    )",
                separator = ",\n",
            ) { seed ->
                buildString {
                    append("        SetAliasSeed(")
                    append("setId = ${toKotlinStringLiteral(seed.setId)}, ")
                    append("releaseDate = ${toKotlinNullableStringLiteral(seed.releaseDate)}, ")
                    append("enName = ${toKotlinNullableStringLiteral(seed.enName)}, ")
                    append("frName = ${toKotlinNullableStringLiteral(seed.frName)}, ")
                    append("officialAbbreviation = ${toKotlinNullableStringLiteral(seed.officialAbbreviation)}, ")
                    append("frenchAbbreviation = ${toKotlinNullableStringLiteral(seed.frenchAbbreviation)}, ")
                    append("tcgOnline = ${toKotlinNullableStringLiteral(seed.tcgOnline)}, ")
                    append("enExtraAliases = ${toKotlinListLiteral(seed.enExtraAliases)}, ")
                    append("frExtraAliases = ${toKotlinListLiteral(seed.frExtraAliases)}, ")
                    append("enSeriesAliases = ${toKotlinListLiteral(seed.enSeriesAliases)}, ")
                    append("frSeriesAliases = ${toKotlinListLiteral(seed.frSeriesAliases)}")
                    append(")")
                }
            }
        }

    return """
        |package app.cardium.search.generated
        |
        |import app.cardium.utils.normalizeCode
        |
        |/**
        | * Generated by GenerateTcgdexDatabase.
        | * Do not edit manually.
        | */
        |data class SetAliasHit(
        |    val setId: String,
        |    val language: String,
        |    val releaseDate: String?,
        |)
        |
        |object SetAliasIndex {
        |    private data class SetAliasSeed(
        |        val setId: String,
        |        val releaseDate: String?,
        |        val enName: String?,
        |        val frName: String?,
        |        val officialAbbreviation: String?,
        |        val frenchAbbreviation: String?,
        |        val tcgOnline: String?,
        |        val enExtraAliases: List<String>,
        |        val frExtraAliases: List<String>,
        |        val enSeriesAliases: List<String>,
        |        val frSeriesAliases: List<String>,
        |    )
        |
        |    private const val MIN_TOKEN_LENGTH: Int = $minTokenLength
        |
        |    private val STOPWORDS_BY_LANGUAGE: Map<String, Set<String>> =
        |    $stopwordsLiteral
        |
        |    private val SEEDS: List<SetAliasSeed> =
        |    $seedLiteral
        |
        |    private val SET_ID_TO_ABBREVIATION: Map<String, String> by lazy {
        |        SEEDS
        |            .mapNotNull { seed ->
        |                seed.officialAbbreviation?.let { seed.setId to it }
        |            }.toMap()
        |    }
        |
        |    private val ALIAS_TO_HITS: Map<String, List<SetAliasHit>> by lazy {
        |        val byAlias = mutableMapOf<String, MutableMap<String, SetAliasHit>>()
        |        for (seed in SEEDS) {
        |            val aliases = mutableSetOf<Pair<String, String>>()
        |            addAlias(aliases, seed.setId, "en")
        |            addOfficialAbbreviationAliases(aliases, seed.officialAbbreviation, "en")
        |            addAlias(aliases, seed.frenchAbbreviation, "fr")
        |            addAlias(aliases, seed.tcgOnline, "en")
        |
        |            addNameAliases(aliases, seed.enName, "en")
        |            addNameAliases(aliases, seed.frName, "fr")
        |
        |            seed.enExtraAliases.forEach { addAlias(aliases, it, "en") }
        |            seed.frExtraAliases.forEach { addAlias(aliases, it, "fr") }
        |            seed.enSeriesAliases.forEach { addAlias(aliases, it, "en") }
        |            seed.frSeriesAliases.forEach { addAlias(aliases, it, "fr") }
        |
        |            for ((alias, language) in aliases) {
        |                val normalizedAlias = normalizeCode(alias)
        |                if (normalizedAlias.isBlank()) continue
        |                val bySetId = byAlias.getOrPut(normalizedAlias) { mutableMapOf() }
        |                val previous = bySetId[seed.setId]
        |                if (previous == null || (seed.releaseDate ?: "") > (previous.releaseDate ?: "")) {
        |                    bySetId[seed.setId] = SetAliasHit(setId = seed.setId, language = language, releaseDate = seed.releaseDate)
        |                }
        |            }
        |        }
        |
        |        byAlias.mapValues { (_, valuesBySetId) ->
        |            valuesBySetId
        |                .values
        |                .sortedWith(
        |                    compareByDescending<SetAliasHit> { it.releaseDate ?: "" }
        |                        .thenBy { it.setId },
        |                )
        |        }
        |    }
        |
        |    fun resolve(alias: String, language: String?): List<SetAliasHit> {
        |        val normalizedAlias = normalizeCode(alias)
        |        if (normalizedAlias.isBlank()) return emptyList()
        |        return ALIAS_TO_HITS[normalizedAlias] ?: emptyList()
        |    }
        |
        |    fun getOfficialAbbreviation(setId: String): String? = SET_ID_TO_ABBREVIATION[setId]
        |
        |    private fun addAlias(
        |        target: MutableSet<Pair<String, String>>,
        |        value: String?,
        |        language: String,
        |    ) {
        |        if (value.isNullOrBlank()) return
        |        target += value to language
        |    }
        |
        |    private fun addOfficialAbbreviationAliases(
        |        target: MutableSet<Pair<String, String>>,
        |        value: String?,
        |        language: String,
        |    ) {
        |        if (value.isNullOrBlank()) return
        |        addAlias(target, value, language)
        |        if (':' in value) {
        |            addAlias(target, value.replace(":", " "), language)
        |            addAlias(target, value.replace(":", ""), language)
        |        }
        |    }
        |
        |    private fun addNameAliases(
        |        target: MutableSet<Pair<String, String>>,
        |        value: String?,
        |        language: String,
        |    ) {
        |        if (value.isNullOrBlank()) return
        |        addAlias(target, value, language)
        |
        |        val normalized = normalizeCode(value)
        |        if (normalized.isBlank()) return
        |        val stopwords = STOPWORDS_BY_LANGUAGE[language].orEmpty()
        |        val tokens =
        |            normalized
        |                .split(Regex("[^a-z0-9]+"))
        |                .filter { token ->
        |                    token.isNotBlank() &&
        |                        token.length >= MIN_TOKEN_LENGTH &&
        |                        token !in stopwords
        |                }
        |        tokens.forEach { addAlias(target, it, language) }
        |    }
        |}
        |
    """.trimMargin()
}

private fun toKotlinStringLiteral(value: String): String = "\"${escapeKotlinString(value)}\""

private fun toKotlinNullableStringLiteral(value: String?): String = value?.let(::toKotlinStringLiteral) ?: "null"

private fun toKotlinListLiteral(values: List<String>): String {
    if (values.isEmpty()) return "emptyList()"
    return values
        .distinct()
        .sorted()
        .joinToString(prefix = "listOf(", postfix = ")") { toKotlinStringLiteral(it) }
}

private fun toKotlinSetLiteral(values: List<String>): String {
    if (values.isEmpty()) return "emptySet()"
    return values
        .distinct()
        .sorted()
        .joinToString(prefix = "setOf(", postfix = ")") { toKotlinStringLiteral(it) }
}

private fun escapeKotlinString(value: String): String {
    val out = StringBuilder(value.length + 16)
    for (char in value) {
        when (char) {
            '\\' -> out.append("\\\\")
            '"' -> out.append("\\\"")
            '\n' -> out.append("\\n")
            '\r' -> out.append("\\r")
            '\t' -> out.append("\\t")
            else -> out.append(char)
        }
    }
    return out.toString()
}

private fun writeFileIfChanged(
    outputFile: File,
    content: String,
) {
    val current = outputFile.takeIf(File::exists)?.readText()
    if (current == content) return
    outputFile.writeText(content)
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

/**
 * Resolves a marketplace product ID from a compiled card JSON object.
 *
 * During the progressive migration of `cards-database`, Cardmarket/TCGPlayer IDs
 * may live at the card root (`thirdParty.<marketplace>`) or under per-variant
 * objects (`variants_detailed[].thirdParty.<marketplace>`).
 *
 * Precedence (deterministic, first non-null wins):
 *   1. Normal variant in `variants_detailed` with `thirdParty.<marketplace>`
 *   2. Legacy root `thirdParty.<marketplace>`
 *   3. First non-normal variant in `variants_detailed` that defines the ID
 */
internal fun resolveMarketplaceId(card: JsonObject, marketplace: String): Int? {
    fun variantMarketplaceId(variant: JsonObject): Int? =
        (variant["thirdParty"] as? JsonObject)
            ?.get(marketplace)
            ?.takeIf { it !is JsonNull }
            ?.jsonPrimitive?.intOrNull

    val detailedVariants = (card["variants_detailed"] as? JsonArray)
        ?.mapNotNull { it as? JsonObject }

    val normalVariantId = detailedVariants
        ?.firstOrNull { it["type"]?.jsonPrimitive?.contentOrNull?.equals("normal", ignoreCase = true) == true }
        ?.let { variantMarketplaceId(it) }
    if (normalVariantId != null) return normalVariantId

    val rootId = card.getNestedInt("thirdParty", marketplace)
    if (rootId != null) return rootId

    return detailedVariants
        ?.firstNotNullOfOrNull { variantMarketplaceId(it) }
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
    // Normalize diacritics to ASCII letters (e.g., "é" -> "e") before slugging.
    // This prevents broken slugs like "m-ga" for "méga".
    return Normalizer.normalize(text, Normalizer.Form.NFD)
        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
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
    // Keep "unnumbered" cards at the end of a set to avoid collisions with real
    // numeric refs like "001" (e.g., "UNNUMBERED-001" should not sort as 1).
    if (localId.contains("unnumbered", ignoreCase = true)) {
        return 999_999
    }

    // Extract numeric part for sorting (e.g., "001" -> 1, "TG01" -> 1, "SWSH001" -> 1)
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

internal fun resolvePokepediaMissingTree(datasetDir: String): File? {
    val projectRoot = runCatching { resolveProjectRoot(datasetDir) }.getOrNull() ?: return null
    val canonical = projectRoot
        .resolve("libs/tcgdex-kmp-sdk/generator-inputs/pokepedia/missing-fr-card-images-tree.json")
    if (canonical.exists()) {
        return canonical
    }

    val legacy = projectRoot
        .resolve("libs/tcgdex-kmp-sdk/resources/pokepedia/missing-fr-card-images-tree.json")
    if (legacy.exists()) {
        println("[Tcgdex][!] DEPRECATED: Using legacy Pokepedia tree path: ${legacy.absolutePath}")
        println("[Tcgdex][i] Move it to: ${canonical.absolutePath}")
        return legacy
    }

    return null
}

private fun loadRarityReverseTranslations(
    datasetDir: String,
    languages: List<String>,
    json: Json,
): Map<String, Map<String, String>> {
    // cards-database translation files live at:
    // libs/cards-database/meta/translations/{lang}.json
    //
    // The dataset dir passed to the generator is:
    // libs/cards-database/server/generated
    //
    // So relative path is: ../../meta/translations
    val translationsDir = File(datasetDir, "../../meta/translations").canonicalFile

    val result = mutableMapOf<String, Map<String, String>>()
    for (language in languages) {
        val lang = language.lowercase()
        if (lang == "en") continue

        val file = File(translationsDir, "$lang.json")
        if (!file.exists()) continue

        val obj = json.parseToJsonElement(file.readText()).jsonObject
        val rarityObj = obj["rarity"]?.jsonObject ?: continue

        // Invert map: localized display name -> English canonical name
        val reversed = mutableMapOf<String, String>()
        for ((englishName, localizedElement) in rarityObj) {
            val localizedName = localizedElement.jsonPrimitive.contentOrNull ?: continue
            reversed[localizedName] = englishName
        }
        result[lang] = reversed
    }
    return result
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

@Serializable
private data class RecognitionHashPayload(
    val dhash: String,
    val phash: String,
    val lighting: String = "original",
    val rotation: Int = 0,
)

@Serializable
private data class RecognitionCardPayload(
    val cardId: String,
    val language: String,
    val imageSource: String,
    val imageUrl: String,
    val hashes: List<RecognitionHashPayload> = emptyList(),
)

@Serializable
private data class RecognitionVectorsPayload(
    val generatedAt: String? = null,
    val language: String? = null,
    val missingCardIds: List<String> = emptyList(),
    val cards: List<RecognitionCardPayload> = emptyList(),
)

private data class RecognitionHashRow(
    val imageSource: String,
    val imageUrl: String,
    val lighting: String,
    val rotation: Int,
    val dhash: String,
    val phash: String,
)

private data class RecognitionVectorsData(
    val rowsByCardLanguage: Map<String, List<RecognitionHashRow>>,
    val missingCardIds: List<String>,
)

internal fun resolveDefaultRecognitionVectorsPath(projectRoot: File): File? {
    val canonical = projectRoot.resolve("libs/tcgdex-kmp-sdk/generator-inputs/recognition/card-vectors-fr.json")
    return canonical.takeIf { it.exists() }
}

private fun loadRecognitionVectors(
    projectRoot: File,
    vectorsFilePath: String?,
    json: Json,
): RecognitionVectorsData? {
    val source =
        if (!vectorsFilePath.isNullOrBlank()) {
            File(vectorsFilePath).also { explicit ->
                if (!explicit.exists()) {
                    println("[Tcgdex][!] Recognition vectors path not found: ${explicit.absolutePath}")
                }
            }.takeIf { it.exists() }
        } else {
            resolveDefaultRecognitionVectorsPath(projectRoot)
        }

    if (source == null) {
        println("[Tcgdex][i] No recognition vectors input found, skipping recognition import")
        return null
    }

    println("[Tcgdex] Recognition vectors source: ${source.absolutePath}")

    return runCatching {
        val payload = json.decodeFromString<RecognitionVectorsPayload>(source.readText())
        val grouped = mutableMapOf<String, MutableList<RecognitionHashRow>>()
        for (entry in payload.cards) {
            if (entry.cardId.isBlank() || entry.language.isBlank()) continue
            val cardId = rewriteHiddenFatesVaultCardId(entry.cardId)
            val rows = grouped.getOrPut("$cardId::${entry.language}") { mutableListOf() }
            for (hash in entry.hashes) {
                rows.add(
                    RecognitionHashRow(
                        imageSource = entry.imageSource,
                        imageUrl = entry.imageUrl,
                        lighting = hash.lighting,
                        rotation = hash.rotation,
                        dhash = hash.dhash.uppercase(),
                        phash = hash.phash.uppercase(),
                    ),
                )
            }
        }
        RecognitionVectorsData(
            rowsByCardLanguage = grouped.mapValues { (_, rows) -> rows.toList() },
            missingCardIds = payload.missingCardIds.map(::rewriteHiddenFatesVaultCardId).sorted(),
        )
    }.onFailure {
        println("[Tcgdex][!] Failed to parse recognition vectors file ${source.absolutePath}: ${it.message}")
    }.getOrNull()
}

// =============================================================================
// Poképedia fallback harvesting
// =============================================================================

private const val POKEPEDIA_SOURCE = "pokepedia"
private const val CARDMARKET_SOURCE = "cardmarket"

internal data class FallbackImage(
    val url: String,
    val source: String,
)

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

internal fun loadPokepediaFallbacks(
    missingFilePath: String?,
    json: Json,
): Map<String, FallbackImage> {
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

        val resolved = mutableMapOf<String, FallbackImage>()
        var skipped = 0

        for (entry in cards) {
            val rawCardId = entry["cardId"]?.jsonPrimitive?.contentOrNull?.trim()
            if (rawCardId.isNullOrBlank()) continue
            val cardId = rewriteHiddenFatesVaultCardId(rawCardId)

            val status = entry["resolutionStatus"]?.jsonPrimitive?.contentOrNull
            val reason = entry["reason"]?.jsonPrimitive?.contentOrNull
            if (reason.equals("POKEPEDIA_THUMBNAIL_MISSING", ignoreCase = true)) {
                skipped++
                continue
            }

            val hdUrl = entry["pokepediaHdUrl"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
            val thumbUrl = entry["pokepediaThumbnailUrl"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
            val cardmarketUrl = entry["cardmarketImageUrl"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }

            val pokepediaUrl = when {
                status.equals("resolved", ignoreCase = true) -> hdUrl ?: thumbUrl
                reason.equals("POKEPEDIA_HD_UNAVAILABLE", ignoreCase = true) -> thumbUrl
                else -> null
            }

            val fallback = when {
                pokepediaUrl != null -> FallbackImage(url = pokepediaUrl, source = POKEPEDIA_SOURCE)
                cardmarketUrl != null -> FallbackImage(url = cardmarketUrl, source = CARDMARKET_SOURCE)
                else -> null
            }

            if (fallback != null) {
                resolved[cardId] = fallback
            }
        }

        println("[Tcgdex][i] Pokepedia fallbacks loaded: total=${resolved.size}, skipped=$skipped")
        resolved
    }.onFailure {
        println("[Tcgdex][x] Failed to load Pokepedia fallback data: ${it.message}")
    }.getOrDefault(emptyMap())
}

internal const val ASSETS_MANIFEST_URL = "https://assets.tcgdex.net/datas.json"

/**
 * Loads the TCGdex assets manifest (datas.json), the authoritative index of which
 * card pictures exist on the CDN: manifest[language][serieId][setId][localId].
 *
 * This is the same file the vendored cards-database compiler consults in
 * getCardPictures (server/compiler/utils/cardUtil.ts), so URL synthesis in this
 * generator follows the exact same existence guarantee as the upstream API.
 *
 * A local file override (--assets-manifest=/path/to/datas.json) is supported for
 * tests and offline runs. Without an override, a fetch failure aborts generation
 * loudly so a stale/unreachable manifest can never silently produce a database
 * with missing image URLs.
 */
internal fun loadAssetsManifest(
    manifestFileOverride: String?,
    json: Json,
): JsonObject {
    val text = if (!manifestFileOverride.isNullOrBlank()) {
        val file = File(manifestFileOverride)
        require(file.isFile && file.length() > 0L) {
            "[Tcgdex][x] Assets manifest override not found or empty: ${file.absolutePath}"
        }
        file.readText()
    } else {
        try {
            val client = java.net.http.HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(30))
                .followRedirects(java.net.http.HttpClient.Redirect.NORMAL)
                .build()
            val request = java.net.http.HttpRequest.newBuilder(URI.create(ASSETS_MANIFEST_URL))
                .timeout(java.time.Duration.ofSeconds(60))
                .GET()
                .build()
            val response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString())
            check(response.statusCode() == 200) {
                "[Tcgdex][x] Assets manifest request returned HTTP ${response.statusCode()}"
            }
            response.body()
        } catch (e: Exception) {
            throw IllegalStateException(
                "[Tcgdex][x] Cannot fetch assets manifest from $ASSETS_MANIFEST_URL. " +
                    "Pass --assets-manifest=/path/to/datas.json to run offline.",
                e,
            )
        }
    }
    return json.parseToJsonElement(text).jsonObject
}

/**
 * Derives sub-set -> parent set links from official abbreviations of the form
 * PARENT:CODE (e.g. CEL:CC -> the set whose official is CEL).
 *
 * Fails loudly on ambiguity: zero or 2+ parent candidates, or a resolved parent
 * whose id is not a strict prefix of the sub-set id. Cardmarket and Pokepedia
 * do not model sub-sets; the parent id is the mapping key they share.
 */
internal fun deriveSubSetParents(sets: List<JsonObject>): Map<String, String> {
    val officialToSetIds = linkedMapOf<String, MutableList<String>>()
    val colonOfficials = mutableListOf<Pair<String, String>>()
    for (set in sets) {
        val id = set.getString("id")?.takeIf { it.isNotBlank() } ?: continue
        val official = set.getNestedString("abbreviation", "official")?.takeIf { it.isNotBlank() } ?: continue
        officialToSetIds.getOrPut(official) { mutableListOf() }.add(id)
        if (':' in official) {
            colonOfficials += id to official
        }
    }

    val parents = linkedMapOf<String, String>()
    for ((subSetId, official) in colonOfficials) {
        val parentOfficial = official.substringBefore(':')
        val candidates = officialToSetIds[parentOfficial].orEmpty().distinct()
        if (candidates.size != 1) {
            error(
                "[Tcgdex][x] Cannot resolve parent for sub-set $subSetId " +
                    "(official=$official, parentOfficial=$parentOfficial). " +
                    "Candidates: ${candidates.joinToString(",").ifBlank { "<none>" }}",
            )
        }
        val parentId = candidates.single()
        if (parentId == subSetId || !subSetId.startsWith(parentId)) {
            error(
                "[Tcgdex][x] Sub-set $subSetId (official=$official) resolved parent $parentId " +
                    "is not a strict prefix of the sub-set id",
            )
        }
        parents[subSetId] = parentId
    }
    return parents
}

/**
 * Synthesizes a card image URL under the parent set folder for derived sub-sets,
 * but only when the assets manifest confirms the picture exists
 * (manifest[language][serieId][parentSetId][localId]). Returns the same
 * suffix-less URL format the compiler emits; consumers append /high.png or
 * /low.png.
 *
 * [subSetParents] maps sub-set id -> (serieId, parentSetId) from
 * [deriveSubSetParents]. Proposed URLs that the manifest does not list
 * (e.g. cel25cc CC001+, sm115sv SV1 under sm115) stay null.
 */
internal fun synthesizeSubSetImageUrl(
    assetsManifest: JsonObject,
    subSetParents: Map<String, Pair<String, String>>,
    language: String,
    setId: String,
    localId: String,
): String? {
    val (serieId, parentSetId) = subSetParents[setId] ?: return null
    val setEntry = (
        (assetsManifest[language] as? JsonObject)
            ?.get(serieId) as? JsonObject
        )
        ?.get(parentSetId) as? JsonObject
    if (setEntry?.containsKey(localId) != true) return null
    return "https://assets.tcgdex.net/$language/$serieId/$parentSetId/$localId"
}

/**
 * Regenerates the unresolved JSON file from the tree file by filtering only unresolved cards.
 * This ensures both files stay synchronized after database generation.
 */
internal fun regenerateUnresolvedFile(treeFile: File, json: Json) {
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

