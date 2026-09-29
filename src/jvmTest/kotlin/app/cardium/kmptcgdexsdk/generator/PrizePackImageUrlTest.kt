package app.cardium.kmptcgdexsdk.generator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
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

    @Test
    fun `Given a unique official abbreviation, When building parents, Then that set is used`() {
        val resolved = prizePackAbbreviationParents(
            sets("""{"id":"swsh5","serie":{"id":"swsh"},"abbreviation":{"official":"BST"}}"""),
        )

        assertEquals(mapOf("BST" to ("swsh" to "swsh5")), resolved)
        assertEquals(
            "https://assets.tcgdex.net/fr/swsh/swsh5/22",
            synthesizePrizePackImageUrl(manifest, resolved, "fr", "pps1", "BST22"),
        )
    }

    @Test
    fun `Given the same set twice, When building parents, Then the abbreviation is kept`() {
        val resolved = prizePackAbbreviationParents(
            sets(
                """{"id":"swsh5","serie":{"id":"swsh"},"abbreviation":{"official":"BST"}}""",
                """{"id":"swsh5","serie":{"id":"swsh"},"abbreviation":{"official":"BST"}}""",
            ),
        )

        assertEquals(mapOf("BST" to ("swsh" to "swsh5")), resolved)
    }

    @Test
    fun `Given two sets sharing an abbreviation, When synthesizing, Then null instead of the first parent`() {
        val resolved = prizePackAbbreviationParents(
            sets(
                """{"id":"sv05","serie":{"id":"sv"},"abbreviation":{"official":"TEF"}}""",
                """{"id":"sv05a","serie":{"id":"sv"},"abbreviation":{"official":"TEF"}}""",
                """{"id":"me03","serie":{"id":"me"},"abbreviation":{"official":"POR"}}""",
            ),
        )
        val trap = json.parseToJsonElement(
            """
            {
              "fr": {
                "sv": {
                  "sv05": { "12": { "high": true } },
                  "sv05a": { "12": { "high": true } }
                }
              }
            }
            """.trimIndent(),
        ).jsonObject

        assertEquals(mapOf("POR" to ("me" to "me03")), resolved)
        assertNull(synthesizePrizePackImageUrl(trap, resolved, "fr", "pps1", "TEF12"))
    }

    @Test
    fun `Given blank id serie or official, When building parents, Then those sets are skipped`() {
        val resolved = prizePackAbbreviationParents(
            sets(
                """{"id":"","serie":{"id":"swsh"},"abbreviation":{"official":"BST"}}""",
                """{"id":"swsh5","serie":{"id":""},"abbreviation":{"official":"BST"}}""",
                """{"id":"swsh5","serie":{"id":"swsh"},"abbreviation":{"official":""}}""",
                """{"id":"swsh5","serie":{"id":"swsh"},"abbreviation":{"official":null}}""",
                """{"id":"swsh5","serie":{"id":"swsh"}}""",
                """{"serie":{"id":"swsh"},"abbreviation":{"official":"BST"}}""",
                """{"id":"me03","serie":{"id":"me"},"abbreviation":{"official":"POR"}}""",
            ),
        )

        assertEquals(mapOf("POR" to ("me" to "me03")), resolved)
        assertEquals(emptyMap(), prizePackAbbreviationParents(emptyList()))
    }

    @Test
    fun `Given TG12 and both TG and TG1, When synthesizing, Then the letter prefix is used`() {
        val resolved = prizePackAbbreviationParents(
            sets(
                """{"id":"short","serie":{"id":"sv"},"abbreviation":{"official":"TG"}}""",
                """{"id":"long","serie":{"id":"sv"},"abbreviation":{"official":"TG1"}}""",
            ),
        )
        val tgManifest = json.parseToJsonElement(
            """
            {
              "fr": {
                "sv": {
                  "short": { "12": { "high": true } },
                  "long": { "2": { "high": true } }
                }
              }
            }
            """.trimIndent(),
        ).jsonObject

        assertEquals(
            "https://assets.tcgdex.net/fr/sv/short/12",
            synthesizePrizePackImageUrl(tgManifest, resolved, "fr", "pps2", "TG12"),
        )
    }

    @Test
    fun `Given only digit-containing abbreviation TG1, When synthesizing TG12, Then null`() {
        val resolved = prizePackAbbreviationParents(
            sets("""{"id":"long","serie":{"id":"sv"},"abbreviation":{"official":"TG1"}}"""),
        )
        val tgManifest = json.parseToJsonElement(
            """{ "fr": { "sv": { "long": { "2": { "high": true }, "12": { "high": true } } } } }""",
        ).jsonObject

        assertNull(synthesizePrizePackImageUrl(tgManifest, resolved, "fr", "pps2", "TG12"))
    }

    @Test
    fun `Given SWSH149 and only the SWSHP promo abbreviation, When synthesizing, Then null`() {
        val resolved = prizePackAbbreviationParents(
            sets("""{"id":"swshp","serie":{"id":"swsh"},"abbreviation":{"official":"SWSHP"}}"""),
        )
        val promoManifest = json.parseToJsonElement(
            """
            { "en": { "swsh": { "swshp": { "149": { "high": true }, "SWSH149": { "high": true } } } } }
            """.trimIndent(),
        ).jsonObject

        assertNull(synthesizePrizePackImageUrl(promoManifest, resolved, "en", "pps3", "SWSH149"))
    }

    @Test
    fun `Given a lowercase local id an uppercase set id or a missing language, When synthesizing, Then null`() {
        assertNull(synthesizePrizePackImageUrl(manifest, parents, "fr", "pps1", "bst22"))
        assertNull(synthesizePrizePackImageUrl(manifest, parents, "fr", "PPS1", "BST22"))
        assertNull(synthesizePrizePackImageUrl(manifest, parents, "de", "pps1", "BST22"))
        assertNull(synthesizePrizePackImageUrl(manifest, parents, "fr", "pps1", "ZZZ22"))
    }

    private fun sets(vararg bodies: String): List<JsonObject> =
        bodies.map { json.parseToJsonElement(it).jsonObject }
}
