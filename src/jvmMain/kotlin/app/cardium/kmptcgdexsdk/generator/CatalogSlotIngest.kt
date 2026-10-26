package app.cardium.kmptcgdexsdk.generator

import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull

data class IngestedCatalogSlot(
    val variantId: String,
    val isCanonical: Boolean,
    val sortIndex: Int,
    val displayRank: Int,
    val typeSlug: String,
    val subtypeSlug: String?,
    val sizeSlug: String,
    val foilSlug: String?,
    val stampSlugs: List<String>,
    val typeLabel: String,
    val subtypeLabel: String?,
    val sizeLabel: String,
    val foilLabel: String?,
    val stampLabels: List<String>,
    val imageUrl: String?,
    val cardmarketProductId: Long?,
    val tcgplayerProductId: Long?,
)

data class CatalogSlotSupplement(
    val cardId: String,
    val variantId: String,
    val typeSlug: String,
    val subtypeSlug: String? = null,
    val sizeSlug: String = "standard",
    val foilSlug: String? = null,
    val stampSlugs: List<String> = emptyList(),
    val label: String,
    val displayRank: Int = 1_500_000,
)

fun verifyPrintCodeInvariants(
    cardId: String,
    slots: List<IngestedCatalogSlot>,
) {
    val tail = cardId.substringAfterLast('.')
    if ('.' in cardId && tail.firstOrNull()?.isLetter() == true) {
        error(
            "[Tcgdex][x] Card id '$cardId' has a dotted segment starting with a letter; " +
                "public print codes would be ambiguous",
        )
    }
    val descriptors =
        slots.map { slot ->
            listOf(
                slot.typeSlug,
                slot.subtypeSlug.orEmpty(),
                slot.sizeSlug,
                slot.foilSlug.orEmpty(),
                slot.stampSlugs.sorted().joinToString(","),
            )
        }
    if (descriptors.size != descriptors.toSet().size) {
        error("[Tcgdex][x] Card '$cardId' has two catalog slots with the same print descriptor")
    }
    if (slots.count { it.isCanonical } > 1) {
        error("[Tcgdex][x] Card '$cardId' has more than one canonical catalog slot")
    }
}

fun ingestCatalogSlots(
    card: JsonObject,
    englishVariants: List<JsonObject>?,
    supplements: List<CatalogSlotSupplement> = emptyList(),
    cardImageUrl: String? = null,
): List<IngestedCatalogSlot> {
    val detailed = card["variants_detailed"] as? JsonArray
    val parsed = mutableListOf<IngestedCatalogSlot>()
    val seen = mutableSetOf<String>()
    detailed?.forEachIndexed { index, element ->
        val variant = element as? JsonObject ?: return@forEachIndexed
        val english = englishVariants?.getOrNull(index)
        val slot = variant.toIngestedSlot(index = index, english = english, cardImageUrl = cardImageUrl)
        if (seen.add(slot.variantId)) parsed += slot
    }
    if (parsed.isEmpty()) {
        parsed += fallbackSlot(cardImageUrl)
    }
    val canonicalIndex = chooseCanonicalIndex(parsed)
    val withCanonical =
        parsed.mapIndexed { index, slot ->
            slot.copy(isCanonical = index == canonicalIndex)
        }
    val extra =
        supplements.mapNotNull { supplement ->
            if (withCanonical.any { it.variantId == supplement.variantId }) return@mapNotNull null
            if (withCanonical.any { samePrint(it, supplement) }) return@mapNotNull null
            IngestedCatalogSlot(
                variantId = supplement.variantId,
                isCanonical = false,
                sortIndex = 10_000 + supplement.displayRank,
                displayRank = supplement.displayRank,
                typeSlug = supplement.typeSlug,
                subtypeSlug = supplement.subtypeSlug,
                sizeSlug = supplement.sizeSlug,
                foilSlug = supplement.foilSlug,
                stampSlugs = supplement.stampSlugs,
                typeLabel = supplement.label,
                subtypeLabel = supplement.subtypeSlug,
                sizeLabel = supplement.sizeSlug,
                foilLabel = supplement.foilSlug,
                stampLabels = supplement.stampSlugs,
                imageUrl = cardImageUrl,
                cardmarketProductId = null,
                tcgplayerProductId = null,
            )
        }
    return (withCanonical + extra).sortedWith(compareBy<IngestedCatalogSlot> { it.displayRank }.thenBy { it.variantId })
}

private fun samePrint(
    slot: IngestedCatalogSlot,
    supplement: CatalogSlotSupplement,
): Boolean =
    slot.typeSlug == supplement.typeSlug &&
        slot.foilSlug == supplement.foilSlug &&
        slot.stampSlugs == supplement.stampSlugs &&
        slot.sizeSlug == supplement.sizeSlug &&
        slot.subtypeSlug == supplement.subtypeSlug

private fun fallbackSlot(cardImageUrl: String?): IngestedCatalogSlot =
    IngestedCatalogSlot(
        variantId = "legacy-normal-none-standard-none-",
        isCanonical = true,
        sortIndex = 0,
        displayRank = 1_000_000,
        typeSlug = "normal",
        subtypeSlug = null,
        sizeSlug = "standard",
        foilSlug = null,
        stampSlugs = emptyList(),
        typeLabel = "Normal",
        subtypeLabel = null,
        sizeLabel = "standard",
        foilLabel = null,
        stampLabels = emptyList(),
        imageUrl = cardImageUrl,
        cardmarketProductId = null,
        tcgplayerProductId = null,
    )

private fun chooseCanonicalIndex(slots: List<IngestedCatalogSlot>): Int {
    val normal =
        slots.indexOfFirst {
            it.typeSlug == "normal" &&
                it.sizeSlug == "standard" &&
                it.foilSlug == null &&
                it.stampSlugs.isEmpty() &&
                (it.subtypeSlug == null || it.subtypeSlug == "unlimited")
        }
    if (normal >= 0) return normal
    return 0
}

private fun JsonObject.toIngestedSlot(
    index: Int,
    english: JsonObject?,
    cardImageUrl: String?,
): IngestedCatalogSlot {
    val typeLabel = string("type") ?: "normal"
    val sizeLabel = string("size") ?: "standard"
    val subtypeLabel = string("subtype")
    val foilLabel = string("foil")
    val stampLabels = stringList("stamp")
    val typeSlug = slug(english?.string("type") ?: typeLabel)
    val sizeSlug = slug(english?.string("size") ?: sizeLabel)
    val subtypeSlug = (english?.string("subtype") ?: subtypeLabel)?.let(::slug)
    val foilSlug = (english?.string("foil") ?: foilLabel)?.let(::slug)
    val stampSlugs = (english?.stringList("stamp") ?: stampLabels).map(::slug).sorted()
    val rawId = string("variantId")
    val variantId =
        if (rawId.isNullOrBlank() || rawId == "generated") {
            legacyVariantId(typeSlug, subtypeSlug, sizeSlug, foilSlug, stampSlugs)
        } else {
            rawId
        }
    val firstEdition = stampSlugs.any { it.contains("1st") || it.contains("first") }
    return IngestedCatalogSlot(
        variantId = variantId,
        isCanonical = false,
        sortIndex = index,
        displayRank = (if (firstEdition) 0 else 1) * 1_000_000 + index,
        typeSlug = typeSlug,
        subtypeSlug = subtypeSlug,
        sizeSlug = sizeSlug,
        foilSlug = foilSlug,
        stampSlugs = stampSlugs,
        typeLabel = typeLabel,
        subtypeLabel = subtypeLabel,
        sizeLabel = sizeLabel,
        foilLabel = foilLabel,
        stampLabels = stampLabels,
        imageUrl = string("image") ?: cardImageUrl,
        cardmarketProductId = marketplaceId("cardmarket"),
        tcgplayerProductId = marketplaceId("tcgplayer"),
    )
}

fun loadCatalogSlotSupplements(file: File): Map<String, List<CatalogSlotSupplement>> {
    if (!file.exists()) return emptyMap()
    val root = Json.parseToJsonElement(file.readText()).jsonObject
    val prints = root["prints"] as? JsonArray ?: return emptyMap()
    return prints
        .mapNotNull { element ->
            val obj = element as? JsonObject ?: return@mapNotNull null
            val cardId = obj.string("cardId") ?: return@mapNotNull null
            val variantId = obj.string("variantId") ?: return@mapNotNull null
            cardId to
                CatalogSlotSupplement(
                    cardId = cardId,
                    variantId = variantId,
                    typeSlug = obj.string("typeSlug") ?: "normal",
                    subtypeSlug = obj.string("subtypeSlug"),
                    sizeSlug = obj.string("sizeSlug") ?: "standard",
                    foilSlug = obj.string("foilSlug"),
                    stampSlugs = obj.stringList("stampSlugs"),
                    label = obj.string("label") ?: variantId,
                    displayRank = (obj["displayRank"] as? JsonPrimitive)?.intOrNull ?: 1_500_000,
                )
        }.groupBy({ it.first }, { it.second })
}

fun List<String>.toJsonArrayString(): String =
    joinToString(prefix = "[", postfix = "]") { value ->
        "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
    }

internal fun legacyVariantId(
    typeSlug: String,
    subtypeSlug: String?,
    sizeSlug: String,
    foilSlug: String?,
    stampSlugs: List<String>,
): String = "legacy-$typeSlug-${subtypeSlug ?: "none"}-$sizeSlug-${foilSlug ?: "none"}-${stampSlugs.joinToString("+")}"

private fun JsonObject.string(key: String): String? = (this[key] as? JsonPrimitive)?.contentOrNull

private fun JsonObject.stringList(key: String): List<String> {
    val array = this[key] as? JsonArray ?: return emptyList()
    return array.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }
}

private fun JsonObject.marketplaceId(marketplace: String): Long? {
    val thirdParty = this["thirdParty"] as? JsonObject ?: return null
    val value = thirdParty[marketplace] ?: return null
    return (value as? JsonPrimitive)?.intOrNull?.toLong()
        ?: (value as? JsonObject)?.get("id")?.let { (it as? JsonPrimitive)?.intOrNull?.toLong() }
}

private fun slug(value: String): String = value.trim().lowercase()
