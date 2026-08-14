package app.cardium.kmptcgdexsdk.generator

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

class Me05PokepediaFallbackTreeTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `Given FR CDN gaps for me05 075-089, When Pokepedia tree is loaded, Then those cards resolve to Nuit Noire scans`() {
        val projectRoot = resolveProjectRoot()
        val treeFile =
            projectRoot.resolve(
                "libs/tcgdex-kmp-sdk/generator-inputs/pokepedia/missing-fr-card-images-tree.json",
            )
        assertTrue(treeFile.isFile, "[x] Missing Pokepedia tree file: ${treeFile.absolutePath}")

        val loaded = loadPokepediaFallbacks(treeFile.absolutePath, json)
        val missingIds = (75..89).map { number -> "me05-%03d".format(number) }

        missingIds.forEach { cardId ->
            val fallback = loaded[cardId]
            assertNotNull(fallback, "[x] Expected Pokepedia fallback for $cardId")
            assertEquals("pokepedia", fallback.source, "[x] Unexpected fallback source for $cardId")
            assertTrue(
                fallback.url.contains("Nuit_Noire_", ignoreCase = false) &&
                    fallback.url.contains(cardId.substringAfter('-')),
                "[x] Expected Nuit Noire HD scan for $cardId, got ${fallback.url}",
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
