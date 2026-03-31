plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    kotlin("native.cocoapods")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Whoopie shared KMP module"
        homepage = "https://github.com/Darpan-Singh/Whoopie"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Add shared dependencies here (e.g. kotlinx-coroutines-core)
        }
    }
}

android {
    namespace = "com.thrillathon.client.shared"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
