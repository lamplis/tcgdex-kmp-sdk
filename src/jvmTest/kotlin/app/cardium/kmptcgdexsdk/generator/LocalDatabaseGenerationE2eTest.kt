package app.cardium.kmptcgdexsdk.generator

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
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
    private val me05FallbackTargetCards =
        listOf(
            TargetCardExpectation(id = "me05-075"),
            TargetCardExpectation(id = "me05-083"),
            TargetCardExpectation(id = "me05-089"),
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
        val recognitionVectorsFile = projectRoot.resolve("libs/tcgdex-kmp-sdk/generator-inputs/recognition/card-vectors-fr.json")

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
        assertTrue(
            recognitionVectorsFile.isFile,
            "[x] Missing recognition vectors file: ${recognitionVectorsFile.absolutePath}.",
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
                    "--recognition-vectors=${recognitionVectorsFile.absolutePath}",
                ),
            )

            assertTrue(outputDb.isFile, "[x] Database generation did not create ${outputDb.absolutePath}.")

            Class.forName("org.sqlite.JDBC")
            DriverManager.getConnection("jdbc:sqlite:${outputDb.absolutePath}").use { connection ->
                (mepFallbackTargetCards + me05FallbackTargetCards).forEach { target ->
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
                    if (!englishRow.fallbackImageUrl.isNullOrBlank()) {
                        assertEquals(
                            "pokepedia",
                            englishRow.fallbackImageSource,
                            "[x] Expected fallback_image_source to equal 'pokepedia' for ${target.id}/$englishLanguage.\n$englishDebugMessage",
                        )
                    } else {
                        assertTrue(
                            !englishRow.imageUrl.isNullOrBlank(),
                            "[x] Expected English row to keep a real image URL when no fallback is present.\n$englishDebugMessage",
                        )
                    }
                }

                val recognitionRows = queryRecognitionRowsForCard(connection, "me03-001", targetLanguage)
                assertTrue(
                    recognitionRows > 0,
                    "[x] Expected recognition rows for me03-001/$targetLanguage.",
                )

                // Sub-set cards (e.g. Trainer Gallery) have no compiler-provided image because
                // the CDN manifest only indexes parent set folders. The generator must
                // synthesize a manifest-confirmed URL under the parent folder.
                val trainerGalleryRow = queryGeneratedCard(connection, "swsh10tg-TG28", targetLanguage)
                assertNotNull(
                    trainerGalleryRow,
                    "[x] Expected a generated card row for swsh10tg-TG28/$targetLanguage.",
                )
                assertEquals(
                    "https://assets.tcgdex.net/fr/swsh/swsh10/TG28",
                    trainerGalleryRow.imageUrl,
                    "[x] Expected synthesized parent-folder image_url for swsh10tg-TG28/$targetLanguage.",
                )

                val classicCollectionCard = queryGeneratedCard(connection, "cel25cc-CC001", targetLanguage)
                assertNotNull(
                    classicCollectionCard,
                    "[x] Expected a generated card row for cel25cc-CC001/$targetLanguage.",
                )
                assertTrue(
                    classicCollectionCard.imageUrl.isNullOrBlank(),
                    "[x] Expected cel25cc-CC001/$targetLanguage image_url to stay empty (no CDN asset), " +
                        "got '${classicCollectionCard.imageUrl}'.",
                )

                val classicCollectionSet = queryGeneratedSet(connection, "cel25cc", targetLanguage)
                assertNotNull(
                    classicCollectionSet,
                    "[x] Expected a generated set row for cel25cc/$targetLanguage.",
                )
                assertEquals(
                    "cel25",
                    classicCollectionSet.parentSetId,
                    "[x] Expected cel25cc parent_set_id to be cel25.",
                )
                assertEquals(
                    "CEL:CC",
                    classicCollectionSet.abbreviationOfficial,
                    "[x] Expected cel25cc abbreviation_official to be CEL:CC.",
                )

                val trainerGallerySet = queryGeneratedSet(connection, "swsh10tg", targetLanguage)
                assertNotNull(
                    trainerGallerySet,
                    "[x] Expected a generated set row for swsh10tg/$targetLanguage.",
                )
                assertEquals(
                    "swsh10",
                    trainerGallerySet.parentSetId,
                    "[x] Expected swsh10tg parent_set_id to be swsh10.",
                )
                assertEquals(
                    "ASR:TG",
                    trainerGallerySet.abbreviationOfficial,
                    "[x] Expected swsh10tg abbreviation_official to be ASR:TG.",
                )

                val hiddenFatesVaultSet = queryGeneratedSet(connection, "sm115sv", targetLanguage)
                assertNotNull(
                    hiddenFatesVaultSet,
                    "[x] Expected a generated set row for sm115sv/$targetLanguage.",
                )
                assertEquals(
                    "sm115",
                    hiddenFatesVaultSet.parentSetId,
                    "[x] Expected sm115sv parent_set_id to be sm115.",
                )
                assertEquals(
                    "HIF:SV",
                    hiddenFatesVaultSet.abbreviationOfficial,
                    "[x] Expected sm115sv abbreviation_official to be HIF:SV.",
                )
                assertNull(
                    queryGeneratedSet(connection, "sma", targetLanguage),
                    "[x] Expected sma to be remapped away; found a leftover sma set row.",
                )

                val vaultCard = queryGeneratedCard(connection, "sm115sv-SV1", targetLanguage)
                assertNotNull(
                    vaultCard,
                    "[x] Expected generated card sm115sv-SV1/$targetLanguage.",
                )
                assertTrue(
                    vaultCard.imageUrl.isNullOrBlank(),
                    "[x] Expected sm115sv-SV1 FR image_url to stay empty (no parent-folder CDN).",
                )
                assertEquals(
                    "pokepedia",
                    vaultCard.fallbackImageSource,
                    "[x] Expected sm115sv-SV1 fallback_image_source to be pokepedia.",
                )
                assertTrue(
                    vaultCard.fallbackImageUrl?.contains("Destin") == true,
                    "[x] Expected sm115sv-SV1 Pokepedia Destinees Occultes fallback URL, got '${vaultCard.fallbackImageUrl}'.",
                )
                assertTrue(
                    vaultCard.detailedPriceRows > 0,
                    "[x] Expected Cardmarket prices on sm115sv-SV1, got ${vaultCard.detailedPriceRows} rows.",
                )
                val vaultRecognitionSources = queryRecognitionSourcesForCard(
                    connection,
                    "sm115sv-SV1",
                    targetLanguage,
                )
                assertTrue(
                    "pokepedia" in vaultRecognitionSources,
                    "[x] Expected pokepedia recognition hash for sm115sv-SV1, got $vaultRecognitionSources.",
                )

                assertTrue(
                    queryCameoRowCount(connection, englishLanguage) > 0,
                    "[x] Expected is_cameo=1 rows for en.",
                )
                assertTrue(
                    queryCameoRowCount(connection, targetLanguage) > 0,
                    "[x] Expected is_cameo=1 rows for fr.",
                )
            }
        } finally {
            tempDir.deleteRecursively()
        }
    }

    @Test
    fun `Given local generator inputs, When generating database, Then Skyridge H04 uses Pokepedia crystal artwork not numbered 4`() {
        val projectRoot = resolveProjectRoot()
        val datasetDir = projectRoot.resolve("libs/cards-database/server/generated")
        val cardmarketExportDir = projectRoot.resolve("libs/tcgdex-kmp-sdk/generator-inputs/cardmarket")
        val pokepediaTreeFile = projectRoot.resolve(
            "libs/tcgdex-kmp-sdk/generator-inputs/pokepedia/missing-fr-card-images-tree.json",
        )
        val recognitionVectorsFile = projectRoot.resolve(
            "libs/tcgdex-kmp-sdk/generator-inputs/recognition/card-vectors-fr.json",
        )

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
        assertTrue(
            recognitionVectorsFile.isFile,
            "[x] Missing recognition vectors file: ${recognitionVectorsFile.absolutePath}.",
        )

        val tempDir = createTempDirectory("tcgdex-e2e-ecard3-h04-").toFile()
        val outputDb = tempDir.resolve("tcgdex.db")
        val crystalFileName = "Carte_Skyridge_H4.png"

        try {
            main(
                arrayOf(
                    "--dataset=${datasetDir.absolutePath}",
                    "--languages=en,fr",
                    "--output=${outputDb.absolutePath}",
                    "--force=true",
                    "--cardmarket-export=${cardmarketExportDir.absolutePath}",
                    "--pokepedia-missing=${pokepediaTreeFile.absolutePath}",
                    "--recognition-vectors=${recognitionVectorsFile.absolutePath}",
                ),
            )

            assertTrue(outputDb.isFile, "[x] Database generation did not create ${outputDb.absolutePath}.")

            Class.forName("org.sqlite.JDBC")
            DriverManager.getConnection("jdbc:sqlite:${outputDb.absolutePath}").use { connection ->
                listOf(targetLanguage, englishLanguage).forEach { language ->
                    val row = queryCardFallbackRow(connection, "ecard3-H04", language)
                    if (row == null) {
                        val availableLanguages = queryAvailableLanguagesForCard(connection, "ecard3-H04")
                        fail(
                            buildDebugMessage(
                                dbPath = outputDb,
                                datasetDir = datasetDir,
                                cardmarketExportDir = cardmarketExportDir,
                                pokepediaTreeFile = pokepediaTreeFile,
                                cardId = "ecard3-H04",
                                language = language,
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
                        cardId = "ecard3-H04",
                        language = language,
                        row = row,
                        availableLanguages = listOf(englishLanguage, targetLanguage),
                    )

                    assertTrue(
                        row.imageUrl.isNullOrBlank(),
                        "[x] Expected ecard3-H04/$language image_url to stay empty (no CDN asset).\n$debugMessage",
                    )
                    assertTrue(
                        row.fallbackImageUrl?.contains(crystalFileName) == true,
                        "[x] Expected ecard3-H04/$language Pokepedia fallback to contain $crystalFileName.\n$debugMessage",
                    )
                    assertEquals(
                        "pokepedia",
                        row.fallbackImageSource,
                        "[x] Expected ecard3-H04/$language fallback_image_source to equal 'pokepedia'.\n$debugMessage",
                    )
                }

                val numberedRow = queryCardFallbackRow(connection, "ecard3-4", targetLanguage)
                assertNotNull(
                    numberedRow,
                    "[x] Expected a generated card row for ecard3-4/$targetLanguage.",
                )
                assertTrue(
                    numberedRow.fallbackImageUrl?.contains(crystalFileName) != true,
                    "[x] Expected ecard3-4 to keep Articuno artwork, not $crystalFileName. " +
                        "got fallback='${numberedRow.fallbackImageUrl}'.",
                )
                assertTrue(
                    numberedRow.imageUrl?.contains(crystalFileName) != true,
                    "[x] Expected ecard3-4 image_url not to be $crystalFileName. " +
                        "got imageUrl='${numberedRow.imageUrl}'.",
                )
                assertTrue(
                    queryRecognitionRowsForCard(connection, "sm115sv-SV1", targetLanguage) > 0,
                    "[x] Expected recognition hashes for sm115sv-SV1/$targetLanguage.",
                )
            }
        } finally {
            tempDir.deleteRecursively()
        }
    }

    @Test
    fun `Given local generator inputs, When generating database, Then international McDonalds 2018 and 2019 use Pokepedia International artwork`() {
        val projectRoot = resolveProjectRoot()
        val datasetDir = projectRoot.resolve("libs/cards-database/server/generated")
        val cardmarketExportDir = projectRoot.resolve("libs/tcgdex-kmp-sdk/generator-inputs/cardmarket")
        val pokepediaTreeFile = projectRoot.resolve(
            "libs/tcgdex-kmp-sdk/generator-inputs/pokepedia/missing-fr-card-images-tree.json",
        )
        val recognitionVectorsFile = projectRoot.resolve(
            "libs/tcgdex-kmp-sdk/generator-inputs/recognition/card-vectors-fr.json",
        )

        assertTrue(datasetDir.isDirectory, "[x] Missing dataset directory: ${datasetDir.absolutePath}.")
        assertTrue(pokepediaTreeFile.isFile, "[x] Missing Pokepedia tree file: ${pokepediaTreeFile.absolutePath}.")

        val tempDir = createTempDirectory("tcgdex-e2e-mcdonalds-international-").toFile()
        val outputDb = tempDir.resolve("tcgdex.db")
        val international2019 = "Carte_Collection_McDonald's_2019_(International)_1.png"
        val international2018 = "Carte_Collection_McDonald's_2018_(International)_11.png"
        val france2019 = "Carte_Collection_McDonald's_2019_(France)_1.png"

        try {
            main(
                arrayOf(
                    "--dataset=${datasetDir.absolutePath}",
                    "--languages=en,fr",
                    "--output=${outputDb.absolutePath}",
                    "--force=true",
                    "--cardmarket-export=${cardmarketExportDir.absolutePath}",
                    "--pokepedia-missing=${pokepediaTreeFile.absolutePath}",
                    "--recognition-vectors=${recognitionVectorsFile.absolutePath}",
                ),
            )

            assertTrue(outputDb.isFile, "[x] Database generation did not create ${outputDb.absolutePath}.")

            Class.forName("org.sqlite.JDBC")
            DriverManager.getConnection("jdbc:sqlite:${outputDb.absolutePath}").use { connection ->
                listOf(targetLanguage, englishLanguage).forEach { language ->
                    assertPokepediaInternationalFallback(
                        connection = connection,
                        dbPath = outputDb,
                        datasetDir = datasetDir,
                        cardmarketExportDir = cardmarketExportDir,
                        pokepediaTreeFile = pokepediaTreeFile,
                        cardId = "2019sm-1",
                        language = language,
                        filename = international2019,
                    )
                    assertPokepediaInternationalFallback(
                        connection = connection,
                        dbPath = outputDb,
                        datasetDir = datasetDir,
                        cardmarketExportDir = cardmarketExportDir,
                        pokepediaTreeFile = pokepediaTreeFile,
                        cardId = "2018sm-11",
                        language = language,
                        filename = international2018,
                    )
                }

                val franceRow = queryCardFallbackRow(connection, "2019sm-fr-1", targetLanguage)
                assertNotNull(franceRow, "[x] Expected a generated card row for 2019sm-fr-1/$targetLanguage.")
                val franceFallback = franceRow.fallbackImageUrl
                assertTrue(
                    franceFallback?.contains(france2019) == true,
                    "[x] Expected 2019sm-fr-1 fallback to contain $france2019. got '$franceFallback'.",
                )
                assertTrue(
                    franceFallback?.contains("(International)") != true,
                    "[x] Expected 2019sm-fr-1 not to use International artwork. got '$franceFallback'.",
                )
            }
        } finally {
            tempDir.deleteRecursively()
        }
    }

    @Test
    fun `Given local generator inputs, When generating database, Then smp-SM226 uses Pokepedia Charizard promo not collection chest`() {
        val projectRoot = resolveProjectRoot()
        val datasetDir = projectRoot.resolve("libs/cards-database/server/generated")
        val cardmarketExportDir = projectRoot.resolve("libs/tcgdex-kmp-sdk/generator-inputs/cardmarket")
        val pokepediaTreeFile = projectRoot.resolve(
            "libs/tcgdex-kmp-sdk/generator-inputs/pokepedia/missing-fr-card-images-tree.json",
        )
        val recognitionVectorsFile = projectRoot.resolve(
            "libs/tcgdex-kmp-sdk/generator-inputs/recognition/card-vectors-fr.json",
        )

        assertTrue(datasetDir.isDirectory, "[x] Missing dataset directory: ${datasetDir.absolutePath}.")
        assertTrue(pokepediaTreeFile.isFile, "[x] Missing Pokepedia tree file: ${pokepediaTreeFile.absolutePath}.")

        val tempDir = createTempDirectory("tcgdex-e2e-smp-sm226-").toFile()
        val outputDb = tempDir.resolve("tcgdex.db")
        val sm226FileName = "Carte_Promo_SM_SM226.png"
        val sm225FileName = "Carte_Promo_SM_SM225.png"

        try {
            main(
                arrayOf(
                    "--dataset=${datasetDir.absolutePath}",
                    "--languages=en,fr",
                    "--output=${outputDb.absolutePath}",
                    "--force=true",
                    "--cardmarket-export=${cardmarketExportDir.absolutePath}",
                    "--pokepedia-missing=${pokepediaTreeFile.absolutePath}",
                    "--recognition-vectors=${recognitionVectorsFile.absolutePath}",
                ),
            )

            assertTrue(outputDb.isFile, "[x] Database generation did not create ${outputDb.absolutePath}.")

            Class.forName("org.sqlite.JDBC")
            DriverManager.getConnection("jdbc:sqlite:${outputDb.absolutePath}").use { connection ->
                listOf(targetLanguage, englishLanguage).forEach { language ->
                    assertPokepediaInternationalFallback(
                        connection = connection,
                        dbPath = outputDb,
                        datasetDir = datasetDir,
                        cardmarketExportDir = cardmarketExportDir,
                        pokepediaTreeFile = pokepediaTreeFile,
                        cardId = "smp-SM226",
                        language = language,
                        filename = sm226FileName,
                    )
                    val sm226Row = queryCardFallbackRow(connection, "smp-SM226", language)
                    assertNotNull(sm226Row)
                    assertTrue(
                        sm226Row.fallbackImageUrl?.contains("Coffre") != true,
                        "[x] Expected smp-SM226/$language fallback not to contain Coffre. " +
                            "got '${sm226Row.fallbackImageUrl}'.",
                    )
                    assertPokepediaInternationalFallback(
                        connection = connection,
                        dbPath = outputDb,
                        datasetDir = datasetDir,
                        cardmarketExportDir = cardmarketExportDir,
                        pokepediaTreeFile = pokepediaTreeFile,
                        cardId = "smp-SM225",
                        language = language,
                        filename = sm225FileName,
                    )
                }
            }
        } finally {
            tempDir.deleteRecursively()
        }
    }

    @Test
    fun `Given local generator inputs, When generating database, Then tk-ex-m uses Negapi artwork not Posipi Groret`() {
        val projectRoot = resolveProjectRoot()
        val datasetDir = projectRoot.resolve("libs/cards-database/server/generated")
        val cardmarketExportDir = projectRoot.resolve("libs/tcgdex-kmp-sdk/generator-inputs/cardmarket")
        val pokepediaTreeFile = projectRoot.resolve(
            "libs/tcgdex-kmp-sdk/generator-inputs/pokepedia/missing-fr-card-images-tree.json",
        )
        val recognitionVectorsFile = projectRoot.resolve(
            "libs/tcgdex-kmp-sdk/generator-inputs/recognition/card-vectors-fr.json",
        )

        assertTrue(datasetDir.isDirectory, "[x] Missing dataset directory: ${datasetDir.absolutePath}.")
        assertTrue(pokepediaTreeFile.isFile, "[x] Missing Pokepedia tree file: ${pokepediaTreeFile.absolutePath}.")

        val tempDir = createTempDirectory("tcgdex-e2e-tk-ex-m-").toFile()
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
                    "--recognition-vectors=${recognitionVectorsFile.absolutePath}",
                ),
            )

            assertTrue(outputDb.isFile, "[x] Database generation did not create ${outputDb.absolutePath}.")

            Class.forName("org.sqlite.JDBC")
            DriverManager.getConnection("jdbc:sqlite:${outputDb.absolutePath}").use { connection ->
                listOf(targetLanguage, englishLanguage).forEach { language ->
                    val charmRow = queryCardFallbackRow(connection, "tk-ex-m-2", language)
                    assertNotNull(charmRow, "[x] Expected a generated card row for tk-ex-m-2/$language.")
                    assertTrue(
                        charmRow.imageUrl.isNullOrBlank(),
                        "[x] Expected tk-ex-m-2/$language image_url to stay empty. got '${charmRow.imageUrl}'.",
                    )
                    assertEquals("pokepedia", charmRow.fallbackImageSource)
                    assertTrue(
                        containsNegapiToken(charmRow.fallbackImageUrl, "2"),
                        "[x] Expected tk-ex-m-2/$language fallback to contain Négapi_2. " +
                            "got '${charmRow.fallbackImageUrl}'.",
                    )
                    assertTrue(
                        charmRow.fallbackImageUrl?.contains("Posipi") != true,
                        "[x] Expected tk-ex-m-2/$language not to use Posipi art. " +
                            "got '${charmRow.fallbackImageUrl}'.",
                    )

                    val reptincelRow = queryCardFallbackRow(connection, "tk-ex-m-3", language)
                    assertNotNull(reptincelRow, "[x] Expected a generated card row for tk-ex-m-3/$language.")
                    assertTrue(
                        reptincelRow.imageUrl.isNullOrBlank(),
                        "[x] Expected tk-ex-m-3/$language image_url to stay empty. got '${reptincelRow.imageUrl}'.",
                    )
                    assertEquals("pokepedia", reptincelRow.fallbackImageSource)
                    assertTrue(
                        containsNegapiToken(reptincelRow.fallbackImageUrl, "3"),
                        "[x] Expected tk-ex-m-3/$language fallback to contain Négapi_3. " +
                            "got '${reptincelRow.fallbackImageUrl}'.",
                    )
                    assertTrue(
                        reptincelRow.fallbackImageUrl?.contains("Posipi") != true,
                        "[x] Expected tk-ex-m-3/$language not to use Posipi/Groret art. " +
                            "got '${reptincelRow.fallbackImageUrl}'.",
                    )

                    assertPokepediaInternationalFallback(
                        connection = connection,
                        dbPath = outputDb,
                        datasetDir = datasetDir,
                        cardmarketExportDir = cardmarketExportDir,
                        pokepediaTreeFile = pokepediaTreeFile,
                        cardId = "tk-ex-p-3",
                        language = language,
                        filename = "Posipi_3.png",
                    )
                }
            }
        } finally {
            tempDir.deleteRecursively()
        }
    }

    private fun containsNegapiToken(url: String?, localId: String): Boolean {
        val value = url.orEmpty()
        return value.contains("Négapi_$localId") || value.contains("N%C3%A9gapi_$localId")
    }

    private fun assertPokepediaInternationalFallback(
        connection: Connection,
        dbPath: File,
        datasetDir: File,
        cardmarketExportDir: File,
        pokepediaTreeFile: File,
        cardId: String,
        language: String,
        filename: String,
    ) {
        val row = queryCardFallbackRow(connection, cardId, language)
        if (row == null) {
            fail(
                buildDebugMessage(
                    dbPath = dbPath,
                    datasetDir = datasetDir,
                    cardmarketExportDir = cardmarketExportDir,
                    pokepediaTreeFile = pokepediaTreeFile,
                    cardId = cardId,
                    language = language,
                    row = null,
                    availableLanguages = queryAvailableLanguagesForCard(connection, cardId),
                ),
            )
        }
        assertNotNull(row)
        val debugMessage = buildDebugMessage(
            dbPath = dbPath,
            datasetDir = datasetDir,
            cardmarketExportDir = cardmarketExportDir,
            pokepediaTreeFile = pokepediaTreeFile,
            cardId = cardId,
            language = language,
            row = row,
            availableLanguages = listOf(englishLanguage, targetLanguage),
        )
        assertTrue(
            row.imageUrl.isNullOrBlank(),
            "[x] Expected $cardId/$language image_url to stay empty (no CDN asset).\n$debugMessage",
        )
        assertTrue(
            row.fallbackImageUrl?.contains(filename) == true,
            "[x] Expected $cardId/$language Pokepedia fallback to contain $filename.\n$debugMessage",
        )
        assertEquals(
            "pokepedia",
            row.fallbackImageSource,
            "[x] Expected $cardId/$language fallback_image_source to equal 'pokepedia'.\n$debugMessage",
        )
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

    private fun queryCardFallbackRow(connection: Connection, cardId: String, language: String): GeneratedCardRow? {
        connection.prepareStatement(
            """
            SELECT
              id,
              language,
              name,
              rarity_id,
              image_url,
              fallback_image_url,
              fallback_image_source
            FROM cards
            WHERE id = ?
              AND language = ?
            """.trimIndent(),
        ).use { statement ->
            statement.setString(1, cardId)
            statement.setString(2, language)
            statement.executeQuery().use { resultSet ->
                if (!resultSet.next()) {
                    return null
                }
                return GeneratedCardRow(
                    id = resultSet.getString("id"),
                    language = resultSet.getString("language"),
                    name = resultSet.getString("name"),
                    rarityId = resultSet.getString("rarity_id"),
                    imageUrl = resultSet.getString("image_url"),
                    fallbackImageUrl = resultSet.getString("fallback_image_url"),
                    fallbackImageSource = resultSet.getString("fallback_image_source"),
                    priceTrend = null,
                    priceAvg = null,
                    priceLow = null,
                    setLogoUrl = null,
                    setSymbolUrl = null,
                    detailedPriceRows = 0,
                )
            }
        }
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
              symbol_url,
              abbreviation_official,
              parent_set_id
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
                    abbreviationOfficial = resultSet.getString("abbreviation_official"),
                    parentSetId = resultSet.getString("parent_set_id"),
                )
            }
        }
    }

    private fun queryCameoRowCount(connection: Connection, language: String): Int {
        connection.prepareStatement(
            """
            SELECT COUNT(*) AS row_count
            FROM card_pokemon
            WHERE is_cameo = 1
              AND language = ?
            """.trimIndent(),
        ).use { statement ->
            statement.setString(1, language)
            statement.executeQuery().use { resultSet ->
                return if (resultSet.next()) resultSet.getInt("row_count") else 0
            }
        }
    }

    private fun queryRecognitionRowsForCard(connection: Connection, cardId: String, language: String): Int {
        connection.prepareStatement(
            """
            SELECT COUNT(*) AS row_count
            FROM card_recognition_hashes
            WHERE card_id = ?
              AND language = ?
            """.trimIndent(),
        ).use { statement ->
            statement.setString(1, cardId)
            statement.setString(2, language)
            statement.executeQuery().use { resultSet ->
                return if (resultSet.next()) resultSet.getInt("row_count") else 0
            }
        }
    }

    private fun queryRecognitionSourcesForCard(
        connection: Connection,
        cardId: String,
        language: String,
    ): Set<String> {
        connection.prepareStatement(
            """
            SELECT DISTINCT image_source
            FROM card_recognition_hashes
            WHERE card_id = ?
              AND language = ?
            """.trimIndent(),
        ).use { statement ->
            statement.setString(1, cardId)
            statement.setString(2, language)
            statement.executeQuery().use { resultSet ->
                val sources = mutableSetOf<String>()
                while (resultSet.next()) {
                    sources += resultSet.getString("image_source")
                }
                return sources
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
        val abbreviationOfficial: String?,
        val parentSetId: String?,
    )

    private data class TargetCardExpectation(
        val id: String,
    )
}
