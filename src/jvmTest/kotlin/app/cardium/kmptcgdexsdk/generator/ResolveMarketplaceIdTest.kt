package app.cardium.kmptcgdexsdk.generator

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ResolveMarketplaceIdTest {
    private val json = Json { ignoreUnknownKeys = true }

    private fun card(jsonString: String): JsonObject =
        json.parseToJsonElement(jsonString.trimIndent()).jsonObject

    @Test
    fun `Given root-only thirdParty cardmarket, When resolved, Then returns root ID`() {
        val card = card("""
            {
              "id": "sv01-001",
              "thirdParty": { "cardmarket": 100, "tcgplayer": 200 },
              "variants": { "normal": true, "reverse": true }
            }
        """)
        assertEquals(100, resolveMarketplaceId(card, "cardmarket"))
    }

    @Test
    fun `Given variant-normal thirdParty cardmarket only, When resolved, Then returns normal variant ID`() {
        val card = card("""
            {
              "id": "me01-001",
              "thirdParty": { "tcgplayer": 654340 },
              "variants": { "normal": true, "reverse": true },
              "variants_detailed": [
                { "type": "normal", "thirdParty": { "cardmarket": 851072 } },
                { "type": "reverse", "thirdParty": { "cardmarket": 851072 } }
              ]
            }
        """)
        assertEquals(851072, resolveMarketplaceId(card, "cardmarket"))
    }

    @Test
    fun `Given both root and variant cardmarket, When resolved, Then normal variant takes precedence`() {
        val card = card("""
            {
              "id": "dual-001",
              "thirdParty": { "cardmarket": 111, "tcgplayer": 222 },
              "variants_detailed": [
                { "type": "normal", "thirdParty": { "cardmarket": 999 } },
                { "type": "reverse", "thirdParty": { "cardmarket": 999 } }
              ]
            }
        """)
        assertEquals(999, resolveMarketplaceId(card, "cardmarket"))
    }

    @Test
    fun `Given only non-normal variant cardmarket, When resolved, Then falls back to first variant`() {
        val card = card("""
            {
              "id": "special-001",
              "thirdParty": { "tcgplayer": 300 },
              "variants_detailed": [
                { "type": "holo", "thirdParty": { "cardmarket": 555 } },
                { "type": "reverse", "thirdParty": { "cardmarket": 666 } }
              ]
            }
        """)
        assertEquals(555, resolveMarketplaceId(card, "cardmarket"))
    }

    @Test
    fun `Given no thirdParty cardmarket anywhere, When resolved, Then returns null`() {
        val card = card("""
            {
              "id": "nomarket-001",
              "thirdParty": { "tcgplayer": 400 },
              "variants_detailed": [
                { "type": "normal", "thirdParty": { "tcgplayer": 500 } }
              ]
            }
        """)
        assertNull(resolveMarketplaceId(card, "cardmarket"))
    }

    @Test
    fun `Given no thirdParty at all, When resolved, Then returns null`() {
        val card = card("""
            {
              "id": "bare-001",
              "variants": { "normal": true }
            }
        """)
        assertNull(resolveMarketplaceId(card, "cardmarket"))
    }

    @Test
    fun `Given variant thirdParty with null cardmarket, When resolved, Then falls back to root`() {
        val card = card("""
            {
              "id": "nullvariant-001",
              "thirdParty": { "cardmarket": 777 },
              "variants_detailed": [
                { "type": "normal", "thirdParty": { "cardmarket": null } }
              ]
            }
        """)
        assertEquals(777, resolveMarketplaceId(card, "cardmarket"))
    }

    @Test
    fun `Given tcgplayer marketplace, When resolved, Then returns tcgplayer ID from root`() {
        val card = card("""
            {
              "id": "tcgp-001",
              "thirdParty": { "tcgplayer": 654340 },
              "variants_detailed": [
                { "type": "normal", "thirdParty": { "cardmarket": 851072 } }
              ]
            }
        """)
        assertEquals(654340, resolveMarketplaceId(card, "tcgplayer"))
    }

    @Test
    fun `Given variants_detailed is empty array, When resolved, Then falls back to root`() {
        val card = card("""
            {
              "id": "emptyvar-001",
              "thirdParty": { "cardmarket": 888 },
              "variants_detailed": []
            }
        """)
        assertEquals(888, resolveMarketplaceId(card, "cardmarket"))
    }

    @Test
    fun `Regression ME1 - variant-only cardmarket feeds S3 price guide lookup`() {
        val priceGuide = mapOf(
            851072 to CardmarketPrice(
                trendPrice = 0.05,
                averageSellPrice = 0.04,
                lowPrice = 0.02,
                updatedIso = "2026-04-19T00:00:00Z",
            ),
        )

        val card = card("""
            {
              "id": "me01-001",
              "thirdParty": { "tcgplayer": 654340 },
              "variants": { "normal": true, "reverse": true },
              "variants_detailed": [
                { "type": "normal", "thirdParty": { "cardmarket": 851072 } },
                { "type": "reverse", "thirdParty": { "cardmarket": 851072 } }
              ]
            }
        """)

        val resolvedId = resolveMarketplaceId(card, "cardmarket")
        assertNotNull(resolvedId, "Variant-only cardmarket ID should be resolved")
        assertEquals(851072, resolvedId)

        val pricing = resolvedId.let { priceGuide[it] }
        assertNotNull(pricing, "S3 price guide should match the resolved variant ID")
        assertEquals(0.05, pricing.trendPrice)
        assertEquals(0.04, pricing.averageSellPrice)
        assertEquals(0.02, pricing.lowPrice)
    }
}
