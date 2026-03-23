package app.cardium.kmptcgdexsdk.generator

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

class LocalDatabaseGenerationE2eTest {
    private val knownCardId = "2014xy-1"
    private val knownCardLanguage = "en"

    @Test
    fun `Given local generator inputs, When generating database, Then known missing image card is enriched with prices and Pokepedia fallback`() {
        val projectRoot = resolveProjectRoot()
        val datasetDir = projectRoot.resolve("libs/cards-database/server/generated")
        val cardmarketExportDir = projectRoot.resolve("libs/tcgdex-kmp-sdk/generator-inputs/cardmarket")
        val pokepediaTreeFile = projectRoot.resolve("libs/tcgdex-kmp-sdk/generator-inputs/pokepedia/missing-fr-card-images-tree.json")

        assertTrue(
            datasetDir.isDirectory,
            "[x] Missing dataset directory: ${datasetDir.absolutePath}. Run :libs:tcgdex-kmp-sdk:compileCardsDatabaseGenerated first.",
        )
        assertTrue(
            cardmarketExportDir.isDirectory,
            "[x] Missing Cardmarket export directory: ${cardmarketExportDir.absolutePath}.",
        )
        assertTrue(
            pokepediaTreeFile.isFile,
            "[x] Missing Pokepedia tree file: ${pokepediaTreeFile.absolutePath}.",
        )

        val tempDir = createTempDirectory("tcgdex-e2e-").toFile()
        val outputDb = tempDir.resolve("tcgdex.db")

        try {
            main(
                arrayOf(
                    "--dataset=${datasetDir.absolutePath}",
                    "--languages=fr",
                    "--output=${outputDb.absolutePath}",
                    "--force=true",
                    "--cardmarket-export=${cardmarketExportDir.absolutePath}",
                    "--pokepedia-missing=${pokepediaTreeFile.absolutePath}",
                ),
            )

            assertTrue(outputDb.isFile, "[x] Database generation did not create ${outputDb.absolutePath}.")

            Class.forName("org.sqlite.JDBC")
            DriverManager.getConnection("jdbc:sqlite:${outputDb.absolutePath}").use { connection ->
                val row = queryGeneratedCard(connection, knownCardId, knownCardLanguage)
                if (row == null) {
                    val availableLanguages = queryAvailableLanguagesForCard(connection, knownCardId)
                    fail(
                        buildDebugMessage(
                            dbPath = outputDb,
                            datasetDir = datasetDir,
                            cardmarketExportDir = cardmarketExportDir,
                            pokepediaTreeFile = pokepediaTreeFile,
                            cardId = knownCardId,
                            language = knownCardLanguage,
                            row = null,
                            availableLanguages = availableLanguages,
                        ),
                    )
                }

                assertNotNull(row)
                val debugMessage = buildDebugMessage(
                    dbPath = outputDb,
                    datasetDir = datasetDir,
                    cardmarketExportDir = cardmarketExportDir,
                    pokepediaTreeFile = pokepediaTreeFile,
                    cardId = knownCardId,
                    language = knownCardLanguage,
                    row = row,
                    availableLanguages = listOf(knownCardLanguage),
                )

                assertTrue(
                    row.hasAnyCardmarketPrice(),
                    "[x] Expected at least one Cardmarket price column to be populated.\n$debugMessage",
                )
                assertTrue(
                    !row.fallbackImageUrl.isNullOrBlank(),
                    "[x] Expected fallback_image_url to be populated.\n$debugMessage",
                )
                assertEquals(
                    "pokepedia",
                    row.fallbackImageSource,
                    "[x] Expected fallback_image_source to equal 'pokepedia'.\n$debugMessage",
                )
                assertTrue(
                    row.detailedPriceRows > 0,
                    "[x] Expected at least one row in card_prices for stronger end-to-end proof.\n$debugMessage",
                )
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
            if (hasSettings && hasSdkModule) {
                return candidate
            }
            cursor = candidate.parentFile
        }
        error("[x] Could not resolve project root from ${System.getProperty("user.dir")}.")
    }

    private fun queryGeneratedCard(connection: Connection, cardId: String, language: String): GeneratedCardRow? {
        val sql = """
            SELECT
              c.id,
              c.language,
              c.name,
              c.image_url,
              c.fallback_image_url,
              c.fallback_image_source,
              c.price_cardmarket_trend,
              c.price_cardmarket_avg,
              c.price_cardmarket_low,
              COUNT(cp.card_id) AS detailed_price_rows
            FROM cards c
            LEFT JOIN card_prices cp
              ON cp.card_id = c.id
             AND cp.card_language = c.language
            WHERE c.id = ?
              AND c.language = ?
            GROUP BY
              c.id,
              c.language,
              c.name,
              c.image_url,
              c.fallback_image_url,
              c.fallback_image_source,
              c.price_cardmarket_trend,
              c.price_cardmarket_avg,
              c.price_cardmarket_low
        """.trimIndent()

        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, cardId)
            statement.setString(2, language)
            statement.executeQuery().use { resultSet ->
                if (!resultSet.next()) {
                    return null
                }
                return resultSet.toGeneratedCardRow()
            }
        }
    }

    private fun queryAvailableLanguagesForCard(connection: Connection, cardId: String): List<String> {
        val sql = """
            SELECT language
            FROM cards
            WHERE id = ?
            ORDER BY language
        """.trimIndent()
        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, cardId)
            statement.executeQuery().use { resultSet ->
                val languages = mutableListOf<String>()
                while (resultSet.next()) {
                    languages += resultSet.getString("language")
                }
                return languages
            }
        }
    }

    private fun ResultSet.toGeneratedCardRow(): GeneratedCardRow =
        GeneratedCardRow(
            id = getString("id"),
            language = getString("language"),
            name = getString("name"),
            imageUrl = getString("image_url"),
            fallbackImageUrl = getString("fallback_image_url"),
            fallbackImageSource = getString("fallback_image_source"),
            priceTrend = (getObject("price_cardmarket_trend") as? Number)?.toDouble(),
            priceAvg = (getObject("price_cardmarket_avg") as? Number)?.toDouble(),
            priceLow = (getObject("price_cardmarket_low") as? Number)?.toDouble(),
            detailedPriceRows = getLong("detailed_price_rows"),
        )

    private fun buildDebugMessage(
        dbPath: File,
        datasetDir: File,
        cardmarketExportDir: File,
        pokepediaTreeFile: File,
        cardId: String,
        language: String,
        row: GeneratedCardRow?,
        availableLanguages: List<String>,
    ): String = buildString {
        appendLine("[i] SQL E2E debug context")
        appendLine("  - cardId: $cardId")
        appendLine("  - language: $language")
        appendLine("  - dbPath: ${dbPath.absolutePath}")
        appendLine("  - datasetDir: ${datasetDir.absolutePath}")
        appendLine("  - cardmarketExportDir: ${cardmarketExportDir.absolutePath}")
        appendLine("  - pokepediaTreeFile: ${pokepediaTreeFile.absolutePath}")
        appendLine("  - availableLanguagesForCard: ${availableLanguages.joinToString(",").ifBlank { "<none>" }}")
        appendLine("  - row: ${row ?: "<missing>"}")
    }

    private data class GeneratedCardRow(
        val id: String,
        val language: String,
        val name: String?,
        val imageUrl: String?,
        val fallbackImageUrl: String?,
        val fallbackImageSource: String?,
        val priceTrend: Double?,
        val priceAvg: Double?,
        val priceLow: Double?,
        val detailedPriceRows: Long,
    ) {
        fun hasAnyCardmarketPrice(): Boolean = priceTrend != null || priceAvg != null || priceLow != null
    }
}
