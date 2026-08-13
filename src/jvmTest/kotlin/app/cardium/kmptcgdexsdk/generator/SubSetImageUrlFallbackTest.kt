package app.cardium.kmptcgdexsdk.generator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class SubSetImageUrlFallbackTest {
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Minimal manifest fixture mirroring the real datas.json shape:
     * manifest[language][serieId][setId][localId] -> quality map.
     * Only parent set folders exist on the CDN (never the sub-set ids).
     */
    private val manifest = json.parseToJsonElement(
        """
        {
          "fr": {
            "swsh": {
              "swsh9": {"TG01": {"high": true}},
              "swsh10": {"TG28": {"high": true}},
              "swsh11": {"TG02": {"high": true}},
              "swsh12": {"TG03": {"high": true}},
              "swsh12.5": {"GG01": {"high": true}},
              "swsh4.5": {"SV001": {"high": true}},
              "cel25": {"8A": {"high": true}}
            },
            "sm": {}
          },
          "en": {
            "sm": {"sma": {"SV1": {"high": true}}}
          }
        }
        """.trimIndent(),
    ).jsonObject

    private val subSetParents: Map<String, Pair<String, String>> = mapOf(
        "swsh9tg" to ("swsh" to "swsh9"),
        "swsh10tg" to ("swsh" to "swsh10"),
        "swsh11tg" to ("swsh" to "swsh11"),
        "swsh12tg" to ("swsh" to "swsh12"),
        "swsh12.5gg" to ("swsh" to "swsh12.5"),
        "swsh4.5sv" to ("swsh" to "swsh4.5"),
        "cel25cc" to ("swsh" to "cel25"),
        "sm115sv" to ("sm" to "sm115"),
    )

    @Test
    fun `Given swsh10tg card in manifest, When synthesizing, Then parent-folder URL is returned`() {
        assertEquals(
            "https://assets.tcgdex.net/fr/swsh/swsh10/TG28",
            synthesize(language = "fr", setId = "swsh10tg", localId = "TG28"),
        )
    }

    @Test
    fun `Given every mapped sub-set, When synthesizing, Then URL uses parent folder without quality suffix`() {
        assertEquals(
            "https://assets.tcgdex.net/fr/swsh/swsh9/TG01",
            synthesize("fr", "swsh9tg", "TG01"),
        )
        assertEquals(
            "https://assets.tcgdex.net/fr/swsh/swsh11/TG02",
            synthesize("fr", "swsh11tg", "TG02"),
        )
        assertEquals(
            "https://assets.tcgdex.net/fr/swsh/swsh12/TG03",
            synthesize("fr", "swsh12tg", "TG03"),
        )
        assertEquals(
            "https://assets.tcgdex.net/fr/swsh/swsh12.5/GG01",
            synthesize("fr", "swsh12.5gg", "GG01"),
        )
        assertEquals(
            "https://assets.tcgdex.net/fr/swsh/swsh4.5/SV001",
            synthesize("fr", "swsh4.5sv", "SV001"),
        )
    }

    @Test
    fun `Given mapped set but localId missing from manifest, When synthesizing, Then null`() {
        assertNull(synthesize("fr", "swsh10tg", "TG99"))
    }

    @Test
    fun `Given mapped set but language missing from manifest, When synthesizing, Then null`() {
        assertNull(synthesize("de", "swsh10tg", "TG28"))
    }

    @Test
    fun `Given unmapped sets, When synthesizing, Then null even when manifest has entries`() {
        // sma is rewritten to sm115sv before derivation; the EN CDN folder stays sma
        // and is never used as a parent-folder remap target.
        assertNull(synthesize("en", "sma", "SV1"))
        // sm115sv is a derived sub-set, but SV1 is not in the sm115 parent folder.
        assertNull(synthesize("en", "sm115sv", "SV1"))
        assertNull(synthesize("fr", "sm115sv", "SV1"))
        // cel25cc is a derived sub-set, but CC001 is not in the parent folder manifest.
        assertNull(synthesize("fr", "cel25cc", "CC001"))
        // Regular parent sets must never be remapped.
        assertNull(synthesize("fr", "swsh10", "TG28"))
    }

    @Test
    fun `Given empty manifest, When synthesizing, Then no URL is emitted without manifest confirmation`() {
        val emptyManifest = json.parseToJsonElement("""{}""").jsonObject
        assertNull(synthesize("fr", "swsh10tg", "TG28", manifestOverride = emptyManifest))
    }

    @Test
    fun `Given empty parent map, When synthesizing, Then sma and mapped sets stay null`() {
        assertNull(synthesize("fr", "swsh10tg", "TG28", parents = emptyMap()))
        assertNull(synthesize("en", "sma", "SV1", parents = emptyMap()))
    }

    private fun synthesize(
        language: String,
        setId: String,
        localId: String,
        manifestOverride: JsonObject = manifest,
        parents: Map<String, Pair<String, String>> = subSetParents,
    ): String? = synthesizeSubSetImageUrl(
        assetsManifest = manifestOverride,
        subSetParents = parents,
        language = language,
        setId = setId,
        localId = localId,
    )
}
