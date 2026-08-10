package app.cardium.kmptcgdexsdk.generator

import kotlin.io.path.createTempFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertNotNull
import kotlinx.serialization.json.Json

class PokepediaFallbackImportTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `Given resolved card with hd url, When loaded, Then pokepedia hd is preferred`() {
        val tree = createTempFile("pokepedia-tree-", ".json").toFile()
        tree.writeText(
            """
            {
              "series":[
                {
                  "sets":[
                    {
                      "cards":[
                        {
                          "cardId":"sv01-001",
                          "resolutionStatus":"resolved",
                          "pokepediaHdUrl":"https://wiki.example/hd.png",
                          "pokepediaThumbnailUrl":"https://wiki.example/thumb.png",
                          "cardmarketImageUrl":"https://product-images.s3.cardmarket.com/51/ABC/123/123.jpg"
                        }
                      ]
                    }
                  ]
                }
              ]
            }
            """.trimIndent(),
        )

        val loaded = loadPokepediaFallbacks(tree.absolutePath, json)
        val fallback = loaded["sv01-001"]
        assertNotNull(fallback)
        assertEquals("https://wiki.example/hd.png", fallback.url)
        assertEquals("pokepedia", fallback.source)
    }

    @Test
    fun `Given unresolved promo mismatch with cardmarket url, When loaded, Then cardmarket fallback is used`() {
        val tree = createTempFile("pokepedia-tree-", ".json").toFile()
        tree.writeText(
            """
            {
              "series":[
                {
                  "sets":[
                    {
                      "cards":[
                        {
                          "cardId":"sv01-002",
                          "resolutionStatus":"unresolved",
                          "reason":"PROMO_IMAGE_MISMATCH",
                          "pokepediaThumbnailUrl":"https://www.pokepedia.fr/images/8/80/Porygon-Z-DP.png",
                          "cardmarketImageUrl":"https://product-images.s3.cardmarket.com/51/MEP/894884/894884.jpg"
                        }
                      ]
                    }
                  ]
                }
              ]
            }
            """.trimIndent(),
        )

        val loaded = loadPokepediaFallbacks(tree.absolutePath, json)
        val fallback = loaded["sv01-002"]
        assertNotNull(fallback)
        assertEquals("https://product-images.s3.cardmarket.com/51/MEP/894884/894884.jpg", fallback.url)
        assertEquals("cardmarket", fallback.source)
    }

    @Test
    fun `Given unresolved card without cardmarket url, When loaded, Then no fallback is returned`() {
        val tree = createTempFile("pokepedia-tree-", ".json").toFile()
        tree.writeText(
            """
            {
              "series":[
                {
                  "sets":[
                    {
                      "cards":[
                        {
                          "cardId":"sv01-003",
                          "resolutionStatus":"unresolved",
                          "reason":"POKEPEDIA_CARD_NOT_FOUND",
                          "pokepediaThumbnailUrl":"https://wiki.example/untrusted-thumb.png"
                        },
                        {
                          "cardId":"sv01-004",
                          "resolutionStatus":"unresolved",
                          "reason":"POKEPEDIA_THUMBNAIL_MISSING",
                          "cardmarketImageUrl":"https://product-images.s3.cardmarket.com/51/MEP/899999/899999.jpg"
                        }
                      ]
                    }
                  ]
                }
              ]
            }
            """.trimIndent(),
        )

        val loaded = loadPokepediaFallbacks(tree.absolutePath, json)
        assertNull(loaded["sv01-003"])
        assertNull(loaded["sv01-004"])
    }
}
