package app.cardium.kmptcgdexsdk.generator

import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject

data class CardmarketExportPrice(
    val condition: String,
    val medianPrice: Double?,
    val avgPrice: Double?,
    val minPrice: Double?,
    val maxPrice: Double?,
    val recommendedPrice: Double?,
    val availableCount: Int?,
    val currency: String?,
    val capturedAt: String? = null,
)

data class CardmarketExportVariant(
    val version: String?,
    val productId: Int?,
    val label: String?,
    // prices[priceLanguage][sellerCountry][condition] -> CardmarketExportPrice
    // Each Cardmarket condition bucket (e.g. "NM", "MT", "EX") is stored as its own
    // entry. The generator no longer collapses conditions; the app decides which
    // condition to display.
    val prices: Map<String, Map<String, Map<String, CardmarketExportPrice>>>,
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

internal fun resolveExportPriceUpdatedIso(
    capturedAt: String?,
    exportUpdatedIso: String?,
): String = capturedAt?.takeIf { it.isNotBlank() } ?: exportUpdatedIso.orEmpty()

private fun mergeCardmarketVariantPrices(
    existing: CardmarketExportVariant,
    incoming: CardmarketExportVariant,
): CardmarketExportVariant {
    val mergedPrices: MutableMap<String, MutableMap<String, MutableMap<String, CardmarketExportPrice>>> =
        existing.prices
            .mapValues { (_, byCountry) ->
                byCountry
                    .mapValues { (_, byCondition) -> byCondition.toMutableMap() }
                    .toMutableMap()
            }
            .toMutableMap()

    for ((priceLang, incomingByCountry) in incoming.prices) {
        val langBucket = mergedPrices.getOrPut(priceLang) { mutableMapOf() }
        for ((country, incomingByCondition) in incomingByCountry) {
            val countryBucket = langBucket.getOrPut(country) { mutableMapOf() }
            for ((condition, price) in incomingByCondition) {
                countryBucket[condition] = price
            }
        }
    }

    return CardmarketExportVariant(
        version = existing.version ?: incoming.version,
        productId = existing.productId ?: incoming.productId,
        label = existing.label ?: incoming.label,
        prices = mergedPrices.mapValues { (_, byCountry) ->
            byCountry.mapValues { (_, byCondition) -> byCondition.toMap() }
        },
    )
}

private fun mergeCardmarketExportCards(
    target: MutableMap<String, CardmarketExportCard>,
    incoming: Map<String, CardmarketExportCard>,
) {
    for ((cardId, incomingCard) in incoming) {
        val existingCard = target[cardId]
        if (existingCard == null) {
            target[cardId] = incomingCard
            continue
        }

        val mergedVariants = existingCard.variants.toMutableList()
        for (incomingVariant in incomingCard.variants) {
            val existingIndex = mergedVariants.indexOfFirst {
                it.version == incomingVariant.version &&
                    it.productId == incomingVariant.productId &&
                    it.label == incomingVariant.label
            }
            if (existingIndex >= 0) {
                mergedVariants[existingIndex] = mergeCardmarketVariantPrices(
                    mergedVariants[existingIndex],
                    incomingVariant,
                )
            } else {
                mergedVariants.add(incomingVariant)
            }
        }

        mergedVariants.sortWith(
            compareBy<CardmarketExportVariant>(
                { it.version ?: "" },
                { it.productId ?: Int.MAX_VALUE },
                { it.label ?: "" },
            ),
        )

        target[cardId] = CardmarketExportCard(
            tcgdexCardId = cardId,
            name = existingCard.name ?: incomingCard.name,
            variants = mergedVariants,
        )
    }
}

internal fun parseCardmarketExportFile(
    file: File,
    json: Json,
): CardmarketExportPrices? {
    return runCatching {
        fun JsonObject.stringOrNull(key: String): String? =
            (this[key] as? JsonPrimitive)?.contentOrNull

        // Reads a scalar int from either a flat primitive OR the *first* numeric value
        // found inside a condition map. Used for the variant-level productId which has
        // never been condition-keyed in the input JSON.
        fun JsonObject.intOrNullSafe(key: String): Int? {
            val value = this[key] ?: return null
            return when (value) {
                is JsonPrimitive -> value.intOrNull
                is JsonObject -> value.values
                    .mapNotNull { (it as? JsonPrimitive)?.doubleOrNull }
                    .firstOrNull()
                    ?.toInt()
                else -> null
            }
        }

        // Returns every condition key present across the five metric maps for one
        // (priceLang, country) block. Each resulting condition becomes its own
        // card_prices row -- no collapse, no fallback.
        fun collectConditions(priceObj: JsonObject): List<String> {
            val metricKeys = listOf(
                "recommendedPrice",
                "avgPrice",
                "minPrice",
                "medianPrice",
                "maxPrice",
                "availableCount",
            )
            val conditions = linkedSetOf<String>()
            for (key in metricKeys) {
                val nested = priceObj[key] as? JsonObject ?: continue
                for (condition in nested.keys) {
                    if (condition.isNotBlank()) conditions.add(condition)
                }
            }
            return conditions.toList()
        }

        // Reads a single metric for a specific condition (e.g. recommendedPrice.NM).
        // Returns null if the metric object is absent OR if the condition key is missing.
        // Never flattens across conditions.
        fun JsonObject.doubleForCondition(key: String, condition: String): Double? {
            val nested = this[key] as? JsonObject ?: return null
            return (nested[condition] as? JsonPrimitive)?.doubleOrNull
        }

        fun JsonObject.intForCondition(key: String, condition: String): Int? {
            val nested = this[key] as? JsonObject ?: return null
            val primitive = nested[condition] as? JsonPrimitive ?: return null
            // Cardmarket exports availableCount as a Double (e.g. 22.0), so parse loosely.
            return primitive.intOrNull ?: primitive.doubleOrNull?.toInt()
        }

        val root = json.parseToJsonElement(file.readText()).jsonObject
        val exportDate = root.stringOrNull("exportDate") ?: ""
        val series = (root["series"] as? JsonArray) ?: JsonArray(emptyList())

        val cardsById = mutableMapOf<String, CardmarketExportCard>()
        val languages = mutableSetOf<String>()
        val countries = mutableSetOf<String>()
        val conditionsSeen = mutableSetOf<String>()
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
                    val rawCardId = cardObj.stringOrNull("tcgdexCardId")?.trim()
                    if (rawCardId.isNullOrBlank()) continue
                    val cardId = rewriteHiddenFatesCardmarketExportId(rawCardId)
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

                            val capturedAtByLang = mutableMapOf<String, String>()
                            val lifecycleObj = variantObj["lifecycle"] as? JsonObject
                            if (lifecycleObj != null) {
                                for ((langRaw, entryEl) in lifecycleObj) {
                                    val lang = langRaw.lowercase().trim()
                                    val captured = (entryEl as? JsonObject)
                                        ?.stringOrNull("captured_at")
                                        ?.trim()
                                        .orEmpty()
                                    if (lang.isNotEmpty() && captured.isNotEmpty()) {
                                        capturedAtByLang[lang] = captured
                                    }
                                }
                            }

                            val prices:
                                MutableMap<String, MutableMap<String, MutableMap<String, CardmarketExportPrice>>> =
                                mutableMapOf()
                            var hasAnyPrice = false

                            for ((priceLangRaw, priceLangElement) in pricesObj) {
                                val priceLang = priceLangRaw.lowercase().trim()
                                val byCountryObj = priceLangElement as? JsonObject ?: continue

                                for ((countryRaw, countryElement) in byCountryObj) {
                                    val country = countryRaw.uppercase().trim()
                                    val priceObj = countryElement as? JsonObject ?: continue
                                    val currency = priceObj.stringOrNull("currency")
                                    val conditions = collectConditions(priceObj)
                                    if (conditions.isEmpty()) continue

                                    for (condition in conditions) {
                                        val exportPrice = CardmarketExportPrice(
                                            condition = condition,
                                            medianPrice = priceObj.doubleForCondition("medianPrice", condition),
                                            avgPrice = priceObj.doubleForCondition("avgPrice", condition),
                                            minPrice = priceObj.doubleForCondition("minPrice", condition),
                                            maxPrice = priceObj.doubleForCondition("maxPrice", condition),
                                            recommendedPrice = priceObj.doubleForCondition(
                                                "recommendedPrice",
                                                condition,
                                            ),
                                            availableCount = priceObj.intForCondition("availableCount", condition),
                                            currency = currency,
                                            capturedAt = capturedAtByLang[priceLang],
                                        )

                                        // Drop purely empty rows (all five price metrics null) but keep
                                        // rows where Cardmarket reported a 0.0 -- that is real source
                                        // data and the app decides how to interpret it.
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
                                        val countryMap = langMap.getOrPut(country) { mutableMapOf() }
                                        countryMap[condition] = exportPrice
                                        languages.add(priceLang)
                                        countries.add(country)
                                        conditionsSeen.add(condition)
                                        priceEntryCount++
                                        hasAnyPrice = true
                                    }
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
            "[Tcgdex] Parsed Cardmarket export file ${file.name}: cards=$cardCount " +
                "cardsWithPrices=${cardsById.size} variants=$variantCount pricedVariants=$pricedVariantCount " +
                "entries=$priceEntryCount languages=${languages.sorted()} countries=${countries.sorted()} " +
                "conditions=${conditionsSeen.sorted()} updated=$exportDate",
        )

        CardmarketExportPrices(
            updatedIso = exportDate,
            cards = cardsById,
        )
    }.onFailure {
        println("[Tcgdex][!] Failed to parse Cardmarket export file ${file.absolutePath}: ${it.message}")
    }.getOrNull()
}

internal fun resolveDefaultCardmarketExportPath(projectRoot: File): File? {
    val canonical = projectRoot.resolve("libs/tcgdex-kmp-sdk/generator-inputs/cardmarket")
    if (canonical.exists()) {
        return canonical
    }

    val legacy = projectRoot.resolve("exports/prices")
    if (legacy.exists()) {
        println("[Tcgdex][!] DEPRECATED: Using legacy Cardmarket export path: ${legacy.absolutePath}")
        println("[Tcgdex][i] Move exports to: ${canonical.absolutePath}")
        return legacy
    }

    return null
}

internal fun loadCardmarketExportPrices(
    projectRoot: File,
    exportFilePath: String?,
    json: Json,
): CardmarketExportPrices? {
    val source =
        if (!exportFilePath.isNullOrBlank()) {
            File(exportFilePath).also { explicit ->
                if (!explicit.exists()) {
                    println("[Tcgdex][!] Cardmarket export path not found: ${explicit.absolutePath}")
                }
            }.takeIf { it.exists() }
        } else {
            resolveDefaultCardmarketExportPath(projectRoot)
        }

    if (source == null) {
        println("[Tcgdex][i] No Cardmarket export input found, skipping export import")
        return null
    }

    println("[Tcgdex] Cardmarket export source: ${source.absolutePath}")

    val exportFiles: List<File> =
        if (source.isDirectory) {
            val perLanguageFiles = source
                .listFiles()
                ?.filter { file ->
                    file.isFile && Regex("""cardmarket-prices-[a-z]{2}\.json""").matches(file.name)
                }
                ?.sortedBy { it.name }
                .orEmpty()
            if (perLanguageFiles.isNotEmpty()) {
                perLanguageFiles
            } else {
                val monolithic = File(source, "cardmarket-prices.json")
                if (monolithic.exists()) listOf(monolithic) else emptyList()
            }
        } else {
            listOf(source)
        }

    if (exportFiles.isEmpty()) {
        println("[Tcgdex][!] No Cardmarket export JSON files found at: ${source.absolutePath}")
        return null
    }

    val mergedCards = mutableMapOf<String, CardmarketExportCard>()
    var mergedUpdatedIso = ""
    for (exportFile in exportFiles) {
        val parsed = parseCardmarketExportFile(exportFile, json) ?: continue
        mergeCardmarketExportCards(mergedCards, parsed.cards)
        if (parsed.updatedIso > mergedUpdatedIso) {
            mergedUpdatedIso = parsed.updatedIso
        }
    }

    if (mergedCards.isEmpty()) {
        println("[Tcgdex][!] Cardmarket export files were readable but no priced cards were found")
        return null
    }

    val languages = mutableSetOf<String>()
    val countries = mutableSetOf<String>()
    val conditions = mutableSetOf<String>()
    var variantCount = 0
    var pricedVariantCount = 0
    var priceEntryCount = 0
    for (card in mergedCards.values) {
        for (variant in card.variants) {
            variantCount++
            var variantHasPrice = false
            for ((priceLang, byCountry) in variant.prices) {
                languages.add(priceLang)
                for ((country, byCondition) in byCountry) {
                    countries.add(country)
                    for ((condition, _) in byCondition) {
                        conditions.add(condition)
                        priceEntryCount++
                        variantHasPrice = true
                    }
                }
            }
            if (variantHasPrice) {
                pricedVariantCount++
            }
        }
    }

    println(
        "[Tcgdex] Cardmarket export loaded from ${exportFiles.size} file(s): cardsWithPrices=${mergedCards.size} " +
            "variants=$variantCount pricedVariants=$pricedVariantCount entries=$priceEntryCount " +
            "languages=${languages.sorted()} countries=${countries.sorted()} " +
            "conditions=${conditions.sorted()} updated=$mergedUpdatedIso",
    )

    return CardmarketExportPrices(
        updatedIso = mergedUpdatedIso,
        cards = mergedCards,
    )
}
