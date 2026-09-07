package app.cardium.kmptcgdexsdk.generator

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

private val MFB_FRENCH_NAMES: Map<String, String> =
    mapOf(
        "1" to "Bulbizarre",
        "2" to "Herbizarre",
        "3" to "Mystherbe",
        "4" to "Ortide",
        "5" to "Noeunoeuf",
        "6" to "Noadkoko",
        "7" to "Inséctateur",
        "8" to "Énergie Plante",
        "9" to "Salamèche",
        "10" to "Reptincel",
        "11" to "Goupix",
        "12" to "Feunard",
        "13" to "Caninos",
        "14" to "Arcanin",
        "15" to "Magmar",
        "16" to "Énergie Feu",
        "17" to "Pikachu",
        "18" to "Raichu",
        "19" to "Magnéti",
        "20" to "Magnéton",
        "21" to "Voltorbe",
        "22" to "Électrode",
        "23" to "Élektek",
        "24" to "Énergie Électrique",
        "25" to "Carapuce",
        "26" to "Carabaffe",
        "27" to "Ptitard",
        "28" to "Têtarte",
        "29" to "Magicarpe",
        "30" to "Léviator",
        "31" to "Lokhlass",
        "32" to "Énergie Eau",
        "33" to "Potion",
        "34" to "Échange",
    )

internal const val MFB_SET_ID = "mfb"
internal const val MFB_FRENCH_SET_NAME = "Mon premier combat"

internal fun frenchNameForMfbLocalId(localId: String): String? {
    val trimmed = localId.trim()
    if (trimmed.isEmpty()) {
        return null
    }
    return MFB_FRENCH_NAMES[trimmed]
}

internal data class EnglishMfbSetClone(
    val serieId: String,
    val logoUrl: String?,
    val symbolUrl: String?,
    val cardCountTotal: Long,
    val cardCountOfficial: Long,
    val releaseDate: String?,
    val abbreviationOfficial: String?,
    val parentSetId: String?,
)

internal fun localizeMfbCardJson(card: JsonObject): JsonObject {
    val localId =
        card["localId"]?.jsonPrimitive?.contentOrNull
            ?: card["id"]?.jsonPrimitive?.contentOrNull?.substringAfterLast("-")
            ?: return card
    val frenchName = frenchNameForMfbLocalId(localId) ?: return card
    return JsonObject(card.toMutableMap().apply { put("name", JsonPrimitive(frenchName)) })
}
