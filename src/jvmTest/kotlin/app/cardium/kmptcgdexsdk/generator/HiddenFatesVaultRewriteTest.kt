package app.cardium.kmptcgdexsdk.generator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject

class HiddenFatesVaultRewriteTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `Given sma set id, When rewriting, Then id becomes sm115sv`() {
        assertEquals("sm115sv", rewriteHiddenFatesVaultSetId("sma"))
        assertEquals("sm115", rewriteHiddenFatesVaultSetId("sm115"))
        assertEquals("xya", rewriteHiddenFatesVaultSetId("xya"))
        assertEquals("swsh4.5sv", rewriteHiddenFatesVaultSetId("swsh4.5sv"))
    }

    @Test
    fun `Given sma card id, When rewriting, Then set prefix becomes sm115sv`() {
        assertEquals("sm115sv-SV1", rewriteHiddenFatesVaultCardId("sma-SV1"))
        assertEquals("sm115-1", rewriteHiddenFatesVaultCardId("sm115-1"))
        assertEquals("xya-1", rewriteHiddenFatesVaultCardId("xya-1"))
    }

    @Test
    fun `Given sma official, When rewriting, Then official is HIF SV`() {
        assertEquals("HIF:SV", rewriteHiddenFatesVaultOfficial("sma", "SV"))
        assertEquals("HIF:SV", rewriteHiddenFatesVaultOfficial("sm115sv", "SV"))
        assertEquals("HIF", rewriteHiddenFatesVaultOfficial("sm115", "HIF"))
        assertEquals("SHF:SV", rewriteHiddenFatesVaultOfficial("swsh4.5sv", "SHF:SV"))
    }

    @Test
    fun `Given Hidden Fates Cardmarket SV export ids, When rewriting, Then they unpad onto sm115sv`() {
        assertEquals("sm115sv-SV1", rewriteHiddenFatesCardmarketExportId("sm115-SV001"))
        assertEquals("sm115sv-SV10", rewriteHiddenFatesCardmarketExportId("sm115-SV010"))
        assertEquals("sm115sv-SV94", rewriteHiddenFatesCardmarketExportId("sm115-SV094"))
        assertEquals("sm115sv-SV1", rewriteHiddenFatesCardmarketExportId("sma-SV1"))
        assertEquals("sm115-004", rewriteHiddenFatesCardmarketExportId("sm115-004"))
        assertEquals("sm115-1", rewriteHiddenFatesCardmarketExportId("sm115-1"))
    }

    @Test
    fun `Given sma set JSON, When rewriting, Then id and official become the Hidden Fates vault sub-set`() {
        val rewritten = rewriteHiddenFatesVaultSetJson(
            json.parseToJsonElement(
                """{"id":"sma","abbreviation":{"official":"SV"},"serie":{"id":"sm"}}""",
            ).jsonObject,
        )

        assertEquals("sm115sv", rewritten.string("id"))
        assertEquals("HIF:SV", rewritten.nestedString("abbreviation", "official"))
        assertEquals("sm", rewritten.nestedString("serie", "id"))
    }

    @Test
    fun `Given sma card JSON, When rewriting, Then card id and nested set id are remapped`() {
        val rewritten = rewriteHiddenFatesVaultCardJson(
            json.parseToJsonElement(
                """
                {
                  "id":"sma-SV1",
                  "localId":"SV1",
                  "image":"https://assets.tcgdex.net/en/sm/sma/SV1",
                  "set":{"id":"sma","abbreviation":{"official":"SV"}}
                }
                """.trimIndent(),
            ).jsonObject,
        )

        assertEquals("sm115sv-SV1", rewritten.string("id"))
        assertEquals("SV1", rewritten.string("localId"))
        assertEquals("https://assets.tcgdex.net/en/sm/sma/SV1", rewritten.string("image"))
        assertEquals("sm115sv", rewritten.nestedString("set", "id"))
        assertEquals("HIF:SV", rewritten.nestedString("set", "abbreviation", "official"))
    }

    private fun JsonObject.string(key: String): String? =
        (this[key] as? JsonPrimitive)?.contentOrNull

    private fun JsonObject.nestedString(vararg keys: String): String? {
        var current: JsonObject = this
        keys.dropLast(1).forEach { key ->
            current = current[key] as? JsonObject ?: return null
        }
        return (current[keys.last()] as? JsonPrimitive)?.contentOrNull
    }
}
