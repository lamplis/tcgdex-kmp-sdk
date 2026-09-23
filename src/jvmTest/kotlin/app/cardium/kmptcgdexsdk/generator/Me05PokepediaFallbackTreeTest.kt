package app.cardium.kmptcgdexsdk.generator

import java.io.File
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

class Me05PokepediaFallbackTreeTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `Given me05 075-089 are no longer FR CDN gaps, When Pokepedia tree is loaded, Then those ids are absent`() {
        val projectRoot = resolveProjectRoot()
        val treeFile =
            projectRoot.resolve(
                "libs/tcgdex-kmp-sdk/generator-inputs/pokepedia/missing-fr-card-images-tree.json",
            )
        assertTrue(treeFile.isFile, "[x] Missing Pokepedia tree file: ${treeFile.absolutePath}")

        val loaded = loadPokepediaFallbacks(treeFile.absolutePath, json)
        val missingIds = (75..89).map { number -> "me05-%03d".format(number) }

        missingIds.forEach { cardId ->
            assertNull(loaded[cardId], "[x] Expected no Pokepedia fallback for $cardId")
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
