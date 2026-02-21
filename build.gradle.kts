import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec
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

// Force sqlite-jdbc to iOS-compatible version for database generation (see plan: fix iOS DB corruption)
configurations.all {
    resolutionStrategy {
        force("org.xerial:sqlite-jdbc:3.39.4.1")
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
val datasetRootDir = rootProject.layout.projectDirectory.dir("libs/cards-database/server/generated")
val generatedDbDir = layout.buildDirectory.dir("generated/tcgdex/resources")
val generatedDbFile = generatedDbDir.map { it.file("tcgdex.db") }
val tcgdexCardmarketExport = providers.gradleProperty("tcgdex.cardmarket.export").orNull
val defaultCardmarketExport = rootProject.layout.projectDirectory
    .dir("../poke-browser/export")
    .file("cardmarket-prices.json")
    .asFile

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
                implementation(libs.okio)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.sqldelight.android.driver)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(libs.sqldelight.native.driver)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.sqldelight.sqlite.driver)
                // Pin sqlite-jdbc to a version compatible with iOS system SQLite.
                // iOS 16+ ships with Apple's SQLite ~3.39.x. Using 3.39.4.1 ensures
                // the generated DB pages are readable by the iOS system library.
                implementation("org.xerial:sqlite-jdbc:3.39.4.1") {
                    because("iOS links system libsqlite3 (~3.39.x); generator must produce compatible pages")
                }
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
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
        }
    }
}

val compileKotlinJvmTask = tasks.named("compileKotlinJvm")
val jvmRuntimeConfig = configurations.named("jvmRuntimeClasspath")

val generateTcgdexDatabase by tasks.registering(JavaExec::class) {
    group = "tcgdex"
    description = "Generates the offline tcgdex SQLite database from the local dataset for all configured languages."
    mainClass.set("app.cardium.kmptcgdexsdk.generator.GenerateTcgdexDatabaseKt")

    val output = generatedDbFile.get().asFile
    val forceFlag = if (tcgdexForce.get() || releaseRequested) "true" else "false"
    val languagesList = resolvedLanguages.split(",").map { it.trim().lowercase() }.filter { it.isNotEmpty() }
    val exportFile = tcgdexCardmarketExport?.let { file(it) } ?: defaultCardmarketExport
    val resolvedExportFile = exportFile.takeIf { it.exists() }

    // Mark all language directories as inputs for incremental builds
    languagesList.forEach { lang ->
        val langDir = datasetRootDir.dir(lang).asFile
        if (langDir.exists()) {
            inputs.dir(langDir)
        }
    }
    if (resolvedExportFile != null) {
        inputs.file(resolvedExportFile)
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
    if (resolvedExportFile != null) {
        args("--cardmarket-export=${resolvedExportFile.absolutePath}")
    }

    classpath = files(layout.buildDirectory.dir("classes/kotlin/jvm/main")) + jvmRuntimeConfig.get()
    dependsOn(compileKotlinJvmTask)
}

tasks.withType<Copy>().matching { it.name.contains("ProcessResources") || it.name.contains("processAndroidMain") }.configureEach {
    dependsOn(generateTcgdexDatabase)
}

// Ensure processAndroidMainJavaRes (new androidLibrary plugin task) depends on database generation
tasks.matching { it.name == "processAndroidMainJavaRes" }.configureEach {
    dependsOn(generateTcgdexDatabase)
}
