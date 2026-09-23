package app.cardium.kmptcgdexsdk.generator

import kotlin.test.Test
import kotlin.test.assertEquals

class VLineRaritySlugTest {
    @Test
    fun `Given Rayquaza VMAX ultra-rare, When canonicalizing, Then holo-rare-vmax`() {
        assertEquals("holo-rare-vmax", VLineRaritySlug.canonicalize("ultra-rare", "Rayquaza VMAX"))
    }

    @Test
    fun `Given Pikachu secret-rare, When canonicalizing, Then secret-rare is unchanged`() {
        assertEquals("secret-rare", VLineRaritySlug.canonicalize("secret-rare", "Pikachu"))
    }

    @Test
    fun `Given Elesa's Sparkle ultra-rare, When canonicalizing, Then ultra-rare is unchanged`() {
        assertEquals("ultra-rare", VLineRaritySlug.canonicalize("ultra-rare", "Elesa's Sparkle"))
    }

    @Test
    fun `Given Magnezone V holo-rare-vmax, When canonicalizing, Then holo-rare-v`() {
        assertEquals("holo-rare-v", VLineRaritySlug.canonicalize("holo-rare-vmax", "Magnezone V"))
    }

    @Test
    fun `Given Rotom V holo-rare-vmax, When canonicalizing, Then holo-rare-v`() {
        assertEquals("holo-rare-v", VLineRaritySlug.canonicalize("holo-rare-vmax", "Rotom V"))
    }

    @Test
    fun `Given Mewtwo VSTAR holo-rare-vmax, When canonicalizing, Then holo-rare-vstar`() {
        assertEquals("holo-rare-vstar", VLineRaritySlug.canonicalize("holo-rare-vmax", "Mewtwo VSTAR"))
    }

    @Test
    fun `Given Charizard VMAX secret-rare, When canonicalizing, Then secret-rare is unchanged`() {
        assertEquals("secret-rare", VLineRaritySlug.canonicalize("secret-rare", "Charizard VMAX"))
    }

    @Test
    fun `Given Centiskorch V shiny-rare-v, When canonicalizing, Then shiny-rare-v is unchanged`() {
        assertEquals("shiny-rare-v", VLineRaritySlug.canonicalize("shiny-rare-v", "Centiskorch V"))
    }

    @Test
    fun `Given swshp promo, When persisting, Then promo`() {
        assertEquals(
            "promo",
            VLineRaritySlug.persistRarityId("promo", "Zacian V", "swshp"),
        )
    }

    @Test
    fun `Given basep promo, When persisting, Then promo is unchanged`() {
        assertEquals("promo", VLineRaritySlug.persistRarityId("promo", "Pikachu", "basep"))
    }
}
