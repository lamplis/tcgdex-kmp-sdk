package app.cardium.tcgdex.sdk

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.cardium.tcgdex.db.TcgdexDatabase
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDatabaseType
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDriver
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private fun newRecognitionInMemoryDriver() = AndroidxSqliteDriver(
    driver = BundledSQLiteDriver(),
    databaseType = AndroidxSqliteDatabaseType.Memory,
    schema = TcgdexDatabase.Schema,
)

class DefaultTcgdexRepositoryRecognitionTest {
    @Test
    fun `Given card recognition rows, When querying one card, Then all hash columns are mapped`() = runBlocking {
        val driver = newRecognitionInMemoryDriver()
        val database = TcgdexDatabase(driver)
        seedRecognitionFixtures(database)

        val repository = DefaultTcgdexRepository(database)
        val rows = repository.getRecognitionHashesForCard("sv01-001", "fr")

        assertEquals(1, rows.size)
        val row = rows.single()
        assertEquals("sv01-001", row.cardId)
        assertEquals("fr", row.language)
        assertEquals("tcgdex", row.imageSource)
        assertEquals("original", row.lighting)
        assertEquals(0, row.rotation)
        assertEquals(64, row.dhash.length)
        assertEquals(64, row.phash.length)
        driver.close()
    }

    @Test
    fun `Given mixed recognition rows, When querying the corpus, Then all categories are returned and tcgp is excluded`() = runBlocking {
        val driver = newRecognitionInMemoryDriver()
        val database = TcgdexDatabase(driver)
        seedRecognitionFixtures(database)

        val repository = DefaultTcgdexRepository(database)
        val rows = repository.getRecognitionHashesForPokemon("fr")

        // The corpus intentionally covers all card categories (Pokemon + Trainer/Energy);
        // only TCG Pocket rows are excluded. See getCardRecognitionHashesForPokemon.
        assertEquals(4, rows.size)
        assertEquals(setOf("tcgdex", "pokepedia", "cardmarket"), rows.map { it.imageSource }.toSet())
        assertTrue(rows.any { it.cardId == "sv01-100" }, "trainer cards are part of the corpus")
        assertFalse(rows.any { it.cardId == "tcgp-001" }, "tcgp cards must be excluded")
        assertTrue(rows.all { it.cardId in setOf("sv01-001", "sv01-002", "sv02-003", "sv01-100") })
        driver.close()
    }
}

private suspend fun seedRecognitionFixtures(database: TcgdexDatabase) {
    val queries = database.tcgdexQueries
    queries.insertSerie(id = "sv", language = "fr", name = "Scarlet & Violet", position = 0)
    queries.insertSerie(id = "tcgp", language = "fr", name = "Pocket", position = 1)

    queries.insertSet(
        id = "sv01",
        language = "fr",
        serieId = "sv",
        name = "Base",
        logoUrl = null,
        symbolUrl = null,
        cardCountTotal = 198,
        cardCountOfficial = 198,
        releaseDate = "2023-03-31",
        abbreviationOfficial = null,
        parentSetId = null,
    )
    queries.insertSet(
        id = "sv02",
        language = "fr",
        serieId = "sv",
        name = "Paldea",
        logoUrl = null,
        symbolUrl = null,
        cardCountTotal = 193,
        cardCountOfficial = 193,
        releaseDate = "2023-06-09",
        abbreviationOfficial = null,
        parentSetId = null,
    )
    queries.insertSet(
        id = "tcgp1",
        language = "fr",
        serieId = "tcgp",
        name = "Pocket Set",
        logoUrl = null,
        symbolUrl = null,
        cardCountTotal = 100,
        cardCountOfficial = 100,
        releaseDate = "2025-01-01",
        abbreviationOfficial = null,
        parentSetId = null,
    )

    suspend fun insertCard(id: String, setId: String, category: String) {
        queries.insertCard(
            id = id,
            language = "fr",
            localId = id.substringAfterLast('-'),
            localNumberSort = 1,
            setId = setId,
            rarityId = null,
            illustratorId = null,
            name = id,
            imageUrl = null,
            fallbackImageUrl = null,
            fallbackImageSource = null,
            originLanguage = "fr",
            category = category,
            types = null,
            supertype = null,
            regulationMark = null,
            hp = null,
            priceCardmarketTrend = null,
            priceCardmarketAvg = null,
            priceCardmarketLow = null,
            priceUpdatedIso = null,
            priceUnit = "EUR",
        )
    }

    insertCard("sv01-001", "sv01", "Pokemon")
    insertCard("sv01-002", "sv01", "Pokemon")
    insertCard("sv02-003", "sv02", "Pokemon")
    insertCard("sv01-100", "sv01", "Trainer")
    insertCard("tcgp-001", "tcgp1", "Pokemon")

    suspend fun insertHash(cardId: String, source: String) {
        queries.insertCardRecognitionHash(
            cardId = cardId,
            language = "fr",
            imageSource = source,
            imageUrl = "https://example.invalid/$cardId.png",
            lighting = "original",
            rotation = 0,
            dhash = "A".repeat(64),
            phash = "B".repeat(64),
        )
    }

    insertHash("sv01-001", "tcgdex")
    insertHash("sv01-002", "pokepedia")
    insertHash("sv02-003", "cardmarket")
    insertHash("sv01-100", "tcgdex")
    insertHash("tcgp-001", "tcgdex")
}
