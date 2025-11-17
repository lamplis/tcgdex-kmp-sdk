package app.cardium.tcgdex.sdk.embedded

import android.content.Context

@Suppress("EXPECTED_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object EmbeddedResourceLoader {
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    actual fun readText(path: String): String? {
        val ctx = appContext ?: return null
        // Try multiple path variants to cope with packaging differences across plugin versions
        val candidates = buildList {
            add(path)
            // compose-resources vs composeResources
            if (path.contains("composeResources/")) add(path.replace("composeResources/", "compose-resources/"))
            if (path.contains("compose-resources/")) add(path.replace("compose-resources/", "composeResources/"))
            // Inject generated resources package segment after composeResources root
            if (path.startsWith("composeResources/")) add(path.replace("composeResources/", "composeResources/cardium.composeapp.generated.resources/"))
            if (path.startsWith("compose-resources/")) add(path.replace("compose-resources/", "compose-resources/cardium.composeapp.generated.resources/"))
            // Some builds place files directly under composeResources without the 'files/' segment
            if (path.contains("composeResources/files/")) add(path.replace("composeResources/files/", "composeResources/"))
            if (path.contains("compose-resources/files/")) add(path.replace("compose-resources/files/", "compose-resources/"))
            // Drop composeResources prefix (assets are already rooted at /assets)
            if (path.startsWith("composeResources/")) add(path.removePrefix("composeResources/"))
            if (path.startsWith("compose-resources/")) add(path.removePrefix("compose-resources/"))
            // Drop composeResources/files prefix variants
            if (path.startsWith("composeResources/files/")) add(path.removePrefix("composeResources/files/"))
            if (path.startsWith("compose-resources/files/")) add(path.removePrefix("compose-resources/files/"))
            // Some Android packagers nest under compose-resources/main/…
            val withoutComposePrefix =
                when {
                    path.startsWith("composeResources/") -> path.removePrefix("composeResources/")
                    path.startsWith("compose-resources/") -> path.removePrefix("compose-resources/")
                    else -> null
                }
            if (withoutComposePrefix != null) {
                add("compose-resources/main/$withoutComposePrefix")
                add("composeResources/main/$withoutComposePrefix")
                // Also try commonMain scope used by Compose resources
                add("compose-resources/commonMain/$withoutComposePrefix")
                add("composeResources/commonMain/$withoutComposePrefix")
                // With generated resources package segment
                add("compose-resources/cardium.composeapp.generated.resources/$withoutComposePrefix")
                add("composeResources/cardium.composeapp.generated.resources/$withoutComposePrefix")
                add("compose-resources/main/cardium.composeapp.generated.resources/$withoutComposePrefix")
                add("composeResources/main/cardium.composeapp.generated.resources/$withoutComposePrefix")
                add("compose-resources/commonMain/cardium.composeapp.generated.resources/$withoutComposePrefix")
                add("composeResources/commonMain/cardium.composeapp.generated.resources/$withoutComposePrefix")
                // Some builds scope resources per variant (e.g., debug)
                add("compose-resources/debug/$withoutComposePrefix")
                add("composeResources/debug/$withoutComposePrefix")
                // Release variant as well (in case running release build)
                add("compose-resources/release/$withoutComposePrefix")
                add("composeResources/release/$withoutComposePrefix")
            }
            // Compute without 'composeResources/files/' prefix and try main/commonMain as well
            val withoutComposeFilesPrefix =
                when {
                    path.startsWith("composeResources/files/") -> path.removePrefix("composeResources/files/")
                    path.startsWith("compose-resources/files/") -> path.removePrefix("compose-resources/files/")
                    else -> null
                }
            if (withoutComposeFilesPrefix != null) {
                add("compose-resources/$withoutComposeFilesPrefix")
                add("composeResources/$withoutComposeFilesPrefix")
                add("compose-resources/main/$withoutComposeFilesPrefix")
                add("composeResources/main/$withoutComposeFilesPrefix")
                add("compose-resources/commonMain/$withoutComposeFilesPrefix")
                add("composeResources/commonMain/$withoutComposeFilesPrefix")
                // With generated resources package segment
                add("compose-resources/cardium.composeapp.generated.resources/$withoutComposeFilesPrefix")
                add("composeResources/cardium.composeapp.generated.resources/$withoutComposeFilesPrefix")
                add("compose-resources/main/cardium.composeapp.generated.resources/$withoutComposeFilesPrefix")
                add("composeResources/main/cardium.composeapp.generated.resources/$withoutComposeFilesPrefix")
                add("compose-resources/commonMain/cardium.composeapp.generated.resources/$withoutComposeFilesPrefix")
                add("composeResources/commonMain/cardium.composeapp.generated.resources/$withoutComposeFilesPrefix")
            }
        }.distinct()

        var lastError: Throwable? = null
        for (p in candidates) {
            val result = runCatching {
                ctx.assets.open(p).bufferedReader().use { it.readText() }
            }
            if (result.isSuccess) {
                println("[OK] EmbeddedResourceLoader: loaded asset '$p' (from '${path}')")
                return result.getOrNull()
            } else {
                lastError = result.exceptionOrNull()
            }
        }
        println("[!] EmbeddedResourceLoader: asset not found path='$path' tried=${candidates}")
        lastError?.let { println("[i] EmbeddedResourceLoader:last error=${it.message}") }
        return null
    }
}


