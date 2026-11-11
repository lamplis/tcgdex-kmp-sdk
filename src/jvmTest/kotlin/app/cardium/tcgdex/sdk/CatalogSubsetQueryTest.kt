package app.cardium.tcgdex.sdk

import app.cardium.tcgdex.db.TcgdexDatabase
import app.cardium.tcgdex.db.TcgdexQueries
import app.cash.sqldelight.async.coroutines.await
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

class CatalogSubsetQueryTest {
    @Test
    fun `given a child set when reading the parent then the child is folded into the parent`() =
        runBlocking {
            val dbPath = Files.createTempFile("tcgdex-subset-test", ".db")
            val driver = JdbcSqliteDriver("jdbc:sqlite:${dbPath.toAbsolutePath()}")
            try {
                TcgdexDatabase.Schema.create(driver).await()
                val database = TcgdexDatabase(driver)
                seedParentAndChild(database)
                val repository = DefaultTcgdexRepository(database)

                val sets = repository.getAllSets("en")
                val parent = sets.single { it.id == "base" }
                assertEquals(12, parent.cardCountTotal)
                assertTrue(sets.none { it.id == "basecc" })

                val cards = repository.getCardsForSet("base", "en")
                assertEquals(listOf("base-001", "basecc-CC1"), cards.map { it.id })
                assertTrue(cards.all { it.setId == "base" })

                assertTrue(repository.getCardsForSet("basecc", "en").isEmpty())
            } finally {
                runCatching { driver.close() }
                runCatching { Files.deleteIfExists(dbPath) }
            }
        }

    private suspend fun seedParentAndChild(database: TcgdexDatabase) {
        val queries = database.tcgdexQueries
        queries.insertSerie(id = "test", language = "en", name = "Test", position = 1)
        queries.insertSet(
            id = "base",
            language = "en",
            serieId = "test",
            name = "Base",
            logoUrl = null,
            symbolUrl = null,
            cardCountTotal = 10,
            cardCountOfficial = 10,
            releaseDate = "1999-01-09",
            abbreviationOfficial = null,
            parentSetId = null,
        )
        queries.insertSet(
            id = "basecc",
            language = "en",
            serieId = "test",
            name = "Base Classic",
            logoUrl = null,
            symbolUrl = null,
            cardCountTotal = 2,
            cardCountOfficial = 2,
            releaseDate = "1999-01-09",
            abbreviationOfficial = null,
            parentSetId = "base",
        )
        insertCard(queries, id = "base-001", localId = "001", setId = "base")
        insertCard(queries, id = "basecc-CC1", localId = "CC1", setId = "basecc")
    }

    private suspend fun insertCard(
        queries: TcgdexQueries,
        id: String,
        localId: String,
        setId: String,
    ) {
        queries.insertCard(
            id = id,
            language = "en",
            localId = localId,
            localNumberSort = 1,
            setId = setId,
            rarityId = null,
            illustratorId = null,
            name = id,
            imageUrl = null,
            fallbackImageUrl = null,
            fallbackImageSource = null,
            originLanguage = "en",
            category = "Pokemon",
            types = null,
            supertype = null,
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
