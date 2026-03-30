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
    private val targetLanguage = "fr"
    private val englishLanguage = "en"
    private val mepFallbackTargetCards =
        listOf(
            TargetCardExpectation(id = "mep-030"),
            TargetCardExpectation(id = "mep-037"),
            TargetCardExpectation(id = "mep-064"),
        )
    private val me03AssetTargetCards =
        listOf(
            TargetCardExpectation(id = "me03-001"),
            TargetCardExpectation(id = "me03-050"),
            TargetCardExpectation(id = "me03-124"),
        )

    @Test
    fun `Given local generator inputs, When generating database, Then MEP cards are enriched with Pokepedia fallback`() {
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
                    "--languages=en,fr",
                    "--output=${outputDb.absolutePath}",
                    "--force=true",
                    "--cardmarket-export=${cardmarketExportDir.absolutePath}",
                    "--pokepedia-missing=${pokepediaTreeFile.absolutePath}",
                ),
            )

            assertTrue(outputDb.isFile, "[x] Database generation did not create ${outputDb.absolutePath}.")

            Class.forName("org.sqlite.JDBC")
            DriverManager.getConnection("jdbc:sqlite:${outputDb.absolutePath}").use { connection ->
                mepFallbackTargetCards.forEach { target ->
                    val row = queryGeneratedCard(connection, target.id, targetLanguage)
                    if (row == null) {
                        val availableLanguages = queryAvailableLanguagesForCard(connection, target.id)
                        fail(
                            buildDebugMessage(
                                dbPath = outputDb,
                                datasetDir = datasetDir,
                                cardmarketExportDir = cardmarketExportDir,
                                pokepediaTreeFile = pokepediaTreeFile,
                                cardId = target.id,
                                language = targetLanguage,
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
                        cardId = target.id,
                        language = targetLanguage,
                        row = row,
                        availableLanguages = listOf(targetLanguage),
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

                }

                val me03SetRow = queryGeneratedSet(connection, "me03", targetLanguage)
                assertNotNull(
                    me03SetRow,
                    "[x] Expected a generated set row for me03/$targetLanguage.",
                )
                assertTrue(
                    !me03SetRow.logoUrl.isNullOrBlank(),
                    "[x] Expected me03.logo_url to be populated.\n${buildSetDebugMessage(outputDb, datasetDir, cardmarketExportDir, pokepediaTreeFile, "me03", targetLanguage, me03SetRow)}",
                )
                assertTrue(
                    !me03SetRow.symbolUrl.isNullOrBlank(),
                    "[x] Expected me03.symbol_url to be populated.\n${buildSetDebugMessage(outputDb, datasetDir, cardmarketExportDir, pokepediaTreeFile, "me03", targetLanguage, me03SetRow)}",
                )

                val me03EnglishSetRow = queryGeneratedSet(connection, "me03", englishLanguage)
                assertNotNull(
                    me03EnglishSetRow,
                    "[x] Expected a generated set row for me03/$englishLanguage.",
                )
                assertTrue(
                    !me03EnglishSetRow.logoUrl.isNullOrBlank(),
                    "[x] Expected me03.logo_url to be populated for $englishLanguage.\n${buildSetDebugMessage(outputDb, datasetDir, cardmarketExportDir, pokepediaTreeFile, "me03", englishLanguage, me03EnglishSetRow)}",
                )
                assertTrue(
                    !me03EnglishSetRow.symbolUrl.isNullOrBlank(),
                    "[x] Expected me03.symbol_url to be populated for $englishLanguage.\n${buildSetDebugMessage(outputDb, datasetDir, cardmarketExportDir, pokepediaTreeFile, "me03", englishLanguage, me03EnglishSetRow)}",
                )

                me03AssetTargetCards.forEach { target ->
                    val row = queryGeneratedCard(connection, target.id, targetLanguage)
                    if (row == null) {
                        val availableLanguages = queryAvailableLanguagesForCard(connection, target.id)
                        fail(
                            buildDebugMessage(
                                dbPath = outputDb,
                                datasetDir = datasetDir,
                                cardmarketExportDir = cardmarketExportDir,
                                pokepediaTreeFile = pokepediaTreeFile,
                                cardId = target.id,
                                language = targetLanguage,
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
                        cardId = target.id,
                        language = targetLanguage,
                        row = row,
                        availableLanguages = listOf(targetLanguage),
                    )

                    assertTrue(
                        !row.rarityId.isNullOrBlank(),
                        "[x] Expected rarity_id to be populated for ${target.id}.\n$debugMessage",
                    )
                    assertTrue(
                        !row.imageUrl.isNullOrBlank() || !row.fallbackImageUrl.isNullOrBlank(),
                        "[x] Expected image_url or fallback_image_url to be populated for ${target.id}.\n$debugMessage",
                    )

                    val englishRow = queryGeneratedCard(connection, target.id, englishLanguage)
                    if (englishRow == null) {
                        val availableLanguages = queryAvailableLanguagesForCard(connection, target.id)
                        fail(
                            buildDebugMessage(
                                dbPath = outputDb,
                                datasetDir = datasetDir,
                                cardmarketExportDir = cardmarketExportDir,
                                pokepediaTreeFile = pokepediaTreeFile,
                                cardId = target.id,
                                language = englishLanguage,
                                row = null,
                                availableLanguages = availableLanguages,
                            ),
                        )
                    }

                    assertNotNull(englishRow)
                    val englishDebugMessage = buildDebugMessage(
                        dbPath = outputDb,
                        datasetDir = datasetDir,
                        cardmarketExportDir = cardmarketExportDir,
                        pokepediaTreeFile = pokepediaTreeFile,
                        cardId = target.id,
                        language = englishLanguage,
                        row = englishRow,
                        availableLanguages = listOf(englishLanguage, targetLanguage),
                    )

                    assertTrue(
                        !englishRow.rarityId.isNullOrBlank(),
                        "[x] Expected rarity_id to be populated for ${target.id}/$englishLanguage.\n$englishDebugMessage",
                    )
                    assertTrue(
                        !englishRow.imageUrl.isNullOrBlank() || !englishRow.fallbackImageUrl.isNullOrBlank(),
                        "[x] Expected image_url or fallback_image_url to be populated for ${target.id}/$englishLanguage.\n$englishDebugMessage",
                    )
                    assertEquals(
                        "pokepedia",
                        englishRow.fallbackImageSource,
                        "[x] Expected fallback_image_source to equal 'pokepedia' for ${target.id}/$englishLanguage.\n$englishDebugMessage",
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
              c.rarity_id,
              c.image_url,
              c.fallback_image_url,
              c.fallback_image_source,
              c.price_cardmarket_trend,
              c.price_cardmarket_avg,
              c.price_cardmarket_low,
              s.logo_url,
              s.symbol_url,
              COUNT(cp.card_id) AS detailed_price_rows
            FROM cards c
            INNER JOIN sets s
              ON s.id = c.set_id
             AND s.language = c.language
            LEFT JOIN card_prices cp
              ON cp.card_id = c.id
             AND cp.card_language = c.language
            WHERE c.id = ?
              AND c.language = ?
            GROUP BY
              c.id,
              c.language,
              c.name,
              c.rarity_id,
              c.image_url,
              c.fallback_image_url,
              c.fallback_image_source,
              c.price_cardmarket_trend,
              c.price_cardmarket_avg,
              c.price_cardmarket_low,
              s.logo_url,
              s.symbol_url
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

    private fun queryGeneratedSet(connection: Connection, setId: String, language: String): GeneratedSetRow? {
        val sql = """
            SELECT
              id,
              language,
              name,
              logo_url,
              symbol_url
            FROM sets
            WHERE id = ?
              AND language = ?
        """.trimIndent()

        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, setId)
            statement.setString(2, language)
            statement.executeQuery().use { resultSet ->
                if (!resultSet.next()) {
                    return null
                }
                return GeneratedSetRow(
                    id = resultSet.getString("id"),
                    language = resultSet.getString("language"),
                    name = resultSet.getString("name"),
                    logoUrl = resultSet.getString("logo_url"),
                    symbolUrl = resultSet.getString("symbol_url"),
                )
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
            rarityId = getString("rarity_id"),
            imageUrl = getString("image_url"),
            fallbackImageUrl = getString("fallback_image_url"),
            fallbackImageSource = getString("fallback_image_source"),
            priceTrend = (getObject("price_cardmarket_trend") as? Number)?.toDouble(),
            priceAvg = (getObject("price_cardmarket_avg") as? Number)?.toDouble(),
            priceLow = (getObject("price_cardmarket_low") as? Number)?.toDouble(),
            setLogoUrl = getString("logo_url"),
            setSymbolUrl = getString("symbol_url"),
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

    private fun buildSetDebugMessage(
        dbPath: File,
        datasetDir: File,
        cardmarketExportDir: File,
        pokepediaTreeFile: File,
        setId: String,
        language: String,
        row: GeneratedSetRow?,
    ): String = buildString {
        appendLine("[i] SQL E2E set debug context")
        appendLine("  - setId: $setId")
        appendLine("  - language: $language")
        appendLine("  - dbPath: ${dbPath.absolutePath}")
        appendLine("  - datasetDir: ${datasetDir.absolutePath}")
        appendLine("  - cardmarketExportDir: ${cardmarketExportDir.absolutePath}")
        appendLine("  - pokepediaTreeFile: ${pokepediaTreeFile.absolutePath}")
        appendLine("  - row: ${row ?: "<missing>"}")
    }

    private data class GeneratedCardRow(
        val id: String,
        val language: String,
        val name: String?,
        val rarityId: String?,
        val imageUrl: String?,
        val fallbackImageUrl: String?,
        val fallbackImageSource: String?,
        val priceTrend: Double?,
        val priceAvg: Double?,
        val priceLow: Double?,
        val setLogoUrl: String?,
        val setSymbolUrl: String?,
        val detailedPriceRows: Long,
    )

    private data class GeneratedSetRow(
        val id: String,
        val language: String,
        val name: String?,
        val logoUrl: String?,
        val symbolUrl: String?,
    )

    private data class TargetCardExpectation(
        val id: String,
    )
}
