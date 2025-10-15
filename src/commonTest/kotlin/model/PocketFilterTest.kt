package app.cardium.tcgdex.sdk.model

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PocketFilterTest {
    @Test
    fun isPocket_bySeriesId_trueForTcgp() {
        assertTrue(PocketFilter.isPocket(seriesId = "tcgp", numberOrRef = null))
        assertTrue(PocketFilter.isPocket(seriesId = "TCGP", numberOrRef = null))
    }

    @Test
    fun isPocket_byRefPattern_trueForPocketStyleRef() {
        assertTrue(PocketFilter.isPocket(seriesId = null, numberOrRef = "P-A-071/000"))
        assertTrue(PocketFilter.isPocket(seriesId = null, numberOrRef = "p-b-123/000"))
    }

    @Test
    fun isPocket_falseForRegularCards() {
        assertFalse(PocketFilter.isPocket(seriesId = "swsh", numberOrRef = "136/203"))
        assertFalse(PocketFilter.isPocket(seriesId = null, numberOrRef = "136/203"))
        assertFalse(PocketFilter.isPocket(seriesId = null, numberOrRef = null))
    }

    @Test
    fun isPocket_cardAndSet_helpers() {
        val pocketSet = TcgdxSet(id = "tcgp1", name = "Pocket Set", serie = TcgdxSerie(id = "tcgp", name = "Pocket"))
        val regularSet = TcgdxSet(id = "swsh3", name = "Darkness Ablaze", serie = TcgdxSerie(id = "swsh", name = "Sword & Shield"))

        val pocketCard = TcgdxCard(id = "tcgp1-071", name = "Pocket", set = pocketSet, number = "P-A-071/000")
        val regularCard = TcgdxCard(id = "swsh3-136", name = "Furret", set = regularSet, number = "136/203")

        assertTrue(PocketFilter.isPocket(pocketSet))
        assertTrue(PocketFilter.isPocket(pocketCard))
        assertFalse(PocketFilter.isPocket(regularSet))
        assertFalse(PocketFilter.isPocket(regularCard))
    }
}


