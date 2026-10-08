package app.cardium.kmptcgdexsdk.generator

import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
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
        assertNull(nmPrice.capturedAt)
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

    @Test
    fun `Given Hidden Fates vault Cardmarket ids, When loaded, Then they map onto sm115sv unpadded ids`() {
        val root = createTempDirectory("cardmarket-hifsv-").toFile()
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
                          "tcgdexCardId":"sm115-SV001",
                          "name":"Scyther",
                          "variants":[
                            {
                              "version":"Normal",
                              "productId":251401,
                              "prices":{"fr":{"FR":{"recommendedPrice":{"NM":4.5},"currency":"EUR"}}}
                            }
                          ]
                        },
                        {
                          "tcgdexCardId":"sm115-004",
                          "name":"Paras",
                          "variants":[
                            {
                              "version":"Normal",
                              "productId":251404,
                              "prices":{"fr":{"FR":{"recommendedPrice":{"NM":0.2},"currency":"EUR"}}}
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
        assertNotNull(loaded.cards["sm115sv-SV1"])
        assertEquals("sm115sv-SV1", loaded.cards.getValue("sm115sv-SV1").tcgdexCardId)
        assertNotNull(loaded.cards["sm115-004"])
        assertNull(loaded.cards["sm115-SV001"])
    }

    @Test
    fun `Given variant lifecycle captured_at, When parsed, Then each price carries that capturedAt`() {
        val file = writeExportFile(
            """
            {
              "exportDate": "2026-08-13T18:22:31Z",
              "series": [{"sets":[{"cards":[{
                "tcgdexCardId":"sv01-001",
                "name":"Bulbasaur",
                "variants":[{
                  "version":"Normal",
                  "productId":123,
                  "label":"V1",
                  "prices":{"fr":{"FR":{
                    "recommendedPrice":{"NM":2.9},
                    "avgPrice":{"NM":2.8},
                    "minPrice":{"NM":1.9},
                    "currency":"EUR"
                  }}},
                  "lifecycle":{"fr":{
                    "captured_at":"2026-05-09T01:32:59Z",
                    "extracted_at":"2026-05-09T01:32:59Z"
                  }}
                }]
              }]}]}]
            }
            """.trimIndent(),
        )

        val parsed = parseCardmarketExportFile(file, json)
        assertNotNull(parsed)
        val nmPrice = parsed.cards.getValue("sv01-001").variants.first().prices["fr"]?.get("FR")?.get("NM")
        assertNotNull(nmPrice)
        assertEquals("2026-05-09T01:32:59Z", nmPrice.capturedAt)
        assertEquals(
            "2026-05-09T01:32:59Z",
            resolveExportPriceUpdatedIso(nmPrice.capturedAt, parsed.updatedIso),
        )
        assertEquals("2026-08-13T18:22:31Z", parsed.updatedIso)
    }

    @Test
    fun `Given no lifecycle, When parsed, Then capturedAt is null and fallback is exportDate`() {
        val file = writeExportFile(
            """
            {
              "exportDate": "2026-03-01T00:00:00Z",
              "series": [{"sets":[{"cards":[{
                "tcgdexCardId":"sv01-001",
                "variants":[{"version":"Normal","prices":{"fr":{"FR":{
                  "recommendedPrice":{"NM":2.9},
                  "currency":"EUR"
                }}}}]
              }]}]}]
            }
            """.trimIndent(),
        )

        val parsed = parseCardmarketExportFile(file, json)
        assertNotNull(parsed)
        val nmPrice = parsed.cards.getValue("sv01-001").variants.first().prices["fr"]?.get("FR")?.get("NM")
        assertNotNull(nmPrice)
        assertNull(nmPrice.capturedAt)
        assertEquals(
            "2026-03-01T00:00:00Z",
            resolveExportPriceUpdatedIso(nmPrice.capturedAt, parsed.updatedIso),
        )
    }

    @Test
    fun `Given fr and en split files with different captured_at, When loaded, Then each language keeps its own date`() {
        val root = createTempDirectory("cardmarket-lifecycle-merge-").toFile()
        val exportDir = root.resolve("exports").apply { mkdirs() }
        exportDir.resolve("cardmarket-prices-fr.json").writeText(
            """
            {
              "exportDate":"2026-08-13T18:22:31Z",
              "series":[{"sets":[{"cards":[{
                "tcgdexCardId":"sv01-001",
                "variants":[{
                  "version":"Normal",
                  "productId":123,
                  "prices":{"fr":{"FR":{"recommendedPrice":{"NM":2.9},"currency":"EUR"}}},
                  "lifecycle":{"fr":{"captured_at":"2026-05-09T01:32:59Z"}}
                }]
              }]}]}]
            }
            """.trimIndent(),
        )
        exportDir.resolve("cardmarket-prices-en.json").writeText(
            """
            {
              "exportDate":"2026-08-13T18:22:33Z",
              "series":[{"sets":[{"cards":[{
                "tcgdexCardId":"sv01-001",
                "variants":[{
                  "version":"Normal",
                  "productId":123,
                  "prices":{"en":{"DE":{"recommendedPrice":{"NM":1.1},"currency":"EUR"}}},
                  "lifecycle":{"en":{"captured_at":"2026-06-01T12:00:00Z"}}
                }]
              }]}]}]
            }
            """.trimIndent(),
        )

        val loaded = loadCardmarketExportPrices(root, exportDir.absolutePath, json)
        assertNotNull(loaded)
        val variant = loaded.cards.getValue("sv01-001").variants.single()
        assertEquals(
            "2026-05-09T01:32:59Z",
            variant.prices["fr"]?.get("FR")?.get("NM")?.capturedAt,
        )
        assertEquals(
            "2026-06-01T12:00:00Z",
            variant.prices["en"]?.get("DE")?.get("NM")?.capturedAt,
        )
    }

    @Test
    fun `Given productId and empty prices, When parsed, Then the card is kept and prices are empty`() {
        val file = writeExportFile(
            """
            {
              "exportDate": "2026-10-06T00:00:00Z",
              "series": [{"sets":[{"cards":[{
                "tcgdexCardId":"pps1-BST22",
                "variants":[{"version":"V1","productId":697079,"prices":{}}]
              }]}]}]
            }
            """.trimIndent(),
        )

        val parsed = parseCardmarketExportFile(file, json)
        assertNotNull(parsed)
        val variant = parsed.cards.getValue("pps1-BST22").variants.single()
        assertEquals(697079, variant.productId)
        assertEquals("V1", variant.version)
        assertTrue(variant.prices.isEmpty())
    }

    @Test
    fun `Given no productId and empty prices, When parsed, Then the card is absent`() {
        val file = writeExportFile(
            """
            {
              "exportDate": "2026-10-06T00:00:00Z",
              "series": [{"sets":[{"cards":[{
                "tcgdexCardId":"sv01-001",
                "variants":[{"version":"V1","prices":{}}]
              }]}]}]
            }
            """.trimIndent(),
        )

        val parsed = parseCardmarketExportFile(file, json)
        assertNotNull(parsed)
        assertNull(parsed.cards["sv01-001"])
    }

    @Test
    fun `Given productId and an NM price, When parsed, Then the priced variant is unchanged`() {
        val file = writeExportFile(
            """
            {
              "exportDate": "2026-10-06T00:00:00Z",
              "series": [{"sets":[{"cards":[{
                "tcgdexCardId":"sv01-001",
                "variants":[{
                  "version":"Normal",
                  "productId":123,
                  "prices":{"fr":{"FR":{
                    "recommendedPrice":{"NM":2.9},
                    "currency":"EUR"
                  }}}
                }]
              }]}]}]
            }
            """.trimIndent(),
        )

        val parsed = parseCardmarketExportFile(file, json)
        assertNotNull(parsed)
        val variant = parsed.cards.getValue("sv01-001").variants.single()
        assertEquals(123, variant.productId)
        assertEquals(2.9, variant.prices["fr"]?.get("FR")?.get("NM")?.recommendedPrice)
    }

    @Test
    fun `Given an empty-price variant whose productId matches the price guide, When anchors are built, Then the list is empty`() {
        val anchors = catalogProductAnchors(
            variants = listOf(
                CardmarketExportVariant(
                    version = "V1",
                    productId = 697079,
                    label = null,
                    prices = emptyMap(),
                ),
            ),
            priceGuideProductId = 697079,
        )
        assertEquals(emptyList(), anchors)
    }

    @Test
    fun `Given an empty-price variant and a null price guide id, When anchors are built, Then the anchor is V1 with that productId`() {
        val anchors = catalogProductAnchors(
            variants = listOf(
                CardmarketExportVariant(
                    version = "V1",
                    productId = 697145,
                    label = null,
                    prices = emptyMap(),
                ),
            ),
            priceGuideProductId = null,
        )
        assertEquals(
            listOf(CatalogProductAnchor(variantKey = "V1", productId = 697145)),
            anchors,
        )
    }

    @Test
    fun `Given variant Normal and a different price-guide id, When anchors are built, Then the Normal slot is not reused`() {
        val anchors = catalogProductAnchors(
            variants = listOf(
                CardmarketExportVariant(
                    version = "Normal",
                    productId = 111,
                    label = null,
                    prices = emptyMap(),
                ),
            ),
            priceGuideProductId = 222,
        )
        assertEquals(emptyList(), anchors)
    }

    private fun writeExportFile(contents: String): File {
        val dir = createTempDirectory("cardmarket-lifecycle-").toFile()
        val file = dir.resolve("cardmarket-prices-fr.json")
        file.writeText(contents)
        return file
    }
}
