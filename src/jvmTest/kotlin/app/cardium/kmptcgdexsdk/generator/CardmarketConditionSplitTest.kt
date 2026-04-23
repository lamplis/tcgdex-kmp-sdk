package app.cardium.kmptcgdexsdk.generator

import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

/**
 * Verifies that the Cardmarket export parser preserves each condition bucket as its
 * own row instead of collapsing them. Reproduces the real-world shape of
 * `me02.5-276` FR where NM=0 sits alongside MT=650.
 */
class CardmarketConditionSplitTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `Given NM zero and MT positive in the same price block, When parsed, Then both conditions land as separate entries`() {
        val file = writeFixture(
            """
            {
              "exportDate": "2026-04-22T21:40:21Z",
              "series": [
                {
                  "sets": [
                    {
                      "cards": [
                        {
                          "tcgdexCardId": "me02.5-276",
                          "name": "Pikachu ex",
                          "variants": [
                            {
                              "version": "V1",
                              "productId": 869888,
                              "label": null,
                              "prices": {
                                "fr": {
                                  "FR": {
                                    "currency": "EUR",
                                    "minPrice":         { "NM": 0.0,   "MT": 600.0 },
                                    "avgPrice":         { "NM": 443.0, "MT": 700.0 },
                                    "medianPrice":      { "NM": 524.0, "MT": 650.0 },
                                    "maxPrice":         { "NM": 626.0, "MT": 850.0 },
                                    "availableCount":   { "NM": 22.0,  "MT": 6.0 },
                                    "recommendedPrice": { "NM": 0.0,   "MT": 650.0 }
                                  }
                                }
                              }
                            }
                          ]
                        }
                      ]
                    }
                  ]
                }
              ]
            }
            """.trimIndent(),
        )

        val parsed = parseCardmarketExportFile(file, json)
        assertNotNull(parsed)
        val card = parsed.cards["me02.5-276"]
        assertNotNull(card)
        assertEquals(1, card.variants.size)
        val frFrPrices = card.variants.first().prices["fr"]?.get("FR")
        assertNotNull(frFrPrices, "expected fr/FR block")
        assertEquals(setOf("NM", "MT"), frFrPrices.keys, "each Cardmarket condition should be its own entry")

        val nm = frFrPrices.getValue("NM")
        assertEquals("NM", nm.condition)
        assertEquals(0.0, nm.recommendedPrice, "raw zero preserved -- app layer decides how to interpret it")
        assertEquals(443.0, nm.avgPrice)
        assertEquals(0.0, nm.minPrice)
        assertEquals(22, nm.availableCount)

        val mt = frFrPrices.getValue("MT")
        assertEquals("MT", mt.condition)
        assertEquals(650.0, mt.recommendedPrice, "MT price must NOT be collapsed into NM")
        assertEquals(600.0, mt.minPrice)
        assertEquals(700.0, mt.avgPrice)
    }

    @Test
    fun `Given a condition map with only sparse metrics, When parsed, Then the condition row contains null for absent metrics`() {
        val file = writeFixture(
            """
            {
              "exportDate": "2026-04-22T21:40:21Z",
              "series": [{"sets":[{"cards":[{
                "tcgdexCardId":"lu-01","name":"Luxio",
                "variants":[{"version":"V1","productId":1,"label":null,
                  "prices":{"fr":{"LU":{
                    "currency":"EUR",
                    "minPrice":{"MT":750.0},
                    "avgPrice":{"MT":750.0},
                    "medianPrice":{"MT":750.0},
                    "maxPrice":{"MT":750.0},
                    "availableCount":{"MT":2.0},
                    "recommendedPrice":{"MT":750.0}
                  }}}
                }]
              }]}]}]
            }
            """.trimIndent(),
        )

        val parsed = parseCardmarketExportFile(file, json)
        assertNotNull(parsed)
        val byCondition = parsed.cards.getValue("lu-01").variants.first().prices.getValue("fr").getValue("LU")
        assertEquals(setOf("MT"), byCondition.keys)
        val mt = byCondition.getValue("MT")
        assertEquals(750.0, mt.recommendedPrice)
        // NM bucket is absent entirely in the JSON -> no NM row emitted at all.
        assertNull(byCondition["NM"])
    }

    @Test
    fun `Given a block where all five price metrics are null or missing, When parsed, Then the empty row is dropped`() {
        val file = writeFixture(
            """
            {
              "exportDate": "2026-04-22T21:40:21Z",
              "series": [{"sets":[{"cards":[{
                "tcgdexCardId":"empty-01","name":"Empty",
                "variants":[{"version":"V1","productId":1,"label":null,
                  "prices":{"fr":{"XX":{"currency":"EUR","availableCount":{"NM":0.0}}}}
                }]
              }]}]}]
            }
            """.trimIndent(),
        )

        val parsed = parseCardmarketExportFile(file, json)
        assertNotNull(parsed)
        // availableCount alone is not a price signal -- the whole card should be absent.
        assertTrue(parsed.cards.isEmpty(), "expected the empty variant to be dropped, got ${parsed.cards}")
    }

    private fun writeFixture(contents: String): File {
        val dir = createTempDirectory("cardmarket-condition-").toFile()
        val file = dir.resolve("cardmarket-prices-fr.json")
        file.writeText(contents)
        return file
    }
}
