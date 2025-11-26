package app.cardium.tcgdex.sdk

import app.cardium.tcgdex.db.TcgdexDatabase
import app.cardium.tcgdex.sdk.storage.TcgdexDatabaseInstaller
import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okio.FileSystem
import okio.FileSystem.Companion.SYSTEM
import okio.Path

/**
 * Entry point for the offline TCGdex SDK.
 *
 * This SDK provides 100% offline access to TCGdex card data through a pre-populated
 * SQLite database. The database is generated at build time from the TCGdex JSON dataset
 * and bundled with the application.
 *
 * ## Usage
 * ```kotlin
 * // 1. Install the bundled database (typically done once at app startup)
 * val dbPath = context.getDatabasePath("tcgdex.db").toPath()
 * TcgdexSdk.installBundledDatabase(dbPath)
 *
 * // 2. Create a driver (platform-specific)
 * val driver = createSqliteDriver(dbPath)
 *
 * // 3. Create the repository
 * val repository = TcgdexSdk.createRepository(driver)
 *
 * // 4. Query data
 * val series = repository.getAllSeries("en")
 * val cards = repository.getCardsForSet("sv01", "en")
 * ```
 *
 * ## Multi-Language Support
 * The database contains data for all languages configured at build time.
 * Pass the desired language code to query methods to get localized data.
 *
 * ## Thread Safety
 * The repository is thread-safe and can be used from any coroutine context.
 *
 * ## Offline Guarantee
 * All data queries are served from the local database. No network calls are made.
 * Card images are loaded separately by the app using the URLs from the database.
 *
 * @see TcgdexRepository for available query methods
 * @see TcgdexDatabaseInstaller for database installation details
 */
object TcgdexSdk {

    /**
     * Creates a [TcgdexRepository] instance backed by the provided SQLite driver.
     *
     * The driver should point to a database that was installed via [installBundledDatabase]
     * or created by the build-time generator.
     *
     * @param driver SQLDelight driver connected to the TCGdex database
     * @param dispatcher Coroutine dispatcher for query execution (default: [Dispatchers.Default])
     * @return A repository instance for querying TCGdex data
     */
    fun createRepository(
        driver: SqlDriver,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): TcgdexRepository = DefaultTcgdexRepository(
        database = TcgdexDatabase(driver),
        dispatcher = dispatcher
    )

    /**
     * Installs the bundled TCGdex database to the specified destination.
     *
     * This method copies the pre-populated database from the app's resources
     * to a writable location where SQLite can access it.
     *
     * The installation is skipped if:
     * - The destination file already exists with the same size
     * - [force] is false
     *
     * @param destination Path where the database should be installed
     * @param fileSystem File system to use for I/O (default: system file system)
     * @param force If true, always reinstall even if the file exists
     * @return true if the database was installed, false if it was already up-to-date
     */
    fun installBundledDatabase(
        destination: Path,
        fileSystem: FileSystem = SYSTEM,
        force: Boolean = false
    ): Boolean = TcgdexDatabaseInstaller.installIfNeeded(
        fileSystem = fileSystem,
        destination = destination,
        force = force
    )

    /**
     * Default supported languages included in the database.
     *
     * This list reflects the default build configuration. The actual languages
     * available depend on the build-time `tcgdex.languages` property.
     */
    val DEFAULT_LANGUAGES = listOf("en", "fr")

    /**
     * The database filename used by the SDK.
     */
    const val DATABASE_FILENAME = TcgdexDatabaseInstaller.DATABASE_FILE_NAME
}
