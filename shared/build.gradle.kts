import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

kotlin {
    android {
        namespace = "com.aleksanderjess.tenniscounter.lib"
        compileSdk {
            version = release(37) {
                minorApiLevel = 2
            }
        }
        minSdk = 29
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        withHostTest {}
    }

    watchosArm64().binaries.framework {
        baseName = "TennisCore"
        isStatic = true
    }
    watchosDeviceArm64().binaries.framework {
        baseName = "TennisCore"
        isStatic = true
    }
    watchosSimulatorArm64().binaries.framework {
        baseName = "TennisCore"
        isStatic = true
    }
    watchosX64().binaries.framework {
        baseName = "TennisCore"
        isStatic = true
    }

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
