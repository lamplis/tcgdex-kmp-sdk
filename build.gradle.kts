import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
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
val datasetRootDir = rootProject.layout.projectDirectory.dir("libs/cards-database/server/generated")
val generatedDbDir = layout.buildDirectory.dir("generated/tcgdex/resources")
val generatedDbFile = generatedDbDir.map { it.file("tcgdex.db") }

// Resolve final languages list (prefer new property, fall back to legacy)
val resolvedLanguages: String = if (tcgdexLanguageLegacy.get().isNotBlank()) {
    tcgdexLanguageLegacy.get()
} else {
    tcgdexLanguages.get()
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget {
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
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "app.cardium.kmptcgdexsdk"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets["main"].resources.srcDir("src/commonMain/resources")
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
    mainClass.set("app.cardium.kmptcgdexsdk.build.GenerateTcgdexDatabaseKt")

    val output = generatedDbFile.get().asFile
    val forceFlag = if (tcgdexForce.get() || releaseRequested) "true" else "false"
    val languagesList = resolvedLanguages.split(",").map { it.trim().lowercase() }.filter { it.isNotEmpty() }

    // Mark all language directories as inputs for incremental builds
    languagesList.forEach { lang ->
        val langDir = datasetRootDir.dir(lang).asFile
        if (langDir.exists()) {
            inputs.dir(langDir)
        }
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

    classpath = files(layout.buildDirectory.dir("classes/kotlin/jvm/main")) + jvmRuntimeConfig.get()
    dependsOn(compileKotlinJvmTask)
}

tasks.withType<Copy>().matching { it.name.contains("ProcessResources") }.configureEach {
    dependsOn(generateTcgdexDatabase)
}
