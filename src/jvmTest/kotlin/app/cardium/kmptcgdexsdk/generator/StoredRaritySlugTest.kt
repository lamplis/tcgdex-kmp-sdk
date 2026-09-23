package app.cardium.kmptcgdexsdk.generator

import kotlin.test.Test
import kotlin.test.assertEquals

class StoredRaritySlugTest {
    @Test
    fun `Given rare-holo-lv-x, When canonicalizing, Then holo-rare-lv-x`() {
        assertEquals(
            "holo-rare-lv-x",
            StoredRaritySlug.canonicalize("rare-holo-lv-x", "pl4-94", emptySet(), emptySet()),
        )
    }

    @Test
    fun `Given full-art-trainer, When canonicalizing, Then ultra-rare`() {
        assertEquals(
            "ultra-rare",
            StoredRaritySlug.canonicalize("full-art-trainer", "swsh12tg-TG23", emptySet(), emptySet()),
        )
    }

    @Test
    fun `Given black-star-promo, When canonicalizing, Then promo`() {
        assertEquals(
            "promo",
            StoredRaritySlug.canonicalize("black-star-promo", "swshp-SWSH001", emptySet(), emptySet()),
        )
    }

    @Test
    fun `Given Charizard GX ultra-rare, When canonicalizing, Then ultra-rare is unchanged`() {
        assertEquals(
            "ultra-rare",
            StoredRaritySlug.canonicalize("ultra-rare", "sm3-20", emptySet(), emptySet()),
        )
    }

    @Test
    fun `Given Secret Rare BREAK, When canonicalizing, Then secret-rare is unchanged`() {
        assertEquals(
            "secret-rare",
            StoredRaritySlug.canonicalize("secret-rare", "xy8-113", emptySet(), emptySet()),
        )
    }

    @Test
    fun `Given ultra-rare in break set, When canonicalizing, Then rare-break`() {
        assertEquals(
            "rare-break",
            StoredRaritySlug.canonicalize("ultra-rare", "xy8-12", setOf("xy8-12"), emptySet()),
        )
    }

    @Test
    fun `Given rare in shining set, When canonicalizing, Then rare-shining`() {
        assertEquals(
            "rare-shining",
            StoredRaritySlug.canonicalize("rare", "neo4-109", emptySet(), setOf("neo4-109")),
        )
    }

    @Test
    fun `Given rare-holo-gx, When canonicalizing, Then holo-rare-gx`() {
        assertEquals(
            "holo-rare-gx",
            StoredRaritySlug.canonicalize("rare-holo-gx", "sm1-1", emptySet(), emptySet()),
        )
    }
}
