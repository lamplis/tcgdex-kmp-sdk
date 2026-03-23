package app.cardium.kmptcgdexsdk.generator

import kotlin.io.path.createTempFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class UnresolvedTreeRegenerationTest {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    @Test
    fun `Given mixed resolved and unresolved cards, When regenerated, Then only unresolved remain`() {
        val tree = createTempFile("pokepedia-tree-", ".json").toFile()
        tree.writeText(
            """
            {
              "generatedAt":"2026-03-01T00:00:00Z",
              "series":[
                {
                  "seriesId":"sv",
                  "sets":[
                    {
                      "setId":"sv01",
                      "cards":[
                        {"cardId":"sv01-001","resolutionStatus":"resolved","pokepediaHdUrl":"https://wiki/1.png"},
                        {"cardId":"sv01-002","resolutionStatus":"unresolved","reason":"POKEPEDIA_CARD_NOT_FOUND"},
                        {"cardId":"sv01-003","resolutionStatus":"unresolved","reason":"POKEPEDIA_HD_UNAVAILABLE"}
                      ]
                    }
                  ]
                }
              ]
            }
            """.trimIndent(),
        )

        regenerateUnresolvedFile(tree, json)

        val unresolvedFile = tree.resolveSibling("missing-fr-card-images-unresolved.json")
        val root = json.parseToJsonElement(unresolvedFile.readText()).jsonObject
        assertEquals(2, root["totalCards"]?.jsonPrimitive?.content?.toInt())
        assertEquals(2, root["unresolvedCards"]?.jsonPrimitive?.content?.toInt())
        assertEquals(0, root["resolvedCards"]?.jsonPrimitive?.content?.toInt())

        val cards = root["series"]!!.jsonArray
            .first().jsonObject["sets"]!!.jsonArray
            .first().jsonObject["cards"]!!.jsonArray
        assertEquals(2, cards.size)
    }
}
