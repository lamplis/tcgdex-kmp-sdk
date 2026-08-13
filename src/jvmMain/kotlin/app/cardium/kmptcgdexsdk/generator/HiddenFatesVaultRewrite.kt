package app.cardium.kmptcgdexsdk.generator

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

internal const val HIDDEN_FATES_VAULT_SOURCE_SET_ID = "sma"
internal const val HIDDEN_FATES_VAULT_SET_ID = "sm115sv"
internal const val HIDDEN_FATES_PARENT_SET_ID = "sm115"
internal const val HIDDEN_FATES_VAULT_OFFICIAL = "HIF:SV"

private val hiddenFatesCardmarketSvExportId =
    Regex("^${Regex.escape(HIDDEN_FATES_PARENT_SET_ID)}-SV0*([1-9][0-9]*)$")

internal fun rewriteHiddenFatesVaultSetId(setId: String): String =
    if (setId == HIDDEN_FATES_VAULT_SOURCE_SET_ID) HIDDEN_FATES_VAULT_SET_ID else setId

internal fun rewriteHiddenFatesVaultCardId(cardId: String): String {
    val prefix = "$HIDDEN_FATES_VAULT_SOURCE_SET_ID-"
    if (!cardId.startsWith(prefix)) return cardId
    return "$HIDDEN_FATES_VAULT_SET_ID-${cardId.removePrefix(prefix)}"
}

internal fun rewriteHiddenFatesVaultOfficial(setId: String, official: String?): String? =
    if (rewriteHiddenFatesVaultSetId(setId) == HIDDEN_FATES_VAULT_SET_ID) {
        HIDDEN_FATES_VAULT_OFFICIAL
    } else {
        official
    }

internal fun rewriteHiddenFatesCardmarketExportId(cardId: String): String {
    val fromSourceSet = rewriteHiddenFatesVaultCardId(cardId)
    if (fromSourceSet != cardId) return fromSourceSet
    val match = hiddenFatesCardmarketSvExportId.matchEntire(cardId) ?: return cardId
    return "$HIDDEN_FATES_VAULT_SET_ID-SV${match.groupValues[1]}"
}

internal fun rewriteHiddenFatesVaultSetJson(set: JsonObject): JsonObject {
    val originalId = (set["id"] as? JsonPrimitive)?.contentOrNull ?: return set
    val rewrittenId = rewriteHiddenFatesVaultSetId(originalId)
    val originalOfficial =
        ((set["abbreviation"] as? JsonObject)?.get("official") as? JsonPrimitive)?.contentOrNull
    val rewrittenOfficial = rewriteHiddenFatesVaultOfficial(originalId, originalOfficial)
    if (rewrittenId == originalId && rewrittenOfficial == originalOfficial) return set

    val mutable = set.toMutableMap()
    if (rewrittenId != originalId) {
        mutable["id"] = JsonPrimitive(rewrittenId)
    }
    if (rewrittenOfficial != originalOfficial) {
        val abbreviation = (set["abbreviation"] as? JsonObject)?.toMutableMap() ?: mutableMapOf()
        if (rewrittenOfficial != null) {
            abbreviation["official"] = JsonPrimitive(rewrittenOfficial)
        }
        mutable["abbreviation"] = JsonObject(abbreviation)
    }
    return JsonObject(mutable)
}

internal fun rewriteHiddenFatesVaultCardJson(card: JsonObject): JsonObject {
    val originalId = (card["id"] as? JsonPrimitive)?.contentOrNull
    val rewrittenId = originalId?.let(::rewriteHiddenFatesVaultCardId)
    val originalSet = card["set"] as? JsonObject
    val rewrittenSet = originalSet?.let(::rewriteHiddenFatesVaultSetJson)
    if (rewrittenId == originalId && rewrittenSet === originalSet) return card

    val mutable = card.toMutableMap()
    if (originalId != null && rewrittenId != null && rewrittenId != originalId) {
        mutable["id"] = JsonPrimitive(rewrittenId)
    }
    if (rewrittenSet != null && rewrittenSet !== originalSet) {
        mutable["set"] = rewrittenSet
    }
    return JsonObject(mutable)
}
