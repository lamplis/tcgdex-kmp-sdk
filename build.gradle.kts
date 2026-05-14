import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

ktlint {
    ignoreFailures.set(true)
    filter {
        exclude("**/build/**")
        exclude("**/generated/**")
    }
}

// -----------------------------------------------------------------------------
// TCGdex Database Generation Configuration
// -----------------------------------------------------------------------------
// Languages to include in the offline database. Comma-separated list.
// Default: "en,fr" (English and French)
// Override via: ./gradlew ... -Ptcgdex.languages=en,fr,de,es
val tcgdexLanguages = providers.gradleProperty("tcgdex.languages").orElse("en,fr")
// Legacy single-language property (deprecated, use tcgdex.languages instead)
val tcgdexLanguageLegacy = providers.gradleProperty("tcgdex.language").orElse("")
// Force regeneration even if inputs haven't changed
val tcgdexForce = providers.gradleProperty("tcgdex.force").map { it.toBoolean() }.orElse(false)
val releaseRequested = gradle.startParameter.taskNames.any { it.contains("Release", ignoreCase = true) }
val cardsDatabaseRootDir = rootProject.layout.projectDirectory.dir("libs/cards-database")
val cardsDatabaseServerDir = cardsDatabaseRootDir.dir("server")
val cardsDatabaseCompilerDir = cardsDatabaseServerDir.dir("compiler")
val cardsDatabaseDataDir = cardsDatabaseRootDir.dir("data")
val cardsDatabaseAsiaDataDir = cardsDatabaseRootDir.dir("data-asia")
val cardsDatabaseMetaDir = cardsDatabaseRootDir.dir("meta")
val datasetRootDir = rootProject.layout.projectDirectory.dir("libs/cards-database/server/generated")
val generatedDbDir = layout.buildDirectory.dir("generated/tcgdex/resources")
val generatedDbFile = generatedDbDir.map { it.file("tcgdex.db") }
val tcgdexCardmarketExport = providers.gradleProperty("tcgdex.cardmarket.export").orNull
val tcgdexCardmarketExportEnv = providers.environmentVariable("TCGDEX_CARDMARKET_EXPORT").orNull
val tcgdexCardmarketExpansions = providers.gradleProperty("tcgdex.cardmarket.expansions").orNull
val tcgdexPokepediaMissing = providers.gradleProperty("tcgdex.pokepediaMissing").orNull
val tcgdexPokepediaMissingEnv = providers.environmentVariable("TCGDEX_POKEPEDIA_MISSING").orNull
val tcgdexRecognitionVectors = providers.gradleProperty("tcgdex.recognitionVectors").orNull
val tcgdexRecognitionVectorsEnv = providers.environmentVariable("TCGDEX_RECOGNITION_VECTORS").orNull
val generatorInputsDir = layout.projectDirectory
    .dir("generator-inputs")
val defaultCardmarketExport = generatorInputsDir
    .dir("cardmarket")
    .asFile
val defaultPokepediaMissing = generatorInputsDir
    .file("pokepedia/missing-fr-card-images-tree.json")
    .asFile
val defaultRecognitionVectors = generatorInputsDir
    .file("recognition/card-vectors-fr.json")
    .asFile
val setAliasesConfigFile = rootProject.layout.projectDirectory.file("tools/set-aliases-config.json").asFile

// Resolve final languages list (prefer new property, fall back to legacy)
val resolvedLanguages: String = if (tcgdexLanguageLegacy.get().isNotBlank()) {
    tcgdexLanguageLegacy.get()
} else {
    tcgdexLanguages.get()
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidLibrary {
        namespace = "app.cardium.kmptcgdexsdk"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    iosArm64()
    iosSimulatorArm64()

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        val commonMain by getting {
            resources.srcDir(generatedDbDir)
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.coroutines)
                implementation(libs.sqldelight.async.coroutines)
                implementation(libs.sqldelight.androidx.driver)
                implementation(libs.androidx.sqlite)
                implementation(libs.androidx.sqlite.bundled)
                implementation(libs.okio)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                // Xerial sqlite-jdbc is used only by JVM tests to read the
                // generated database via raw JDBC. Runtime/code uses the
                // bundled SQLite driver (with guaranteed FTS5).
                implementation("org.xerial:sqlite-jdbc:3.46.1.3")
            }
        }
    }
}


sqldelight {
    databases {
        create("TcgdexDatabase") {
            packageName.set("app.cardium.tcgdex.db")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight"))
            srcDirs(
                "src/commonMain/sqldelight",
            )
            generateAsync.set(true)
        }
    }
}

val compileKotlinJvmTask = tasks.named("compileKotlinJvm")
val jvmRuntimeConfig = configurations.named("jvmRuntimeClasspath")

// Resolve the Bun executable deterministically so Gradle daemon / IDE / CI environments
// that lack Bun on their minimal PATH can still run compileCardsDatabaseGenerated.
// Precedence: property > env > BUN_INSTALL > ~/.bun > bare PATH fallback.
val bunExecutable: String = run {
    val isWindows = System.getProperty("os.name").startsWith("Windows", ignoreCase = true)
    val bareName = if (isWindows) "bun.cmd" else "bun"

    // 1. Gradle property: -Ptcgdex.bun.executable=/absolute/path/to/bun
    providers.gradleProperty("tcgdex.bun.executable").orNull
        ?.let { return@run it }

    // 2. Environment variable: TCGDEX_BUN=/absolute/path/to/bun
    System.getenv("TCGDEX_BUN")
        ?.takeIf { it.isNotBlank() }
        ?.let { return@run it }

    // 3. BUN_INSTALL env (set by Bun's official installer)
    System.getenv("BUN_INSTALL")
        ?.takeIf { it.isNotBlank() }
        ?.let { File(it, "bin/$bareName") }
        ?.takeIf { it.isFile && it.canExecute() }
        ?.let { return@run it.absolutePath }

    // 4. Default user-home install path (~/.bun/bin/bun)
    File(System.getProperty("user.home"), ".bun/bin/$bareName")
        .takeIf { it.isFile && it.canExecute() }
        ?.let { return@run it.absolutePath }

    // 5. Bare name — works when the daemon PATH already includes bun
    bareName
}

val compileCardsDatabaseGenerated by tasks.registering(Exec::class) {
    group = "tcgdex"
    description = "Compiles cards-database TypeScript into generated JSON artifacts."
    workingDir = cardsDatabaseServerDir.asFile
    commandLine(bunExecutable, "run", "compile")

    doFirst {
        if (!File(bunExecutable).let { it.isFile && it.canExecute() }) {
            // Bare name on PATH — verify it resolves before Exec runs
            val onPath = Runtime.getRuntime().exec(arrayOf("which", bunExecutable)).inputStream.bufferedReader().readLine()
            if (onPath.isNullOrBlank()) {
                throw GradleException(
                    "Cannot find Bun executable ('$bunExecutable').\n" +
                        "Fix: install Bun (curl -fsSL https://bun.sh/install | bash), then either:\n" +
                        "  - restart the Gradle daemon: ./gradlew --stop\n" +
                        "  - or set -Ptcgdex.bun.executable=/path/to/bun\n" +
                        "  - or export TCGDEX_BUN=/path/to/bun",
                )
            }
        }
    }

    inputs.dir(cardsDatabaseCompilerDir)
    inputs.dir(cardsDatabaseDataDir)
    inputs.dir(cardsDatabaseAsiaDataDir)
    inputs.dir(cardsDatabaseMetaDir)
    listOf(
        cardsDatabaseRootDir.file("interfaces.ts").asFile,
        cardsDatabaseServerDir.file("package.json").asFile,
        cardsDatabaseServerDir.file("tsconfig.json").asFile,
    ).forEach { inputFile ->
        if (inputFile.exists()) {
            inputs.file(inputFile)
        }
    }

    outputs.dir(datasetRootDir)
}

val generateTcgdexDatabase by tasks.registering(JavaExec::class) {
    group = "tcgdex"
    description = "Generates the offline tcgdex SQLite database from the local dataset for all configured languages."
    mainClass.set("app.cardium.kmptcgdexsdk.generator.GenerateTcgdexDatabaseKt")

    val output = generatedDbFile.get().asFile
    val forceFlag = if (tcgdexForce.get() || releaseRequested) "true" else "false"
    val languagesList = resolvedLanguages.split(",").map { it.trim().lowercase() }.filter { it.isNotEmpty() }
    val exportPath =
        if (!tcgdexCardmarketExport.isNullOrBlank()) {
            file(tcgdexCardmarketExport)
        } else if (!tcgdexCardmarketExportEnv.isNullOrBlank()) {
            file(tcgdexCardmarketExportEnv)
        } else {
            defaultCardmarketExport
        }
    val resolvedExportPath = exportPath.takeIf { it.exists() }
    val resolvedPokepediaMissingFile =
        if (!tcgdexPokepediaMissing.isNullOrBlank()) {
            file(tcgdexPokepediaMissing)
        } else if (!tcgdexPokepediaMissingEnv.isNullOrBlank()) {
            file(tcgdexPokepediaMissingEnv)
        } else {
            defaultPokepediaMissing
        }.takeIf { it.exists() }
    val resolvedRecognitionVectorsFile =
        if (!tcgdexRecognitionVectors.isNullOrBlank()) {
            file(tcgdexRecognitionVectors)
        } else if (!tcgdexRecognitionVectorsEnv.isNullOrBlank()) {
            file(tcgdexRecognitionVectorsEnv)
        } else {
            defaultRecognitionVectors
        }.takeIf { it.exists() }

    inputs.dir(datasetRootDir)
    if (resolvedExportPath != null) {
        if (resolvedExportPath.isDirectory) {
            inputs.dir(resolvedExportPath)
        } else {
            inputs.file(resolvedExportPath)
        }
    }
    if (setAliasesConfigFile.exists()) {
        inputs.file(setAliasesConfigFile)
    }
    tcgdexCardmarketExpansions?.let { expansions ->
        val expansionsFile = file(expansions)
        if (expansionsFile.exists()) {
            inputs.file(expansionsFile)
        }
    }
    if (resolvedPokepediaMissingFile != null) {
        inputs.file(resolvedPokepediaMissingFile)
    }
    if (resolvedRecognitionVectorsFile != null) {
        inputs.file(resolvedRecognitionVectorsFile)
    }
    outputs.file(output)

    doFirst {
        generatedDbDir.get().asFile.mkdirs()
        println("[Tcgdex] Generating database for languages: $languagesList")
    }

    args(
        "--dataset=${datasetRootDir.asFile.absolutePath}",
        "--languages=${languagesList.joinToString(",")}",
        "--output=${output.absolutePath}",
        "--force=$forceFlag",
    )
    if (resolvedExportPath != null) {
        args("--cardmarket-export=${resolvedExportPath.absolutePath}")
    }
    if (setAliasesConfigFile.exists()) {
        args("--set-aliases-config=${setAliasesConfigFile.absolutePath}")
    }
    if (!tcgdexCardmarketExpansions.isNullOrBlank()) {
        args("--cardmarket-expansions=${tcgdexCardmarketExpansions}")
    }
    if (resolvedPokepediaMissingFile != null) {
        args("--pokepedia-missing=${resolvedPokepediaMissingFile.absolutePath}")
    }
    if (resolvedRecognitionVectorsFile != null) {
        args("--recognition-vectors=${resolvedRecognitionVectorsFile.absolutePath}")
    }

    classpath = files(layout.buildDirectory.dir("classes/kotlin/jvm/main")) + jvmRuntimeConfig.get()
    dependsOn(compileCardsDatabaseGenerated)
    dependsOn(compileKotlinJvmTask)
}

tasks.withType<Copy>().matching { it.name.contains("ProcessResources") || it.name.contains("processAndroidMain") }.configureEach {
    dependsOn(generateTcgdexDatabase)
}

// Ensure processAndroidMainJavaRes (new androidLibrary plugin task) depends on database generation
tasks.matching { it.name == "processAndroidMainJavaRes" }.configureEach {
    dependsOn(generateTcgdexDatabase)
}

val jvmTestTask = tasks.named<Test>("jvmTest")

jvmTestTask.configure {
    dependsOn(compileCardsDatabaseGenerated)
    maxHeapSize = "1536m"
}

tasks.register<Test>("generatorE2eTest") {
    group = "verification"
    description = "Runs the local tcgdex.db generation SQL end-to-end test."
    val baseJvmTest = jvmTestTask.get()
    testClassesDirs = baseJvmTest.testClassesDirs
    classpath = baseJvmTest.classpath
    dependsOn(compileCardsDatabaseGenerated)
    maxHeapSize = "1536m"
    shouldRunAfter(jvmTestTask)
    filter {
        includeTestsMatching("*LocalDatabaseGenerationE2eTest")
    }
}
