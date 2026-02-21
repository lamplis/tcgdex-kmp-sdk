package app.cardium.tcgdex.sdk.storage

import okio.FileSystem

internal actual fun platformFileSystem(): FileSystem = FileSystem.SYSTEM

