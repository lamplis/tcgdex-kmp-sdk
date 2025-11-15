package tools

import kotlin.test.Test
import kotlin.test.assertTrue
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ManifestValidationTest {
    @Test
    fun manifestIsPresentAndParsable() {
        // Try common cache locations (module-level and project root)
        val candidates: List<Path> =
            listOf(
                Paths.get(System.getProperty("user.dir")).resolve("build/cache/datas.json"),
                Paths.get(System.getProperty("user.dir")).resolve("libs/tcgdex-kmp-sdk/build/cache/datas.json"),
            )
        val cachePath: Path? = candidates.firstOrNull { Files.isRegularFile(it) }

        assertTrue(
            cachePath != null,
            "Image manifest cache not found in expected locations. Run :libs:tcgdex-kmp-sdk:generateEmbeddedCatalog first.",
        )

        val raw = Files.readString(cachePath!!)
        assertTrue(raw.isNotBlank(), "Image manifest file is empty: $cachePath")

        val manifest = parseManifest(raw)
        assertTrue(manifest.isNotEmpty(), "Parsed image manifest is empty or corrupted")

        // Basic structural checks: at least 'en' exists and contains some nested data
        val hasEn = manifest.containsKey("en")
        assertTrue(hasEn, "Manifest missing expected top-level 'en' language key")

        val enMap = manifest["en"]!!
        assertTrue(enMap.isNotEmpty(), "Manifest 'en' branch is empty")

        val anySetMap = enMap.values.firstOrNull()
        assertTrue(anySetMap != null && anySetMap!!.isNotEmpty(), "Manifest 'en' has no sets under any series")

        val anyCards = anySetMap!!.values.firstOrNull()
        assertTrue(anyCards != null && anyCards!!.isNotEmpty(), "Manifest 'en' first set has no card entries")
    }
}
