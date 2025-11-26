package app.cardium.tcgdex.sdk.storage

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.dataWithContentsOfFile
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
internal actual fun loadDatabaseAsset(): ByteArray {
    val fileManager = NSFileManager.defaultManager
    val bundleCandidates =
        listOfNotNull(
            NSBundle.bundleWithIdentifier("app.cardium.ComposeApp")?.also {
                println("[Tcgdex][i] Probing ComposeApp bundle: ${it.bundlePath}")
            },
            NSBundle.mainBundle.also {
                println("[Tcgdex][i] Probing main app bundle: ${it.bundlePath}")
            },
        )

    bundleCandidates.forEach { bundle ->
        val candidate = bundle.pathForResource("tcgdex", "db")
        if (candidate != null) {
            println("[Tcgdex][i] Candidate resource path: $candidate")
            if (fileManager.fileExistsAtPath(candidate)) {
                return candidate.readBytes()
            } else {
                println("[Tcgdex][!] Resource path missing on disk: $candidate")
            }
        } else {
            println("[Tcgdex][i] Bundle ${bundle.bundlePath} does not contain tcgdex.db")
        }
    }

    val fallbackPaths = listOf(
        NSBundle.mainBundle.bundlePath + "/tcgdex.db",
        NSBundle.mainBundle.bundlePath + "/compose-resources/tcgdex.db",
    )
    fallbackPaths.forEach { path ->
        if (fileManager.fileExistsAtPath(path)) {
            println("[Tcgdex][i] Using fallback tcgdex.db at $path")
            return path.readBytes()
        } else {
            println("[Tcgdex][i] Fallback candidate not found: $path")
        }
    }

    error("Unable to find tcgdex.db in application bundle or ComposeApp framework resources")
}

@OptIn(ExperimentalForeignApi::class)
private fun String.readBytes(): ByteArray {
    val data = NSData.dataWithContentsOfFile(this)
        ?: error("Unable to read tcgdex.db from $this")
    val length = data.length.toInt()
    val bytes = ByteArray(length)
    memScoped {
        bytes.usePinned {
            memcpy(it.addressOf(0), data.bytes, data.length)
        }
    }
    println("[Tcgdex][i] Loaded tcgdex.db ($length bytes) from $this")
    return bytes
}

