package app.cardium.tcgdex.sdk

import app.cardium.tcgdex.db.TcgdexDatabase
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultTcgdexRepositoryPriceUpdateTest {
    @Test
    fun `Given cards with embedded price timestamps, When querying latest price update, Then most recent ISO is returned`() = runBlocking {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        TcgdexDatabase.Schema.create(driver)
        val database = TcgdexDatabase(driver)
        val queries = database.tcgdexQueries

        queries.insertSerie(
            id = "sv",
            language = "en",
            name = "Scarlet & Violet",
            position = 0,
        )
        queries.insertSet(
            id = "sv01",
            language = "en",
            serieId = "sv",
            name = "Base",
            logoUrl = null,
            symbolUrl = null,
            cardCountTotal = 198,
            cardCountOfficial = 198,
            releaseDate = "2023-03-31",
        )
        queries.insertCard(
            id = "sv01-001",
            language = "en",
            localId = "001",
            localNumberSort = 1,
            setId = "sv01",
            rarityId = null,
            illustratorId = null,
            name = "Sprigatito",
            imageUrl = null,
            fallbackImageUrl = null,
            fallbackImageSource = null,
            originLanguage = "en",
            category = "Pokemon",
            types = "Grass",
            supertype = "Pokemon",
            regulationMark = "G",
            priceCardmarketTrend = 1.2,
            priceCardmarketAvg = null,
            priceCardmarketLow = null,
            priceUpdatedIso = "2026-02-14T09:00:00Z",
            priceUnit = "EUR",
        )
        queries.insertCard(
            id = "sv01-002",
            language = "en",
            localId = "002",
            localNumberSort = 2,
            setId = "sv01",
            rarityId = null,
            illustratorId = null,
            name = "Floragato",
            imageUrl = null,
            fallbackImageUrl = null,
            fallbackImageSource = null,
            originLanguage = "en",
            category = "Pokemon",
            types = "Grass",
            supertype = "Pokemon",
            regulationMark = "G",
            priceCardmarketTrend = 3.4,
            priceCardmarketAvg = null,
            priceCardmarketLow = null,
            priceUpdatedIso = "2026-03-20T18:45:00Z",
            priceUnit = "EUR",
        )

        val repository = DefaultTcgdexRepository(database)

        assertEquals("2026-03-20T18:45:00Z", repository.getLatestPriceUpdateIso())
        driver.close()
    }

    @Test
    fun `Given no cards with embedded price timestamps, When querying latest price update, Then null is returned`() = runBlocking {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        TcgdexDatabase.Schema.create(driver)
        val repository = DefaultTcgdexRepository(TcgdexDatabase(driver))

        assertNull(repository.getLatestPriceUpdateIso())
        driver.close()
    }
}
