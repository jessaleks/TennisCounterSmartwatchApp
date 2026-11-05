// AGP 9 ships built-in Kotlin. Put a newer Kotlin Gradle plugin on the build
// classpath so the Compose compiler and the language version stay in lockstep.
// https://developer.android.com/build/releases/agp-9-0-0-release-notes#runtime-dependency-on-kotlin-gradle-plugin
buildscript {
    dependencies {
        classpath(libs.kotlin.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
