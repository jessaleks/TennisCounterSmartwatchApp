plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.aleksanderjess.tenniscounter"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aleksanderjess.tenniscounter"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true

    }
    buildToolsVersion = "35.0.0"
}

dependencies {

    implementation(libs.play.services.wearable)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.compose.material)
    implementation(libs.compose.foundation)
    implementation(libs.wear.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.core.splashscreen)
    implementation(libs.navigation.runtime.ktx)
    implementation(libs.navigation.compose)
    implementation(libs.compose.material3)
    implementation(libs.material3.android)
    implementation(libs.ui.text.google.fonts)
    implementation(libs.junit.jupiter)
    implementation(libs.testng)
    implementation(libs.ui.test.junit4.android)

    androidTestImplementation(platform(libs.compose.bom))

    debugImplementation(libs.ui.tooling)
    
}