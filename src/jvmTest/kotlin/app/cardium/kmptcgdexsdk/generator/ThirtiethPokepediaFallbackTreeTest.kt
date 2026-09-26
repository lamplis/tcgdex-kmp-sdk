package app.cardium.kmptcgdexsdk.generator

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

class ThirtiethPokepediaFallbackTreeTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `Given Classic Collection reprints, When Pokepedia tree is loaded, Then 30th-c uses wiki HD scans`() {
        val projectRoot = resolveProjectRoot()
        val treeFile =
            projectRoot.resolve(
                "libs/tcgdex-kmp-sdk/generator-inputs/pokepedia/missing-fr-card-images-tree.json",
            )
        assertTrue(treeFile.isFile, "[x] Missing Pokepedia tree file: ${treeFile.absolutePath}")

        val loaded = loadPokepediaFallbacks(treeFile.absolutePath, json)

        val classic001 = loaded["30th-c-001"]
        assertNotNull(classic001, "[x] Expected Pokepedia fallback for 30th-c-001")
        assertEquals("pokepedia", classic001.source, "[x] Unexpected fallback source for 30th-c-001")
        assertTrue(
            classic001.url.contains("Carte_Set_de_Base_4.png"),
            "[x] Expected Set de Base HD scan for 30th-c-001, got ${classic001.url}",
        )

        val classicPokepediaCount =
            loaded.count { (cardId, fallback) ->
                cardId.startsWith("30th-c-") && fallback.source == "pokepedia"
            }
        assertTrue(
            classicPokepediaCount >= 26,
            "[x] Expected at least 26 30th-c Pokepedia HD backups, got $classicPokepediaCount",
        )

        val rgbScans =
            mapOf(
                "30th-R" to "189.jpg",
                "30th-G" to "190.jpg",
                "30th-B" to "191.jpg",
            )
        for ((cardId, scan) in rgbScans) {
            val fallback = loaded[cardId]
            assertNotNull(fallback, "[x] Expected Pokecardex fallback for $cardId")
            assertEquals("pokecardex", fallback.source, "[x] Unexpected fallback source for $cardId")
            assertTrue(
                fallback.url.contains(scan),
                "[x] Expected Pokecardex scan $scan for $cardId, got ${fallback.url}",
            )
        }
    }

    private fun resolveProjectRoot(): File {
        var cursor: File? = File(System.getProperty("user.dir")).absoluteFile
        repeat(8) {
            val candidate = cursor ?: return@repeat
            val hasSettings = candidate.resolve("settings.gradle.kts").isFile
            val hasSdkModule = candidate.resolve("libs/tcgdex-kmp-sdk").isDirectory
            if (hasSettings && hasSdkModule) {
                return candidate
            }
            cursor = candidate.parentFile
        }
        error("[x] Could not resolve project root from ${System.getProperty("user.dir")}.")
    }
}
