plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.thrillathon.client"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.thrillathon.client"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.face.detection)
    implementation ("androidx.camera:camera-camera2:1.5.3")
    implementation ("androidx.camera:camera-lifecycle:1.5.3")
    implementation ("androidx.camera:camera-view:1.5.3")
    implementation ("androidx.core:core:1.18.0")
    implementation ("com.google.mlkit:face-mesh-detection:16.0.0-beta3")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation(project(":shared"))
    implementation ("com.squareup.okhttp3:okhttp:4.11.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

}
