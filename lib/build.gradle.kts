@file:OptIn(ExperimentalWasmDsl::class)

import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.mavenPublish)
}

kotlin {
    jvmToolchain(21)
    metadata()

    // define targets
    jvm()
    androidTarget()
    wasmJs {
        nodejs()
        binaries.executable()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                // arrow
                implementation(project.dependencies.platform(libs.arrow.bom))
                implementation(libs.arrow.core)

                // compose
                implementation(compose.runtime)

                // koin
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.koin.compose.viewmodel)
            }
        }

        // all platforms using skia
        val skiaMain by creating {
            dependsOn(commonMain)
        }

        listOf(
            jvmMain,
            wasmJsMain,
            nativeMain,
        ).forEach {
            it.get().dependsOn(skiaMain)
        }

        val nativeMain by getting {
            dependsOn(skiaMain)
        }

        listOf(
            iosX64Main,
            iosArm64Main,
            iosSimulatorArm64Main,
            macosX64Main,
            macosArm64Main,
        ).forEach {
            it.get().dependsOn(nativeMain)
        }
    }
}

android {
    namespace = "com.carthas.common"
    compileSdk = 36
}

tasks.withType<KotlinCompile>().all {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

group = "com.carthas"
val artifactId = "common"
version = "0.4.1"

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), artifactId, version.toString())

    pom {
        name = "Carthas common library"
        description = "A common set of tools used for Compose Multiplatform development"
        inceptionYear = "2025"
        url = "https://github.com/carthas/common/"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "carthas"
                name = "Carthas Software"
                url = "https://github.com/carthas/"
            }
        }
        scm {
            url = "https://github.com/carthas/common/"
            connection = "scm:git:git://github.com/carthas/common"
            developerConnection = "scm:git:ssh://git@github.com/carthas/common.git"
        }
    }
}