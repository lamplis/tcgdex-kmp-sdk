package app.cardium.tcgdex.sdk

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.cardium.tcgdex.db.TcgdexDatabase
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDatabaseType
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDriver
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun newInMemoryDriver() = AndroidxSqliteDriver(
    driver = BundledSQLiteDriver(),
    databaseType = AndroidxSqliteDatabaseType.Memory,
    schema = TcgdexDatabase.Schema,
)

class DefaultTcgdexRepositoryPriceUpdateTest {
    @Test
    fun `Given cards with embedded price timestamps, When querying latest price update, Then most recent ISO is returned`() = runBlocking {
        val driver = newInMemoryDriver()
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
        val driver = newInMemoryDriver()
        val repository = DefaultTcgdexRepository(TcgdexDatabase(driver))

        assertNull(repository.getLatestPriceUpdateIso())
        driver.close()
    }

    @Test
    fun `Given per-condition rows in card_prices, When getCardPricesForCard is called, Then every condition row is returned verbatim`() = runBlocking {
        // Reproduces me02.5-276 FR: the database stores each Cardmarket condition bucket as
        // its own row (NM=0, MT=650) plus the GLOBAL price-guide baseline. The repository must
        // return all of them; it must not collapse or fallback between tiers.
        val driver = newInMemoryDriver()
        val database = TcgdexDatabase(driver)
        database.seedCardFk("me02.5-276")
        database.insertPriceRow("me02.5-276", variant = "V1", priceLanguage = "fr", sellerCountry = "FR", condition = "NM", recommendedPrice = 0.0, avgPrice = 443.0, minPrice = 0.0)
        database.insertPriceRow("me02.5-276", variant = "V1", priceLanguage = "fr", sellerCountry = "FR", condition = "MT", recommendedPrice = 650.0, avgPrice = 700.0, minPrice = 600.0)
        database.insertPriceRow("me02.5-276", variant = "Normal", priceLanguage = "", sellerCountry = "GLOBAL", condition = "", recommendedPrice = 730.67, avgPrice = 569.93, minPrice = 430.0)

        val repository = DefaultTcgdexRepository(database)

        val rows = repository.getCardPricesForCard("me02.5-276", language = "fr", sellerCountry = "FR")
        assertEquals(3, rows.size, "expected NM + MT + GLOBAL rows, got $rows")

        val nm = rows.single { it.sellerCountry == "FR" && it.condition == "NM" }
        val mt = rows.single { it.sellerCountry == "FR" && it.condition == "MT" }
        val global = rows.single { it.sellerCountry == "GLOBAL" }

        assertEquals(0.0, nm.recommendedPrice)
        assertEquals(443.0, nm.avgPrice)
        assertEquals(650.0, mt.recommendedPrice)
        assertEquals(730.67, global.recommendedPrice)
        assertEquals("", global.condition)

        driver.close()
    }

    @Test
    fun `Given multiple cards, When getCardPricesForCards is called, Then rows are grouped per card id`() = runBlocking {
        val driver = newInMemoryDriver()
        val database = TcgdexDatabase(driver)
        database.seedCardFk("me02-127")
        database.seedCardFk("me02.5-276")
        database.insertPriceRow("me02-127", variant = "V3", priceLanguage = "fr", sellerCountry = "FR", condition = "NM", recommendedPrice = 40.0)
        database.insertPriceRow("me02.5-276", variant = "V1", priceLanguage = "fr", sellerCountry = "FR", condition = "NM", recommendedPrice = 0.0, avgPrice = 443.0)
        database.insertPriceRow("me02.5-276", variant = "V1", priceLanguage = "fr", sellerCountry = "FR", condition = "MT", recommendedPrice = 650.0)

        val repository = DefaultTcgdexRepository(database)
        val grouped = repository.getCardPricesForCards(
            listOf("me02-127", "me02.5-276", "absent-999"),
            language = "fr",
            sellerCountry = "FR",
        )

        assertTrue(grouped.containsKey("me02-127"))
        assertEquals(1, grouped.getValue("me02-127").size)
        assertEquals(40.0, grouped.getValue("me02-127").single().recommendedPrice)

        val multi = grouped.getValue("me02.5-276")
        assertEquals(2, multi.size)
        assertEquals(setOf("NM", "MT"), multi.map { it.condition }.toSet())

        assertNull(grouped["absent-999"], "absent cards must not appear in the output map")

        driver.close()
    }
}

/**
 * Minimum FK chain (serie/set/card) required before a card_prices row can be inserted.
 */
private suspend fun TcgdexDatabase.seedCardFk(cardId: String) {
    val setId = cardId.substringBeforeLast('-').ifEmpty { "tset" }
    val localId = cardId.substringAfterLast('-').ifEmpty { "001" }
    tcgdexQueries.insertSerie(id = "tserie", language = "fr", name = "Test Serie", position = 0)
    tcgdexQueries.insertSet(
        id = setId,
        language = "fr",
        serieId = "tserie",
        name = "Test Set",
        logoUrl = null,
        symbolUrl = null,
        cardCountTotal = 100,
        cardCountOfficial = 100,
        releaseDate = "2026-01-01",
    )
    tcgdexQueries.insertCard(
        id = cardId,
        language = "fr",
        localId = localId,
        localNumberSort = 0,
        setId = setId,
        rarityId = null,
        illustratorId = null,
        name = "Test $cardId",
        imageUrl = null,
        fallbackImageUrl = null,
        fallbackImageSource = null,
        originLanguage = "fr",
        category = "Pokemon",
        types = null,
        supertype = null,
        regulationMark = null,
        priceCardmarketTrend = null,
        priceCardmarketAvg = null,
        priceCardmarketLow = null,
        priceUpdatedIso = null,
        priceUnit = "EUR",
    )
}

private suspend fun TcgdexDatabase.insertPriceRow(
    cardId: String,
    variant: String,
    priceLanguage: String,
    sellerCountry: String,
    condition: String,
    recommendedPrice: Double? = null,
    avgPrice: Double? = null,
    minPrice: Double? = null,
    medianPrice: Double? = null,
    maxPrice: Double? = null,
) {
    tcgdexQueries.insertCardPrice(
        cardId = cardId,
        cardLanguage = "fr",
        variant = variant,
        priceLanguage = priceLanguage,
        sellerCountry = sellerCountry,
        condition = condition,
        currency = "EUR",
        minPrice = minPrice,
        avgPrice = avgPrice,
        medianPrice = medianPrice,
        maxPrice = maxPrice,
        recommendedPrice = recommendedPrice,
        availableCount = null,
        productId = null,
        updatedIso = "2026-04-22T21:40:21Z",
    )
}
