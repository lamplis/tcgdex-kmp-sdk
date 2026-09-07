package app.cardium.kmptcgdexsdk.generator

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MfbFrenchLocalizationTest {
    @Test
    fun `Given mfb localIds, When looking up French names, Then known ids map and unknown is null`() {
        assertEquals("Bulbizarre", frenchNameForMfbLocalId("1"))
        assertEquals("Échange", frenchNameForMfbLocalId("34"))
        assertEquals("Salamèche", frenchNameForMfbLocalId(" 9 "))
        assertNull(frenchNameForMfbLocalId("99"))
        assertNull(frenchNameForMfbLocalId("  "))
    }

    @Test
    fun `Given an English mfb card json, When localizing, Then name is replaced`() {
        val card =
            JsonObject(
                mapOf(
                    "id" to JsonPrimitive("mfb-1"),
                    "localId" to JsonPrimitive("1"),
                    "name" to JsonPrimitive("Bulbasaur"),
                ),
            )
        val localized = localizeMfbCardJson(card)
        assertEquals("Bulbizarre", localized["name"]?.toString()?.trim('"'))
        assertEquals("mfb-1", localized["id"]?.toString()?.trim('"'))
    }
}
