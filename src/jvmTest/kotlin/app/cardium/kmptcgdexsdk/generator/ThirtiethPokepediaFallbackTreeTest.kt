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
    fun `Given Classic Collection reprints, When Pokepedia tree is loaded, Then 30C uses wiki HD scans`() {
        val projectRoot = resolveProjectRoot()
        val treeFile =
            projectRoot.resolve(
                "libs/tcgdex-kmp-sdk/generator-inputs/pokepedia/missing-fr-card-images-tree.json",
            )
        assertTrue(treeFile.isFile, "[x] Missing Pokepedia tree file: ${treeFile.absolutePath}")

        val loaded = loadPokepediaFallbacks(treeFile.absolutePath, json)

        val classic002 = loaded["30C-CC002"]
        assertNotNull(classic002, "[x] Expected Pokepedia fallback for 30C-CC002")
        assertEquals("pokepedia", classic002.source, "[x] Unexpected fallback source for 30C-CC002")
        assertTrue(
            classic002.url.contains("Carte_Set_de_Base_4.png"),
            "[x] Expected Set de Base HD scan for 30C-CC002, got ${classic002.url}",
        )

        val classicPokepediaCount =
            loaded.count { (cardId, fallback) ->
                cardId.startsWith("30C-CC") && fallback.source == "pokepedia"
            }
        assertTrue(
            classicPokepediaCount >= 26,
            "[x] Expected at least 26 30C-CC Pokepedia HD backups, got $classicPokepediaCount",
        )

        val rgbScans =
            mapOf(
                "30C-R" to "189.jpg",
                "30C-G" to "190.jpg",
                "30C-B" to "191.jpg",
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
