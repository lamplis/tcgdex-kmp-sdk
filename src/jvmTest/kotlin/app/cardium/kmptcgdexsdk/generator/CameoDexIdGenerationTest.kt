package app.cardium.kmptcgdexsdk.generator

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class CameoDexIdGenerationTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `Given card with cameo dex ids When generating database Then card_pokemon marks cameo rows`() {
        val projectRoot = resolveProjectRoot()
        val datasetDir = projectRoot.resolve("libs/cards-database/server/generated")
        val cardsFile = datasetDir.resolve("en/cards.json")
        val cardmarketExportDir = projectRoot.resolve("libs/tcgdex-kmp-sdk/generator-inputs/cardmarket")
        val pokepediaTreeFile = projectRoot.resolve("libs/tcgdex-kmp-sdk/generator-inputs/pokepedia/missing-fr-card-images-tree.json")

        assertTrue(cardsFile.isFile, "[x] Missing cards dataset file: ${cardsFile.absolutePath}")
        assertTrue(datasetDir.isDirectory, "[x] Missing generated dataset directory: ${datasetDir.absolutePath}")
        assertTrue(cardmarketExportDir.isDirectory, "[x] Missing Cardmarket export directory: ${cardmarketExportDir.absolutePath}")
        assertTrue(pokepediaTreeFile.isFile, "[x] Missing Pokepedia tree file: ${pokepediaTreeFile.absolutePath}")

        val cameoCard = findCardWithCameo(cardsFile)
        assertNotNull(
            cameoCard,
            "[x] No EN cards with cameoDexIds in ${cardsFile.absolutePath}. Compiler likely dropped the field.",
        )

        val tempDir = createTempDirectory("tcgdex-cameo-").toFile()
        val outputDb = tempDir.resolve("tcgdex.db")
        val emptyRecognitionVectors = tempDir.resolve("recognition-empty.json")
        emptyRecognitionVectors.writeText("""{"cards":[],"missingCardIds":[]}""")

        try {
            main(
                arrayOf(
                    "--dataset=${datasetDir.absolutePath}",
                    "--languages=en",
                    "--output=${outputDb.absolutePath}",
                    "--force=true",
                    "--cardmarket-export=${cardmarketExportDir.absolutePath}",
                    "--pokepedia-missing=${pokepediaTreeFile.absolutePath}",
                    "--recognition-vectors=${emptyRecognitionVectors.absolutePath}",
                ),
            )

            assertTrue(outputDb.isFile, "[x] Database generation did not create ${outputDb.absolutePath}.")
            Class.forName("org.sqlite.JDBC")
            DriverManager.getConnection("jdbc:sqlite:${outputDb.absolutePath}").use { connection ->
                val mainDexId = assertNotNull(cameoCard.mainDexId, "[x] Expected a main dexId on ${cameoCard.id}.")
                val mainIsCameo = queryIsCameo(connection, cameoCard.id, "en", mainDexId)
                val cameoIsCameo = queryIsCameo(connection, cameoCard.id, "en", cameoCard.cameoDexId)

                assertEquals(0, mainIsCameo, "[x] Main dex row should be marked as solo (is_cameo=0).")
                assertEquals(1, cameoIsCameo, "[x] Cameo dex row should be marked as cameo (is_cameo=1).")
            }
        } finally {
            tempDir.deleteRecursively()
        }
    }

    @Test
    fun `Given Snorlax Doll trainer card When generating database Then it is a cameo of dex 143`() {
        val projectRoot = resolveProjectRoot()
        val datasetDir = projectRoot.resolve("libs/cards-database/server/generated")
        val cardsFile = datasetDir.resolve("en/cards.json")
        val cardmarketExportDir = projectRoot.resolve("libs/tcgdex-kmp-sdk/generator-inputs/cardmarket")
        val pokepediaTreeFile =
            projectRoot.resolve("libs/tcgdex-kmp-sdk/generator-inputs/pokepedia/missing-fr-card-images-tree.json")

        assertTrue(cardsFile.isFile, "[x] Missing cards dataset file: ${cardsFile.absolutePath}")

        val trainerCameos =
            listOf(
                "sv04-175" to 143,
                "base1-70" to 35,
            )
        trainerCameos.forEach { (cardId, dexId) ->
            val cameoIds = readCameoDexIds(cardsFile, cardId)
            assertTrue(
                dexId in cameoIds,
                "[x] Expected $cardId compiled cameoDexIds to contain $dexId, got $cameoIds.",
            )
        }

        val trainerOnly = findCardWithCameo(cardsFile, requireMainDex = false)
        assertNotNull(
            trainerOnly,
            "[x] No trainer-only cameoDexIds in ${cardsFile.absolutePath}.",
        )
        assertEquals(
            null,
            trainerOnly.mainDexId,
            "[x] Trainer-only finder should return a card with no main dexId, got ${trainerOnly.id}.",
        )

        val tempDir = createTempDirectory("tcgdex-trainer-cameo-").toFile()
        val outputDb = tempDir.resolve("tcgdex.db")
        val emptyRecognitionVectors = tempDir.resolve("recognition-empty.json")
        emptyRecognitionVectors.writeText("""{"cards":[],"missingCardIds":[]}""")

        try {
            main(
                arrayOf(
                    "--dataset=${datasetDir.absolutePath}",
                    "--languages=en",
                    "--output=${outputDb.absolutePath}",
                    "--force=true",
                    "--cardmarket-export=${cardmarketExportDir.absolutePath}",
                    "--pokepedia-missing=${pokepediaTreeFile.absolutePath}",
                    "--recognition-vectors=${emptyRecognitionVectors.absolutePath}",
                ),
            )

            assertTrue(outputDb.isFile, "[x] Database generation did not create ${outputDb.absolutePath}.")
            Class.forName("org.sqlite.JDBC")
            DriverManager.getConnection("jdbc:sqlite:${outputDb.absolutePath}").use { connection ->
                trainerCameos.forEach { (cardId, dexId) ->
                    assertEquals(
                        1,
                        queryIsCameo(connection, cardId, "en", dexId),
                        "[x] Expected $cardId dex $dexId to persist as is_cameo=1.",
                    )
                }
            }
        } finally {
            tempDir.deleteRecursively()
        }
    }

    private fun resolveProjectRoot(): File {
        var cursor: File? = File(System.getProperty("user.dir")).absoluteFile
        repeat(8) {
            val candidate = cursor ?: return@repeat
            val hasSettings = candidate.resolve("settings.gradle.kts").isFile
            val hasSdkModule = candidate.resolve("libs/tcgdex-kmp-sdk").isDirectory
            if (hasSettings && hasSdkModule) return candidate
            cursor = candidate.parentFile
        }
        error("[x] Could not resolve project root from ${System.getProperty("user.dir")}.")
    }

    private fun findCardWithCameo(
        cardsFile: File,
        requireMainDex: Boolean = true,
    ): CameoCard? {
        val cards = json.parseToJsonElement(cardsFile.readText()).jsonArray
        fun match(trainerOnly: Boolean): CameoCard? {
            cards.forEach { element ->
                val card = element.jsonObject
                val id = card["id"]?.jsonPrimitive?.contentOrNull ?: return@forEach
                val dexIds = card.readIntArray("dexId")
                val cameoDexIds = card.readIntArray("cameoDexIds")
                if (cameoDexIds.isEmpty()) return@forEach
                if (trainerOnly) {
                    if (dexIds.isNotEmpty()) return@forEach
                    return CameoCard(id = id, mainDexId = null, cameoDexId = cameoDexIds.first())
                }
                if (dexIds.isEmpty()) return@forEach
                val mainDex = dexIds.first()
                val cameoDex = cameoDexIds.firstOrNull { it !in dexIds } ?: return@forEach
                return CameoCard(id = id, mainDexId = mainDex, cameoDexId = cameoDex)
            }
            return null
        }
        return if (requireMainDex) {
            match(trainerOnly = false)
        } else {
            match(trainerOnly = true) ?: match(trainerOnly = false)
        }
    }

    private fun readCameoDexIds(
        cardsFile: File,
        cardId: String,
    ): List<Int> {
        val cards = json.parseToJsonElement(cardsFile.readText()).jsonArray
        cards.forEach { element ->
            val card = element.jsonObject
            val id = card["id"]?.jsonPrimitive?.contentOrNull ?: return@forEach
            if (id == cardId) return card.readIntArray("cameoDexIds")
        }
        return emptyList()
    }

    private fun queryIsCameo(
        connection: Connection,
        cardId: String,
        language: String,
        dexId: Int,
    ): Int? {
        val sql =
            """
            SELECT is_cameo
            FROM card_pokemon
            WHERE card_id = ? AND language = ? AND pokemon_dex_id = ?
            """.trimIndent()
        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, cardId)
            statement.setString(2, language)
            statement.setInt(3, dexId)
            statement.executeQuery().use { rs ->
                if (!rs.next()) return null
                return rs.getInt("is_cameo")
            }
        }
    }

    private fun JsonObject.readIntArray(key: String): List<Int> {
        val raw = this[key] ?: return emptyList()
        if (raw !is JsonArray) return emptyList()
        return raw.mapNotNull { it.jsonPrimitive.intOrNull }
    }

    private data class CameoCard(
        val id: String,
        val mainDexId: Int?,
        val cameoDexId: Int,
    )
}
