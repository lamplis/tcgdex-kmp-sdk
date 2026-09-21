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
}
