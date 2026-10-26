package app.cardium.kmptcgdexsdk.generator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

class CardCatalogSlotIngestionTest {
    @Test
    fun `Given detailed variants When ingested Then canonical print and stable legacy id are chosen`() {
        val card =
            Json
                .parseToJsonElement(
                    """
                    {
                      "variants_detailed": [
                        {"type":"holo","size":"standard","stamp":["1st-edition"],"variantId":"generated"},
                        {"type":"normal","size":"standard","variantId":"normal-stable"}
                      ]
                    }
                    """.trimIndent(),
                ).jsonObject

        val slots =
            ingestCatalogSlots(
                card = card,
                englishVariants = null,
                cardImageUrl = "https://assets.tcgdex.net/en/card",
            )

        assertEquals("normal-stable", slots.first { it.isCanonical }.variantId)
        assertTrue(slots.any { it.variantId.startsWith("legacy-holo-") })
        assertEquals(0, slots.first { it.stampSlugs.contains("1st-edition") }.displayRank / 1_000_000)
    }

    @Test
    fun `Given a known missing stamp When supplemented Then the print is added once`() {
        val card =
            Json
                .parseToJsonElement(
                    """{"variants_detailed":[{"type":"normal","size":"standard","variantId":"base"}]}""",
                ).jsonObject
        val slots =
            ingestCatalogSlots(
                card = card,
                englishVariants = null,
                supplements =
                    listOf(
                        CatalogSlotSupplement(
                            cardId = "xy2-80",
                            variantId = "supplement-build-abear",
                            typeSlug = "normal",
                            stampSlugs = listOf("build-abear"),
                            label = "Build Abear Stamp",
                        ),
                    ),
            )

        assertEquals(1, slots.count { it.variantId == "supplement-build-abear" })
        assertEquals("Build Abear Stamp", slots.first { it.variantId == "supplement-build-abear" }.typeLabel)
    }

    @Test
    fun `Given a card id whose last dotted segment starts with a letter When verifying Then the build fails`() {
        assertFailsWith<IllegalStateException> {
            verifyPrintCodeInvariants(cardId = "sv08.a-001", slots = listOf(sampleSlot()))
        }
    }

    @Test
    fun `Given two slots with the same descriptor When verifying Then the build fails`() {
        val slot = sampleSlot()
        assertFailsWith<IllegalStateException> {
            verifyPrintCodeInvariants(cardId = "base2-11", slots = listOf(slot, slot.copy(variantId = "other")))
        }
    }

    @Test
    fun `Given two canonical slots When verifying Then the build fails`() {
        assertFailsWith<IllegalStateException> {
            verifyPrintCodeInvariants(
                cardId = "base2-11",
                slots =
                    listOf(
                        sampleSlot(variantId = "normal"),
                        sampleSlot(variantId = "other", typeSlug = "holo"),
                    ),
            )
        }
    }

    @Test
    fun `Given a dotted set id and distinct slots When verifying Then the build passes`() {
        verifyPrintCodeInvariants(
            cardId = "sv08.5-001",
            slots =
                listOf(
                    sampleSlot(isCanonical = true),
                    sampleSlot(variantId = "reverse", isCanonical = false, typeSlug = "reverse"),
                ),
        )
    }

    private fun sampleSlot(
        variantId: String = "normal",
        isCanonical: Boolean = true,
        typeSlug: String = "normal",
    ): IngestedCatalogSlot =
        IngestedCatalogSlot(
            variantId = variantId,
            isCanonical = isCanonical,
            sortIndex = 0,
            displayRank = 0,
            typeSlug = typeSlug,
            subtypeSlug = null,
            sizeSlug = "standard",
            foilSlug = null,
            stampSlugs = emptyList(),
            typeLabel = typeSlug,
            subtypeLabel = null,
            sizeLabel = "standard",
            foilLabel = null,
            stampLabels = emptyList(),
            imageUrl = null,
            cardmarketProductId = null,
            tcgplayerProductId = null,
        )
}
