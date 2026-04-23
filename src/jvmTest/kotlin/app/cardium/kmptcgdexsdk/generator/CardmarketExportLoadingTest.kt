package app.cardium.kmptcgdexsdk.generator

import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.serialization.json.Json

class CardmarketExportLoadingTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `Given split Cardmarket file, When loaded, Then returns merged card prices`() {
        val root = createTempDirectory("cardmarket-root-").toFile()
        val exportDir = root.resolve("exports").apply { mkdirs() }
        exportDir.resolve("cardmarket-prices-fr.json").writeText(
            """
            {
              "exportDate":"2026-03-01T00:00:00Z",
              "series":[
                {
                  "sets":[
                    {
                      "cards":[
                        {
                          "tcgdexCardId":"sv01-001",
                          "name":"Bulbasaur",
                          "variants":[
                            {
                              "version":"Normal",
                              "productId":123,
                              "label":"V1",
                              "prices":{
                                "fr":{
                                  "FR":{
                                    "medianPrice":{"NM":2.5},
                                    "avgPrice":{"NM":2.8},
                                    "minPrice":{"NM":1.9},
                                    "maxPrice":{"NM":3.1},
                                    "recommendedPrice":{"NM":2.9},
                                    "availableCount":{"NM":4},
                                    "currency":"EUR"
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

        val loaded = loadCardmarketExportPrices(root, exportDir.absolutePath, json)
        assertNotNull(loaded)
        val card = loaded.cards["sv01-001"]
        assertNotNull(card)
        assertEquals(1, card.variants.size)
        val variant = card.variants.first()
        assertEquals(123, variant.productId)
        // The generator stores one CardmarketExportPrice per Cardmarket condition bucket
        // (here only "NM"); the database row is identified by (priceLanguage, sellerCountry, condition).
        val nmPrice = variant.prices["fr"]?.get("FR")?.get("NM")
        assertNotNull(nmPrice)
        assertEquals("NM", nmPrice.condition)
        assertEquals(2.9, nmPrice.recommendedPrice)
        assertEquals(2.8, nmPrice.avgPrice)
        assertEquals(1.9, nmPrice.minPrice)
    }

    @Test
    fun `Given empty export directory, When loaded, Then returns null`() {
        val root = createTempDirectory("cardmarket-empty-").toFile()
        val exportDir = root.resolve("empty").apply { mkdirs() }
        val loaded = loadCardmarketExportPrices(root, exportDir.absolutePath, json)
        assertNull(loaded)
    }

    @Test
    fun `Given no explicit path and canonical exists, When loaded, Then uses canonical directory`() {
        val root = createTempDirectory("cardmarket-default-").toFile()
        val canonical = root.resolve("libs/tcgdex-kmp-sdk/generator-inputs/cardmarket").apply { mkdirs() }
        canonical.resolve("cardmarket-prices-en.json").writeText(
            """
            {
              "exportDate":"2026-03-02T00:00:00Z",
              "series":[{"sets":[{"cards":[{"tcgdexCardId":"sv01-002","variants":[{"version":"Normal","prices":{"en":{"DE":{"recommendedPrice":{"NM":1.1},"currency":"EUR"}}}}]}]}]}]
            }
            """.trimIndent(),
        )

        val loaded = loadCardmarketExportPrices(root, null, json)
        assertNotNull(loaded)
        assertNotNull(loaded.cards["sv01-002"])
    }
}
