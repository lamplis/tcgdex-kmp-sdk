package app.cardium.kmptcgdexsdk.generator

import app.cash.sqldelight.async.coroutines.await
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private val cameoDexIdsInSource = Regex("""cameoDexIds\s*:\s*\[[^\]]*\d""")

private val compiledCardsJson = Json { ignoreUnknownKeys = true }

internal fun cardsDatabaseDataDir(datasetDir: File): File =
    datasetDir.parentFile.parentFile.resolve("data")

internal fun countSourceCameoCards(dataDir: File): Int {
    if (!dataDir.isDirectory) return 0
    return dataDir.walkTopDown()
        .onEnter { dir -> dir.name != "node_modules" }
        .filter { it.isFile && it.extension == "ts" }
        .count { file -> cameoDexIdsInSource.containsMatchIn(file.readText()) }
}

internal fun countCompiledCameoCards(cardsFile: File): Int {
    check(cardsFile.isFile) {
        "[Tcgdex][x] Missing compiled cards file: ${cardsFile.absolutePath}"
    }
    val array = compiledCardsJson.parseToJsonElement(cardsFile.readText()).jsonArray
    return array.count { element ->
        val cameos = element.jsonObject["cameoDexIds"] as? JsonArray ?: return@count false
        cameos.any { it.jsonPrimitive.intOrNull != null }
    }
}

internal fun assertCameoJsonMatchesSource(
    datasetDir: File,
    languages: List<String>,
) {
    val sourceCount = countSourceCameoCards(cardsDatabaseDataDir(datasetDir))
    check(sourceCount > 0) {
        "[Tcgdex][x] No cameoDexIds in libs/cards-database/data. Dataset lost cameo annotations."
    }
    for (language in languages) {
        val cardsFile = datasetDir.resolve(language).resolve("cards.json")
        val jsonCount = countCompiledCameoCards(cardsFile)
        if (language.equals("en", ignoreCase = true)) {
            check(jsonCount == sourceCount) {
                """
                [Tcgdex][x] CAMEO DEX IDS DROPPED DURING COMPILE
                language=$language sourceTs=$sourceCount compiledJson=$jsonCount
                file=${cardsFile.absolutePath}
                Fix: emit cameoDexIds in cardUtil.ts, then
                ./gradlew :libs:tcgdex-kmp-sdk:compileCardsDatabaseGenerated
                """.trimIndent()
            }
        } else {
            check(jsonCount > 0 && jsonCount <= sourceCount) {
                """
                [Tcgdex][x] CAMEO DEX IDS DROPPED DURING COMPILE
                language=$language sourceTs=$sourceCount compiledJson=$jsonCount
                file=${cardsFile.absolutePath}
                English must match the TypeScript source; other languages may have fewer
                cards if those prints are missing from the localized dataset.
                """.trimIndent()
            }
        }
    }
}

internal suspend fun assertCameoRowsPersisted(
    languages: List<String>,
    countCameoRowsForLanguage: suspend (String) -> Long,
) {
    for (language in languages) {
        val count = countCameoRowsForLanguage(language)
        check(count > 0L) {
            "[Tcgdex][x] No is_cameo=1 rows for language=$language after generation"
        }
    }
}

internal suspend fun countCameoRows(
    driver: SqlDriver,
    language: String,
): Long {
    return driver.executeQuery(
        identifier = null,
        sql = "SELECT COUNT(*) FROM card_pokemon WHERE is_cameo = 1 AND language = ?",
        mapper = { cursor ->
            QueryResult.AsyncValue {
                cursor.next().await()
                cursor.getLong(0) ?: 0L
            }
        },
        parameters = 1,
    ) {
        bindString(0, language)
    }.await()
}
