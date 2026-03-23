package app.cardium.kmptcgdexsdk.generator

import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GeneratorPathResolutionTest {
    @Test
    fun `Given canonical Pokepedia tree exists, When resolvePokepediaMissingTree called, Then returns canonical path`() {
        val root = createCardiumLikeRoot()
        val canonical = root.resolve("libs/tcgdex-kmp-sdk/generator-inputs/pokepedia/missing-fr-card-images-tree.json")
        canonical.parentFile.mkdirs()
        canonical.writeText("""{"series":[]}""")

        val datasetDir = root.resolve("libs/cards-database/server/generated").apply { mkdirs() }
        val resolved = resolvePokepediaMissingTree(datasetDir.absolutePath)

        assertNotNull(resolved)
        assertEquals(canonical.absolutePath, resolved.absolutePath)
    }

    @Test
    fun `Given only legacy Pokepedia tree exists, When resolvePokepediaMissingTree called, Then returns legacy path`() {
        val root = createCardiumLikeRoot()
        val legacy = root.resolve("libs/tcgdex-kmp-sdk/resources/pokepedia/missing-fr-card-images-tree.json")
        legacy.parentFile.mkdirs()
        legacy.writeText("""{"series":[]}""")

        val datasetDir = root.resolve("libs/cards-database/server/generated").apply { mkdirs() }
        val resolved = resolvePokepediaMissingTree(datasetDir.absolutePath)

        assertNotNull(resolved)
        assertEquals(legacy.absolutePath, resolved.absolutePath)
    }

    @Test
    fun `Given no Pokepedia tree exists, When resolvePokepediaMissingTree called, Then returns null`() {
        val root = createCardiumLikeRoot()
        val datasetDir = root.resolve("libs/cards-database/server/generated").apply { mkdirs() }
        val resolved = resolvePokepediaMissingTree(datasetDir.absolutePath)
        assertNull(resolved)
    }

    @Test
    fun `Given canonical Cardmarket directory exists, When resolveDefaultCardmarketExportPath called, Then returns canonical`() {
        val root = createCardiumLikeRoot()
        val canonical = root.resolve("libs/tcgdex-kmp-sdk/generator-inputs/cardmarket").apply { mkdirs() }
        val resolved = resolveDefaultCardmarketExportPath(root)
        assertNotNull(resolved)
        assertEquals(canonical.absolutePath, resolved.absolutePath)
    }

    @Test
    fun `Given only legacy Cardmarket directory exists, When resolveDefaultCardmarketExportPath called, Then returns legacy`() {
        val root = createCardiumLikeRoot()
        val legacy = root.resolve("exports/prices").apply { mkdirs() }
        val resolved = resolveDefaultCardmarketExportPath(root)
        assertNotNull(resolved)
        assertEquals(legacy.absolutePath, resolved.absolutePath)
    }

    @Test
    fun `Given canonical and legacy Cardmarket directories exist, When resolveDefaultCardmarketExportPath called, Then canonical wins`() {
        val root = createCardiumLikeRoot()
        val canonical = root.resolve("libs/tcgdex-kmp-sdk/generator-inputs/cardmarket").apply { mkdirs() }
        root.resolve("exports/prices").mkdirs()

        val resolved = resolveDefaultCardmarketExportPath(root)
        assertNotNull(resolved)
        assertEquals(canonical.absolutePath, resolved.absolutePath)
    }

    private fun createCardiumLikeRoot(): File {
        val root = createTempDirectory("cardium-root-").toFile()
        root.resolve("composeApp").mkdirs()
        root.resolve("tools").mkdirs()
        return root
    }
}
