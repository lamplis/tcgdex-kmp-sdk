package app.cardium.tcgdex.sdk.embedded

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile

@OptIn(ExperimentalForeignApi::class)
@Suppress("EXPECTED_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object EmbeddedResourceLoader {
    actual fun readText(path: String): String? {
        return runCatching {
            // Path format: "composeResources/.../file.json"
            val lastSlash = path.lastIndexOf('/')
            val nameWithExt = if (lastSlash >= 0) path.substring(lastSlash + 1) else path
            val dot = nameWithExt.lastIndexOf('.')
            if (dot <= 0 || dot >= nameWithExt.length - 1) {
                println("[!] EmbeddedResourceLoader(iOS): invalid path='$path' (missing extension)")
                return null
            }
            val name = nameWithExt.substring(0, dot)
            val ext = nameWithExt.substring(dot + 1)
            val dir = if (lastSlash > 0) path.substring(0, lastSlash) else ""
            // NSBundle expects resource name + type + directory
            val bundle = NSBundle.mainBundle
            val fullPath = bundle.pathForResource(name, ext, dir)
            if (fullPath == null) {
                println("[!] EmbeddedResourceLoader(iOS): asset not found name='$name' ext='$ext' dir='$dir' (from '$path')")
                return null
            }
            val text = NSString.stringWithContentsOfFile(
                path = fullPath,
                encoding = NSUTF8StringEncoding,
                error = null,
            ) as? String
            if (text != null) {
                println("[OK] EmbeddedResourceLoader(iOS): loaded '$dir/$name.$ext'")
            } else {
                println("[!] EmbeddedResourceLoader(iOS): failed reading '$dir/$name.$ext'")
            }
            text
        }.getOrNull()
    }
}


