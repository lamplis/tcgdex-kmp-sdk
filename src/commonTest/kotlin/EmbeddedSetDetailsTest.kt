package app.cardium.tcgdex.sdk.embedded

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EmbeddedSetDetailsTest {
    @Serializable
    private data class EmbeddedCard(val id: String, val number: String, val imageBase: String? = null)
    @Serializable
    private data class EmbeddedSet(val id: String, val cards: List<EmbeddedCard> = emptyList())

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun swshp_cards_have_imageBase_and_exact_numbers() {
        val raw = EmbeddedCatalogData.setDetailsJson("fr", "swshp")
        assertNotNull(raw, "Missing embedded set details for fr/swshp")
        val details = json.decodeFromString(EmbeddedSet.serializer(), raw!!)
        // At least 1 known promo entry, with exact DB-style number (e.g., SWSH001)
        val first = details.cards.firstOrNull()
        assertNotNull(first, "No cards in embedded swshp")
        assertNotNull(first.imageBase, "imageBase is required")
        // Number must contain SWSH prefix for SWSH promos
        assertTrue(first.number.startsWith("SWSH") || first.number.first().isDigit(), "Unexpected promo number format: ${first.number}")
    }
}


