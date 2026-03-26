plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions { jvmTarget = "17" }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "SipTrack Compose Multiplatform UI module"
        homepage = "https://github.com/JSchmidty/JSipTrack"
        version = "1.0"
        ios.deploymentTarget = "17.0"
        framework {
            baseName = "SipTrackComposeApp"
            isStatic = true
        }
        // Declare dependency on the shared KMP pod so CocoaPods resolves it
        pod("SipTrackKit") {
            path = project.file("../shared")
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(libs.compose.ui)
                implementation(libs.compose.material3)
                implementation(libs.compose.foundation)
                implementation(libs.compose.runtime)
                implementation(libs.koin.compose)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.kotlinx.coroutines.android)
            }
        }
        // iosMain sourceSet — hosts MainViewController.kt entry point
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                // Compose UI for iOS is provided by the Compose Multiplatform plugin
                // No additional iOS-specific Compose deps needed beyond commonMain
            }
        }
        val iosX64Main by getting { dependsOn(iosMain) }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
    }
}

android {
    namespace = "com.siptech.siptrack"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.siptech.siptrack"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { compose = true }
}
