@file:Suppress("ktlint:standard:filename")

package app.cardium.kmptcgdexsdk.generator

import java.io.File

/**
 * Missing images index generator for Pokecardex fallback.
 *
 * Generates a CSV file listing cards with missing images and their Pokecardex fallback URLs.
 * This is called during database generation to create a build-time index.
 *
 * CSV format:
 * language,card_id,set_id,local_id,tcgdex_url,pokecardex_url
 */
object MissingImagesIndexGenerator {

    private const val BASE_URL = "https://pokecardex.b-cdn.net/assets/images/sets"

    /**
     * TCGdex to Pokecardex set ID mapping.
     * Must be kept in sync with PokecardexFallbackResolver in shared module.
     */
    private val setMapping: Map<String, String> = mapOf(
        // Mega Evolution (2025)
        "sv09" to "PFL",
        "sv09pt5" to "MEG",
        
        // Scarlet & Violet era
        "sv08pt5" to "PRE",
        "sv08" to "SSP",
        "sv07" to "SCR",
        "sv06pt5" to "SFA",
        "sv06" to "TWM",
        "sv05" to "TEF",
        "sv04pt5" to "PAF",
        "sv04" to "PAR",
        "sv03pt5" to "MEW",
        "sv03" to "OBF",
        "sv02" to "PAL",
        "sv01" to "SVI",
        "sve" to "SVE",
        "svp" to "SVP",
        
        // Sword & Shield era
        "swsh12pt5" to "CRZ",
        "swsh12" to "SIT",
        "swsh11" to "LOR",
        "swsh10pt5" to "PGO",
        "swsh10" to "ASR",
        "swsh9" to "BRS",
        "swsh8" to "FST",
        "swsh7" to "EVS",
        "swsh6" to "CRE",
        "swsh5" to "SWSH5",
        "swsh45" to "SWSH45",
        "swsh4" to "SWSH4",
        "swsh35" to "SWSH35",
        "swsh3" to "SWSH3",
        "swsh2" to "SWSH2",
        "swsh1" to "SWSH1",
        "swshp" to "PRSWSH",
        "cel25" to "CEL",
        
        // Sun & Moon era
        "sm12" to "SM12",
        "sm115" to "SM115",
        "sm11" to "SM11",
        "sm10" to "SM10",
        "sm9" to "SM09",
        "sm8" to "SM08",
        "sm75" to "SM75",
        "sm7" to "SM07",
        "sm6" to "SM06",
        "sm5" to "SM05",
        "sm4" to "SM04",
        "sm35" to "SLE",
        "sm3" to "SM03",
        "sm2" to "SM02",
        "sm1" to "SM01",
        "smp" to "PRSM",
        
        // XY era
        "xy12" to "EVO",
        "xy11" to "STS",
        "xy10" to "FAC",
        "g1" to "GNR",
        "xy9" to "BKP",
        "xy8" to "BKT",
        "xy7" to "AOR",
        "xy6" to "ROS",
        "dc1" to "DCR",
        "xy5" to "PRC",
        "xy4" to "PHF",
        "xy3" to "FFI",
        "xy2" to "FLF",
        "xy1" to "XY",
        "xyp" to "PRXY",
        
        // Black & White era
        "bw11" to "LTR",
        "bw10" to "PLB",
        "bw9" to "PLF",
        "bw8" to "PLS",
        "bw7" to "BCR",
        "bw6" to "DRX",
        "bw5" to "DEX",
        "bw4" to "NXD",
        "bw3" to "NVI",
        "bw2" to "EPO",
        "bw1" to "BLW",
        "bwp" to "PRBW",
        
        // HeartGold SoulSilver era
        "col1" to "CL",
        "hgss4" to "TM",
        "hgss3" to "UD",
        "hgss2" to "UL",
        "hgss1" to "HGSS",
        "hgssp" to "PRHS",
        
        // Platinum era
        "pl4" to "AR",
        "pl3" to "SV",
        "pl2" to "RR",
        "pl1" to "PT",
        
        // Diamond & Pearl era
        "dp7" to "SF",
        "dp6" to "LA",
        "dp5" to "MD",
        "dp4" to "GE",
        "dp3" to "SW",
        "dp2" to "MT",
        "dp1" to "DP",
        "dpp" to "PRDP",
        
        // EX era
        "ex16" to "PK",
        "ex15" to "DF",
        "ex14" to "CG",
        "ex13" to "HP",
        "ex12" to "LM",
        "ex11" to "DS",
        "ex10" to "UF",
        "ex9" to "EM",
        "ex8" to "DX",
        "ex7" to "TRR",
        "ex6" to "RFVF",
        "ex5" to "HL",
        "ex4" to "TMTA",
        "ex3" to "DR",
        "ex2" to "SS",
        "ex1" to "RS",
        "np" to "PRNI",
        
        // Wizards era
        "ecard3" to "SK",
        "ecard2" to "AQ",
        "ecard1" to "EX",
        "lc" to "LC",
        "neo4" to "N4",
        "neo3" to "NR",
        "neo2" to "ND",
        "neo1" to "NG",
        "gym2" to "GC",
        "gym1" to "GH",
        "tr" to "TR",
        "base6" to "BS2",
        "base3" to "FO",
        "base2" to "JU",
        "base1" to "BS",
        "basep" to "PRWC",
        
        // Special sets
        "mcd19" to "MC9",
        "mcd21" to "MC10US",
        "mcd22" to "MC11US",
        "mcd23" to "M23",
        "mcd24" to "M24",
        "det1" to "DPK",
    )

    /**
     * Data class representing a missing image entry.
     */
    data class MissingImageEntry(
        val language: String,
        val cardId: String,
        val setId: String,
        val localId: String,
        val tcgdexUrl: String?,
        val pokecardexUrl: String?,
    )

    /**
     * Build Pokecardex URL for a card.
     *
     * @param setId TCGdex set ID
     * @param localId Card local ID
     * @param language Language code
     * @param hd Whether to use HD quality
     * @return Pokecardex URL or null if set not mapped
     */
    fun buildPokecardexUrl(setId: String, localId: String, language: String, hd: Boolean = false): String? {
        val pokecardexCode = setMapping[setId.lowercase()] ?: return null
        val cardNumber = extractCardNumber(localId) ?: return null
        
        val langPrefix = if (language.lowercase() == "fr") "" else "US/"
        val hdPath = if (hd) "HD/" else ""
        
        return "$BASE_URL/$pokecardexCode/$langPrefix$hdPath$cardNumber.jpg"
    }

    /**
     * Extract numeric card number from localId.
     */
    private fun extractCardNumber(localId: String): Int? {
        val digits = localId.filter { it.isDigit() }
        return digits.toIntOrNull()
    }

    /**
     * Check if a set has Pokecardex mapping.
     */
    fun hasMapping(setId: String): Boolean = setMapping.containsKey(setId.lowercase())

    /**
     * Generate the missing images CSV file.
     *
     * @param entries List of missing image entries
     * @param outputDir Directory to write the CSV file
     * @return The generated CSV file
     */
    fun generateCsv(entries: List<MissingImageEntry>, outputDir: File): File {
        val missingDir = File(outputDir, "MISSING")
        missingDir.mkdirs()
        
        val csvFile = File(missingDir, "missing_images.csv")
        
        csvFile.bufferedWriter().use { writer ->
            // Write header
            writer.write("language,card_id,set_id,local_id,tcgdex_url,pokecardex_url")
            writer.newLine()
            
            // Write entries
            for (entry in entries.sortedWith(compareBy({ it.language }, { it.setId }, { it.localId }))) {
                val line = listOf(
                    entry.language,
                    entry.cardId,
                    entry.setId,
                    entry.localId,
                    entry.tcgdexUrl ?: "",
                    entry.pokecardexUrl ?: "",
                ).joinToString(",") { escapeCsvField(it) }
                writer.write(line)
                writer.newLine()
            }
        }
        
        println("[Tcgdex] Generated missing images index: ${csvFile.absolutePath}")
        println("[Tcgdex]   Total entries: ${entries.size}")
        
        return csvFile
    }

    /**
     * Escape a CSV field value.
     */
    private fun escapeCsvField(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }
}

