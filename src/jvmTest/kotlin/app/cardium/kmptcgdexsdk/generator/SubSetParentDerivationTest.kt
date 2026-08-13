package app.cardium.kmptcgdexsdk.generator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class SubSetParentDerivationTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `Given CEL CC with parent CEL present, When deriving, Then parent is cel25`() {
        val result = deriveSubSetParents(
            sets(
                """{"id":"cel25","abbreviation":{"official":"CEL"}}""",
                """{"id":"cel25cc","abbreviation":{"official":"CEL:CC"}}""",
            ),
        )
        assertEquals(mapOf("cel25cc" to "cel25"), result)
    }

    @Test
    fun `Given colon official with no matching parent, When deriving, Then throws`() {
        assertFailsWith<IllegalStateException> {
            deriveSubSetParents(
                sets("""{"id":"cel25cc","abbreviation":{"official":"CEL:CC"}}"""),
            )
        }
    }

    @Test
    fun `Given two sets sharing the parent official, When deriving, Then throws`() {
        assertFailsWith<IllegalStateException> {
            deriveSubSetParents(
                sets(
                    """{"id":"cel25","abbreviation":{"official":"CEL"}}""",
                    """{"id":"cel25b","abbreviation":{"official":"CEL"}}""",
                    """{"id":"cel25cc","abbreviation":{"official":"CEL:CC"}}""",
                ),
            )
        }
    }

    @Test
    fun `Given parent id that is not a prefix of the sub-set id, When deriving, Then throws`() {
        assertFailsWith<IllegalStateException> {
            deriveSubSetParents(
                sets(
                    """{"id":"swsh10","abbreviation":{"official":"CEL"}}""",
                    """{"id":"cel25cc","abbreviation":{"official":"CEL:CC"}}""",
                ),
            )
        }
    }

    @Test
    fun `Given non-colon officials, When deriving, Then no links`() {
        val result = deriveSubSetParents(
            sets(
                """{"id":"sma","abbreviation":{"official":"SV"}}""",
                """{"id":"cel25","abbreviation":{"official":"CEL"}}""",
            ),
        )
        assertEquals(emptyMap(), result)
    }

    @Test
    fun `Given Hidden Fates vault with HIF SV official, When deriving, Then parent is sm115`() {
        val result = deriveSubSetParents(
            sets(
                """{"id":"sm115","abbreviation":{"official":"HIF"}}""",
                """{"id":"sm115sv","abbreviation":{"official":"HIF:SV"}}""",
            ),
        )
        assertEquals(mapOf("sm115sv" to "sm115"), result)
    }

    private fun sets(vararg bodies: String): List<JsonObject> =
        bodies.map { json.parseToJsonElement(it).jsonObject }
}
