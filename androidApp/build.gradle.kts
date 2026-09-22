import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.fivesuits.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.fivesuits.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }
    buildFeatures.compose = true
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

kotlin.compilerOptions.jvmTarget.set(JvmTarget.JVM_17)

dependencies {
    implementation(project(":composeApp"))
    implementation(libs.androidx.activity.compose)
}
