package app.cardium.tcgdex.sdk.util

import kotlin.test.Test
import kotlin.test.assertEquals

class ImageUrlUtilsTest {
    @Test
    fun `Given a PokeBeach picture file When quality URLs are built Then the file URL is unchanged`() {
        val jpg = "https://www.pokebeach.com/news/2026/10/ME06_EN_53_webp.jpg"
        val png = "https://www.pokebeach.com/news/2026/08/ME06_EN_11.png"
        val webp = "https://www.pokebeach.com/news/2026/09/card.webp"
        val avif = "https://www.pokebeach.com/news/2026/10/ME06_EN_94-1.avif"
        for (url in listOf(jpg, png, webp, avif)) {
            assertEquals(url, toThumbnailUrl(url))
            assertEquals(url, toHighQualityUrl(url))
        }
    }

    @Test
    fun `Given a TCGdex CDN base When quality URLs are built Then low and high suffixes are appended`() {
        val base = "https://assets.tcgdex.net/en/me/me06/001"
        assertEquals("$base/low.png", toThumbnailUrl(base))
        assertEquals("$base/high.png", toHighQualityUrl(base))
    }
}
