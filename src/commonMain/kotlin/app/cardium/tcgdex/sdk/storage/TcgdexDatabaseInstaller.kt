package app.cardium.tcgdex.sdk.storage

import okio.FileSystem
import okio.FileSystem.Companion.SYSTEM
import okio.Path

/**
 * Handles installation of the bundled TCGdex SQLite database.
 *
 * The database is generated at build time by [GenerateTcgdexDatabase] and bundled
 * with the application as a resource. This installer copies it to a writable location
 * where SQLite can access it.
 *
 * ## Platform-Specific Loading
 * The [loadDatabaseAsset] function is implemented differently per platform:
 * - **Android**: Loads from `assets/tcgdex.db`
 * - **iOS**: Loads from the app bundle (main bundle or framework bundle)
 * - **JVM**: Loads from classpath resources
 *
 * ## Installation Strategy
 * The installer uses a simple size-based check to determine if reinstallation is needed.
 * This avoids unnecessary I/O while ensuring the database is updated when a new version
 * is bundled.
 *
 * ## Thread Safety
 * Installation is not thread-safe. Callers should ensure this is called from a single
 * thread (typically at app startup) before creating any database connections.
 *
 * @see TcgdexSdk.installBundledDatabase for the public API
 */
object TcgdexDatabaseInstaller {
    /**
     * The filename used for the TCGdex database.
     * This should match the filename used in build-time generation.
     */
    const val DATABASE_FILE_NAME = "tcgdex.db"

    /**
     * Logical schema/data version of the bundled database.
     *
     * This version is stored in two places:
     * 1. `PRAGMA user_version` inside the SQLite file (set during generation)
     * 2. A sidecar metadata file (`tcgdex.db.meta`) stored next to the database
     *
     * The metadata file enables lightweight version checks before touching SQLite,
     * ensuring the bundled database is re-installed after schema changes even if
     * the file size stays the same.
     *
     * ## VERSION HISTORY
     * - Version 8: Fixed origin_language for McDonald's French cards (was incorrectly set to "en").
     *              Cards that exist in FR with Pokepedia fallback now correctly have origin_language="fr".
     * - Version 7: Added evolution chain data (evolves_from, evolves_to) to pokemon_species table.
     *              Evolution data sourced from PokeAPI via the Pokédex API.
     * - Version 6: Added Cardmarket EUR pricing columns (trend, avg, low, updated, unit)
     *              Pricing is embedded at build time from Cardmarket price guide.
     * - Version 5: All 9 languages for pokemon_species (de, en, es, fr, it, ja, ko, zh-cn, zh-tw)
     * - Version 1: Initial schema
     * - Version 2: Added card_pokemon junction table for multi-Pokémon cards
     * - Version 3: Added additional indexes
     * - Version 4: Added pokemon_species table for canonical Pokémon names
     *              (fixes TAG TEAM card names appearing in Pokédex list)
     *
     * ## IMPORTANT: After bumping this version, you MUST:
     * 1. Regenerate the database: `./gradlew :libs:tcgdex-kmp-sdk:generateTcgdexDatabase -Ptcgdex.force=true`
     * 2. Copy to iOS: `cp libs/tcgdex-kmp-sdk/build/generated/tcgdex/resources/tcgdex.db iosApp/iosApp/Resources/tcgdex.db`
     * 3. Copy to Android: `cp libs/tcgdex-kmp-sdk/build/generated/tcgdex/resources/tcgdex.db composeApp/src/androidMain/assets/tcgdex.db`
     * 4. Rebuild both platforms
     *
     * @see docs/POKEDEX_DATA_REMEDIATION.md for more details
     */
    const val DATABASE_USER_VERSION = 8

    private const val METADATA_SUFFIX = ".meta"

    /**
     * Installs the bundled database to the specified destination if needed.
     *
     * Installation is performed if:
     * - The destination file doesn't exist
     * - The destination file size differs from the bundled database size
     * - [force] is true
     *
     * @param fileSystem File system to use for I/O operations
     * @param destination Path where the database should be installed
     * @param force If true, always reinstall even if the file exists
     * @return true if the database was copied, false if it was already up-to-date
     */
    fun installIfNeeded(
        fileSystem: FileSystem = SYSTEM,
        destination: Path,
        force: Boolean = false
    ): Boolean {
        // Load the bundled database from platform-specific resources
        val bytes = loadDatabaseAsset()

        // Check if installation is needed based on file size
        val existingSize = fileSystem.metadataOrNull(destination)?.size?.toLong()
        val metadataPath = destination.parent?.resolve("${destination.name}$METADATA_SUFFIX")
        val metadataVersion = metadataPath?.let { path ->
            runCatching {
                fileSystem.metadataOrNull(path)?.let {
                    fileSystem.read(path) { readUtf8().trim().toInt() }
                }
            }.getOrNull()
        }
        val needsCopy =
            force ||
                existingSize != bytes.size.toLong() ||
                metadataVersion != DATABASE_USER_VERSION

        println("[Tcgdex][i] installIfNeeded dest=$destination force=$force existingSize=$existingSize targetSize=${bytes.size} metadata=$metadataVersion needsCopy=$needsCopy")

        if (!needsCopy) return false

        // Ensure parent directory exists
        destination.parent?.let { parent ->
            if (!fileSystem.exists(parent)) {
                fileSystem.createDirectories(parent, mustCreate = false)
            }
        }

        // Copy the database to the destination
        fileSystem.write(destination) { write(bytes) }
        println("[Tcgdex][i] Wrote ${bytes.size} bytes to $destination")

        // Persist the logical version alongside the database
        metadataPath?.let { path ->
            fileSystem.write(path) {
                writeUtf8(DATABASE_USER_VERSION.toString())
            }
            println("[Tcgdex][i] Wrote metadata version ${DATABASE_USER_VERSION} to $path")
        }
        return true
    }
}

/**
 * Loads the bundled TCGdex database from platform-specific resources.
 *
 * This is an expect function with actual implementations per platform:
 * - **Android**: Reads from `context.assets.open("tcgdex.db")`
 * - **iOS**: Reads from `NSBundle.mainBundle` or framework bundle
 * - **JVM**: Reads from classpath resources
 *
 * @return The database file contents as a byte array
 * @throws IllegalStateException if the database cannot be found
 */
internal expect fun loadDatabaseAsset(): ByteArray

