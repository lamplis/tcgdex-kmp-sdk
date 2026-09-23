package app.cardium.kmptcgdexsdk.generator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

class ReprintOriginImageUrlTest {
    private val json = Json { ignoreUnknownKeys = true }

    private val manifest = json.parseToJsonElement(
        """
        {
          "en": { "ex": { "ex7": { "19": { "high": true } } } },
          "fr": { "ex": { "ex7": { "19": { "high": true } } } }
        }
        """.trimIndent(),
    ).jsonObject

    @Test
    fun `Given 30th-c-006 and a confirmed origin scan, When synthesizing, Then language-specific URL is returned`() {
        assertEquals(
            "https://assets.tcgdex.net/en/ex/ex7/19",
            synthesizeReprintOriginImageUrl(manifest, "30th-c-006", "en"),
        )
        assertEquals(
            "https://assets.tcgdex.net/fr/ex/ex7/19",
            synthesizeReprintOriginImageUrl(manifest, "30th-c-006", "fr"),
        )
    }

    @Test
    fun `Given origin localId missing from the manifest, When synthesizing, Then null`() {
        val missing = json.parseToJsonElement(
            """{ "en": { "ex": { "ex7": { "1": { "high": true } } } } }""",
        ).jsonObject
        assertNull(synthesizeReprintOriginImageUrl(missing, "30th-c-006", "en"))
    }

    @Test
    fun `Given an unmapped card, When synthesizing, Then null`() {
        assertNull(synthesizeReprintOriginImageUrl(manifest, "30th-c-001", "en"))
    }
}
