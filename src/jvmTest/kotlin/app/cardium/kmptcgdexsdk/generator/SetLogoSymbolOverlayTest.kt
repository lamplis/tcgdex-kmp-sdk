package app.cardium.kmptcgdexsdk.generator

import java.io.File
import kotlin.io.path.createTempFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

class SetLogoSymbolOverlayTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `Given compiled CDN url When resolving overlay Then compiled url wins`() {
        val compiled = "https://assets.tcgdex.net/en/me/me03/logo"
        val overlay = "https://www.pokepedia.fr/images/c/c6/Logo.png"

        val resolved = resolveSetAssetUrl(compiled, overlay)

        assertEquals(compiled, resolved)
    }

    @Test
    fun `Given blank compiled url When overlay is Pokepedia Then overlay url is used`() {
        val overlay = "https://www.pokepedia.fr/images/0/0c/Logo_30e_Anniversaire_JCC.png"

        val resolved = resolveSetAssetUrl("  ", overlay)

        assertEquals(overlay, resolved)
    }

    @Test
    fun `Given blank compiled url When overlay is Pokecardex Then url is rejected`() {
        val overlay = "https://pokecardex.b-cdn.net/assets/images/symboles/30C.png"

        val resolved = resolveSetAssetUrl(null, overlay)

        assertNull(resolved)
        assertFalse(isAllowedSetAssetUrl(overlay))
    }

    @Test
    fun `Given overlay JSON When loaded Then 30th logo is Pokepedia`() {
        val file: File = createTempFile("set-logos-", ".json").toFile()
        file.writeText(
            """
            {
              "30th": {
                "wikiPageSlug": "30e_Anniversaire",
                "logoUrl": "https://www.pokepedia.fr/images/0/0c/Logo_30e_Anniversaire_JCC.png",
                "symbolUrl": "https://pokecardex.b-cdn.net/assets/images/symboles/30C.png"
              }
            }
            """.trimIndent(),
        )

        val loaded = loadSetLogoSymbolOverlay(file.absolutePath, json)
        val entry = loaded["30th"]

        assertEquals(
            "https://www.pokepedia.fr/images/0/0c/Logo_30e_Anniversaire_JCC.png",
            entry?.logoUrl,
        )
        assertTrue(isAllowedSetAssetUrl(entry?.logoUrl))
        assertNull(resolveSetAssetUrl(null, entry?.symbolUrl))
    }

    @Test
    fun `Given missing overlay file When loaded Then empty map is returned`() {
        val loaded = loadSetLogoSymbolOverlay("/tmp/does-not-exist-set-logos.json", json)

        assertEquals(emptyMap(), loaded)
    }
}
