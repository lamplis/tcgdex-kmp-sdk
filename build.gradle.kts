import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    kotlin("plugin.serialization") version "2.2.20"
}

ktlint {
    ignoreFailures.set(true)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // Add JVM target to support shared:jvm consumption
    jvm()

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    )

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.datetime)
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.cio)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "app.cardium.tcgdex.sdk"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Generator: fetch catalog via SDK and write JSON to src/commonMain/resources/tcgdex/<lang>/
tasks.register<JavaExec>("generateEmbeddedCatalog") {
    group = "tcgdex"
    description = "Generate embedded TCGdex catalog JSONs for all languages"
    mainClass.set("tools.GenerateEmbeddedCatalog")
    val jvmMain = kotlin.targets.getByName("jvm").compilations.getByName("main")
    dependsOn(jvmMain.compileTaskProvider)
    classpath = files(jvmMain.output.allOutputs, jvmMain.runtimeDependencyFiles)
    // Write under module resources by default
    systemProperty("outputDir", project.projectDir.resolve("src/commonMain/resources/tcgdex").absolutePath)
    // Pass-through optional tuning properties for faster debug runs and local mode
    val keys = listOf("langs", "limitSets", "progressEvery", "skipDetails", "localDbPath", "offlineOnly", "verbose", "seriesFilter")
    keys.forEach { key ->
        val fromProject = project.findProperty(key)?.toString()
        val fromSystem = System.getProperty(key)
        val fromEnv = System.getenv(key.uppercase())
        val value = fromProject ?: fromSystem ?: fromEnv
        if (!value.isNullOrBlank()) {
            systemProperty(key, value)
        }
    }
}

// Ensure the generator runs for release builds to refresh embedded resources & Kotlin data
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    if (name.contains("Release")) {
        dependsOn("generateEmbeddedCatalog")
    }
}

// Also wire Android library assembleRelease to trigger generation
tasks.matching { it.name == "assembleRelease" || it.name == "bundleReleaseAar" }.configureEach {
    dependsOn("generateEmbeddedCatalog")
}
