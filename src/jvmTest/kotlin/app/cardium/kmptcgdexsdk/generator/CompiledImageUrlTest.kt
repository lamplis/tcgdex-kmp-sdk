package app.cardium.kmptcgdexsdk.generator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

class CompiledImageUrlTest {
    private val json = Json { ignoreUnknownKeys = true }
    private val compiled001 = "https://assets.tcgdex.net/fr/me/30th/001"
    private val compiledR = "https://assets.tcgdex.net/fr/me/30th/R"

    private val manifest =
        json.parseToJsonElement(
            """
            {
              "fr": {
                "me": {
                  "30th": {
                    "001": {}
                  }
                }
              }
            }
            """.trimIndent(),
        ).jsonObject

    @Test
    fun `Given 30th R missing from manifest When compiled URL present Then image is dropped`() {
        assertNull(
            compiledImageUrlForCard(
                compiledImage = compiledR,
                assetsManifest = manifest,
                language = "fr",
                serieId = "me",
                setId = "30th",
                localId = "R",
            ),
        )
    }

    @Test
    fun `Given 30th 001 absent from manifest When compiled URL present Then numbered CDN image is kept`() {
        val empty =
            json.parseToJsonElement("""{ "fr": { "me": {} } }""").jsonObject
        assertEquals(
            compiled001,
            compiledImageUrlForCard(
                compiledImage = compiled001,
                assetsManifest = empty,
                language = "fr",
                serieId = "me",
                setId = "30th",
                localId = "001",
            ),
        )
    }

    @Test
    fun `Given 30th 001 listed in manifest When compiled URL present Then image is kept`() {
        assertEquals(
            compiled001,
            compiledImageUrlForCard(
                compiledImage = compiled001,
                assetsManifest = manifest,
                language = "fr",
                serieId = "me",
                setId = "30th",
                localId = "001",
            ),
        )
    }

    @Test
    fun `Given another set When compiled URL is absent from manifest Then image is kept`() {
        val compiled = "https://assets.tcgdex.net/fr/sv/sv01/001"
        assertEquals(
            compiled,
            compiledImageUrlForCard(
                compiledImage = compiled,
                assetsManifest = manifest,
                language = "fr",
                serieId = "sv",
                setId = "sv01",
                localId = "001",
            ),
        )
    }
}
