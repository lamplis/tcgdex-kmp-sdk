package app.cardium.kmptcgdexsdk.generator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

class PrizePackImageUrlTest {
    private val json = Json { ignoreUnknownKeys = true }
    private val parents = mapOf("BST" to ("swsh" to "swsh5"))

    private val manifest = json.parseToJsonElement(
        """
        {
          "fr": { "swsh": { "swsh5": { "22": { "high": true } } } },
          "en": { "swsh": { "swsh5": { "22": { "high": true } } } }
        }
        """.trimIndent(),
    ).jsonObject

    @Test
    fun `Given pps1 BST22 and a confirmed parent scan, When synthesizing, Then language-specific URL is returned`() {
        assertEquals(
            "https://assets.tcgdex.net/fr/swsh/swsh5/22",
            synthesizePrizePackImageUrl(manifest, parents, "fr", "pps1", "BST22"),
        )
        assertEquals(
            "https://assets.tcgdex.net/en/swsh/swsh5/22",
            synthesizePrizePackImageUrl(manifest, parents, "en", "pps1", "BST22"),
        )
    }

    @Test
    fun `Given parent localId missing from the manifest, When synthesizing, Then null`() {
        val missing = json.parseToJsonElement(
            """{ "fr": { "swsh": { "swsh5": { "1": { "high": true } } } } }""",
        ).jsonObject
        assertNull(synthesizePrizePackImageUrl(missing, parents, "fr", "pps1", "BST22"))
    }

    @Test
    fun `Given an energy localId or a non prize pack set, When synthesizing, Then null`() {
        assertNull(synthesizePrizePackImageUrl(manifest, parents, "fr", "pps1", "Grass"))
        assertNull(synthesizePrizePackImageUrl(manifest, parents, "fr", "swsh5", "BST22"))
    }
}
