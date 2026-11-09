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
        json
            .parseToJsonElement(
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
    fun `Given a PokeBeach backup and a CDN scan When choosing the image Then the CDN scan wins`() {
        val manifestWithMe06 =
            json
                .parseToJsonElement(
                    """
                    {
                      "fr": {
                        "me": {
                          "me06": {
                            "001": {}
                          }
                        }
                      }
                    }
                    """.trimIndent(),
                ).jsonObject
        assertEquals(
            "https://assets.tcgdex.net/fr/me/me06/001",
            cdnImageUrlOverPokebeachBackup(
                compiledImageUrl = "https://www.pokebeach.com/news/2026/07/050339_P_HERAKUROSU_tiGp.jpg",
                assetsManifest = manifestWithMe06,
                language = "fr",
                serieId = "me",
                setId = "me06",
                localId = "001",
            ),
        )
    }

    @Test
    fun `Given a PokeBeach backup and a Cardmarket fallback When storing the image Then Cardmarket is shown`() {
        val cardmarket =
            FallbackImage(
                url = "https://product-images.s3.cardmarket.com/51/DLR/910000/910000.jpg",
                source = "cardmarket",
            )
        val (imageUrl, fallback) =
            resolveStoredCardImage(
                compiledImageUrl = "https://www.pokebeach.com/news/2026/07/050339_P_HERAKUROSU_tiGp.jpg",
                language = "fr",
                setId = "me06",
                treeFallback = cardmarket,
                explicitBackupUrl = null,
            )
        assertNull(imageUrl)
        assertEquals(cardmarket, fallback)
    }

    @Test
    fun `Given only a PokeBeach backup When storing the image Then the backup stays available`() {
        val (imageUrl, fallback) =
            resolveStoredCardImage(
                compiledImageUrl = "https://www.pokebeach.com/news/2026/07/050339_P_HERAKUROSU_tiGp.jpg",
                language = "en",
                setId = "me06",
                treeFallback = null,
                explicitBackupUrl = null,
            )
        assertNull(imageUrl)
        assertEquals("pokebeach", fallback?.source)
        assertEquals(
            "https://www.pokebeach.com/news/2026/07/050339_P_HERAKUROSU_tiGp.jpg",
            fallback?.url,
        )
    }

    @Test
    fun `Given a TCGdex CDN image When storing the image Then the CDN image stays primary`() {
        val cardmarket =
            FallbackImage(
                url = "https://product-images.s3.cardmarket.com/51/DLR/910000/910000.jpg",
                source = "cardmarket",
            )
        val cdn = "https://assets.tcgdex.net/fr/me/me06/001"
        val (imageUrl, fallback) =
            resolveStoredCardImage(
                compiledImageUrl = cdn,
                language = "fr",
                setId = "me06",
                treeFallback = cardmarket,
                explicitBackupUrl = null,
            )
        assertEquals(cdn, imageUrl)
        assertEquals(cardmarket, fallback)
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
