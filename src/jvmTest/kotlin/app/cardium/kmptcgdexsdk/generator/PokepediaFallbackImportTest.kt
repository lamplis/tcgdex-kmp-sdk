package app.cardium.kmptcgdexsdk.generator

import kotlin.io.path.createTempFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.serialization.json.Json

class PokepediaFallbackImportTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `Given hd and thumbnail urls, When loaded, Then hd is preferred`() {
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
                          "pokepediaThumbnailUrl":"https://wiki.example/thumb.png"
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
        assertEquals("https://wiki.example/hd.png", loaded["sv01-001"])
    }

    @Test
    fun `Given thumbnail-only url, When loaded, Then thumbnail is used`() {
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
                          "resolutionStatus":"resolved",
                          "pokepediaThumbnailUrl":"https://wiki.example/thumb-only.png"
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
        assertEquals("https://wiki.example/thumb-only.png", loaded["sv01-002"])
    }

    @Test
    fun `Given unresolved and thumbnail-missing reasons, When loaded, Then they are skipped`() {
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
                          "reason":"POKEPEDIA_THUMBNAIL_MISSING"
                        },
                        {
                          "cardId":"sv01-004",
                          "resolutionStatus":"unresolved",
                          "reason":"POKEPEDIA_CARD_NOT_FOUND"
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
