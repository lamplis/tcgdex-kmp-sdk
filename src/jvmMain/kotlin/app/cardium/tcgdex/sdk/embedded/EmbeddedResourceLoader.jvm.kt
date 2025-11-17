package app.cardium.tcgdex.sdk.embedded

@Suppress("EXPECTED_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object EmbeddedResourceLoader {
    actual fun readText(path: String): String? {
        // Try with and without leading slash and composeResources prefix
        val candidates = listOf(
            path,
            "/$path",
            path.removePrefix("composeResources/"),
            "/${path.removePrefix("composeResources/")}",
        ).distinct()
        var lastError: Throwable? = null
        for (p in candidates) {
            try {
                val stream =
                    EmbeddedResourceLoader::class.java.getResourceAsStream(p)
                        ?: Thread.currentThread().contextClassLoader?.getResourceAsStream(p)
            if (stream != null) {
                    val text = stream.bufferedReader().use { it.readText() }
                    println("[OK] EmbeddedResourceLoader(JVM): loaded resource '$p' (from '$path')")
                    return text
                }
            } catch (t: Throwable) {
                lastError = t
            }
        }
        println("[!] EmbeddedResourceLoader(JVM): resource not found path='$path' tried=$candidates")
        lastError?.let { println("[i] EmbeddedResourceLoader(JVM): last error=${it.message}") }
        return null
    }
}


