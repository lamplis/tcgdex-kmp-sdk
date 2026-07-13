package app.cardium.tcgdex.sdk

import app.cardium.tcgdex.db.TcgdexDatabase
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.sqldelight.async.coroutines.await
import java.nio.file.Files
import java.nio.file.Path
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

class DefaultTcgdexRepositoryQueryFallbackTest {
    @Test
    fun `given FR request when set metadata is EN-only then cards and set stay visible without FR set clone`() =
        runBlocking {
            val fixture = openRepositoryFixture()
            try {
                val frCard = fixture.repository.getCardById("bw11-RC22", "fr")
                assertNotNull(frCard)
                assertEquals("bw11", frCard.setId)
                assertEquals("fr", frCard.setLanguage)
                assertEquals("en", frCard.originLanguage)
                assertEquals("Legendary Treasures", frCard.setName)
                assertTrue(frCard.setName.isNotBlank())

                val frCards = fixture.repository.getCardsForSet("bw11", "fr")
                val enCards = fixture.repository.getCardsForSet("bw11", "en")
                assertTrue(frCards.any { it.id == "bw11-RC22" })
                assertEquals(enCards.size, frCards.size, "FR set should expose the full EN card list through fallback joins")

                val frSets = fixture.repository.getAllSets("fr")
                assertTrue(frSets.any { it.id == "bw11" }, "bw11 should be visible in FR set queries through EN fallback")

                DriverManager.getConnection("jdbc:sqlite:${fixture.databasePath.toAbsolutePath()}").use { connection ->
                    connection.createStatement().use { statement ->
                        statement.executeQuery("SELECT COUNT(*) FROM sets WHERE id = 'bw11' AND language = 'fr'").use { result ->
                            assertTrue(result.next())
                            assertEquals(
                                0,
                                result.getInt(1),
                                "Fallback fix must not duplicate metadata by inserting a French bw11 set row",
                            )
                        }
                    }
                }
            } finally {
                runCatching { fixture.driver.close() }
                runCatching { Files.deleteIfExists(fixture.databasePath) }
            }
        }

    private data class RepositoryFixture(
        val repository: DefaultTcgdexRepository,
        val driver: JdbcSqliteDriver,
        val databasePath: Path,
    )

    private suspend fun openRepositoryFixture(): RepositoryFixture {
        val dbPath = Files.createTempFile("tcgdex-fallback-test", ".db")
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbPath.toAbsolutePath()}")
        TcgdexDatabase.Schema.create(driver).await()
        val database = TcgdexDatabase(driver)
        seedMinimalFallbackFixture(database)
        val repository = DefaultTcgdexRepository(database)
        return RepositoryFixture(
            repository = repository,
            driver = driver,
            databasePath = dbPath,
        )
    }

    private suspend fun seedMinimalFallbackFixture(database: TcgdexDatabase) {
        val queries = database.tcgdexQueries

        queries.insertSerie(
            id = "bw",
            language = "en",
            name = "Black & White",
            position = 1,
        )
        queries.insertSerie(
            id = "bw",
            language = "fr",
            name = "Noir & Blanc",
            position = 1,
        )

        // Keep set metadata EN-only to verify query fallback (no FR clone row).
        queries.insertSet(
            id = "bw11",
            language = "en",
            serieId = "bw",
            name = "Legendary Treasures",
            logoUrl = null,
            symbolUrl = null,
            cardCountTotal = 140,
            cardCountOfficial = 113,
            releaseDate = "2013-11-06",
        )

        queries.insertCard(
            id = "bw11-RC22",
            language = "en",
            localId = "RC22",
            localNumberSort = 22,
            setId = "bw11",
            rarityId = null,
            illustratorId = null,
            name = "Reshiram",
            imageUrl = "https://assets.tcgdex.net/en/bw/bw11/RC22",
            fallbackImageUrl = null,
            fallbackImageSource = null,
            originLanguage = "en",
            category = "Pokemon",
            types = "Fire",
            supertype = "Basic",
            regulationMark = null,
            hp = null,
            priceCardmarketTrend = null,
            priceCardmarketAvg = null,
            priceCardmarketLow = null,
            priceUpdatedIso = null,
            priceUnit = null,
        )

        queries.insertCard(
            id = "bw11-RC22",
            language = "fr",
            localId = "RC22",
            localNumberSort = 22,
            setId = "bw11",
            rarityId = null,
            illustratorId = null,
            name = "Reshiram",
            imageUrl = "https://assets.tcgdex.net/en/bw/bw11/RC22",
            fallbackImageUrl = null,
            fallbackImageSource = null,
            originLanguage = "en",
            category = "Pokemon",
            types = "Fire",
            supertype = "Basic",
            regulationMark = null,
            hp = null,
            priceCardmarketTrend = null,
            priceCardmarketAvg = null,
            priceCardmarketLow = null,
            priceUpdatedIso = null,
            priceUnit = null,
        )
    }
}
