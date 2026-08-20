package app.cardium.kmptcgdexsdk.generator

import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CameoDexIdValidationTest {
    @Test
    fun `Given matching source and compiled cameo cards When asserting Then check succeeds`() {
        val root = createCameoFixture(sourceCameo = true, compiledCameo = true)
        try {
            val datasetDir = root.resolve("server/generated")
            assertCameoJsonMatchesSource(
                datasetDir = datasetDir,
                languages = listOf("en"),
            )
            assertEquals(1, countSourceCameoCards(cardsDatabaseDataDir(datasetDir)))
            assertEquals(1, countCompiledCameoCards(datasetDir.resolve("en/cards.json")))
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun `Given source cameo and compiled json without cameoDexIds When asserting Then check throws`() {
        val root = createCameoFixture(sourceCameo = true, compiledCameo = false)
        try {
            val error =
                assertFailsWith<IllegalStateException> {
                    assertCameoJsonMatchesSource(
                        datasetDir = root.resolve("server/generated"),
                        languages = listOf("en"),
                    )
                }
            assertTrue(error.message.orEmpty().contains("CAMEO DEX IDS DROPPED DURING COMPILE"))
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun `Given english matching source and fewer french compiled cameos When asserting Then check succeeds`() {
        val root = createCameoFixture(sourceCameo = true, compiledCameo = true)
        try {
            val generatedFr = root.resolve("server/generated/fr")
            generatedFr.mkdirs()
            generatedFr.resolve("cards.json").writeText(
                """[{"id":"base1-1","dexId":[1],"cameoDexIds":[25]}]""",
            )
            root.resolve("data/card2.ts").writeText(
                """
                export default {
                  id: "base1-2",
                  dexId: [4],
                  cameoDexIds: [25],
                }
                """.trimIndent(),
            )
            root.resolve("server/generated/en/cards.json").writeText(
                """[{"id":"base1-1","dexId":[1],"cameoDexIds":[25]},{"id":"base1-2","dexId":[4],"cameoDexIds":[25]}]""",
            )
            assertCameoJsonMatchesSource(
                datasetDir = root.resolve("server/generated"),
                languages = listOf("en", "fr"),
            )
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun `Given no source cameo annotations When asserting Then check throws`() {
        val root = createCameoFixture(sourceCameo = false, compiledCameo = false)
        try {
            val error =
                assertFailsWith<IllegalStateException> {
                    assertCameoJsonMatchesSource(
                        datasetDir = root.resolve("server/generated"),
                        languages = listOf("en"),
                    )
                }
            assertTrue(error.message.orEmpty().contains("No cameoDexIds in libs/cards-database/data"))
        } finally {
            root.deleteRecursively()
        }
    }

    private fun createCameoFixture(
        sourceCameo: Boolean,
        compiledCameo: Boolean,
    ): File {
        val root = createTempDirectory("cameo-validation-").toFile()
        val dataDir = root.resolve("data")
        val generatedEn = root.resolve("server/generated/en")
        dataDir.mkdirs()
        generatedEn.mkdirs()
        val sourceBody =
            if (sourceCameo) {
                """
                export default {
                  id: "base1-1",
                  dexId: [1],
                  cameoDexIds: [25],
                }
                """.trimIndent()
            } else {
                """
                export default {
                  id: "base1-1",
                  dexId: [1],
                }
                """.trimIndent()
            }
        dataDir.resolve("card.ts").writeText(sourceBody)
        val compiledBody =
            if (compiledCameo) {
                """[{"id":"base1-1","dexId":[1],"cameoDexIds":[25]}]"""
            } else {
                """[{"id":"base1-1","dexId":[1]}]"""
            }
        generatedEn.resolve("cards.json").writeText(compiledBody)
        return root
    }
}
