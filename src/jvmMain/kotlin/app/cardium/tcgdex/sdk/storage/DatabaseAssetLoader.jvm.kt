package app.cardium.tcgdex.sdk.storage

internal actual fun loadDatabaseAsset(): ByteArray {
    val stream = TcgdexDatabaseInstaller::class.java.classLoader?.getResourceAsStream(TcgdexDatabaseInstaller.DATABASE_FILE_NAME)
        ?: error("Unable to locate embedded database asset: ${TcgdexDatabaseInstaller.DATABASE_FILE_NAME}")
    return stream.use { it.readBytes() }
}

