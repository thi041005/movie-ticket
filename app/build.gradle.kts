plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    alias(libs.plugins.google.gms.google.services)
}


android {
    namespace = "com.example.app02"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.app02"
        minSdk = 31
        targetSdk = 35
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
        compose = true
    }
    kapt {
        correctErrorTypes = true
    }
    buildToolsVersion = "35.0.0"
}

dependencies {

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)

    // Core AndroidX libraries
    implementation(libs.androidx.core.ktx.v1120)
    implementation(libs.androidx.lifecycle.runtime.ktx.v262)
    implementation(libs.androidx.activity.compose.v180)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // Compose UI
    implementation(libs.ui)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material)
    implementation(libs.androidx.runtime)
    implementation(libs.material3)

    // Material Icons
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    // Lifecycle components
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Networking
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v115)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.chromecast.sender)
    implementation(libs.firebase.database.ktx)
    implementation(libs.androidx.compose.ui.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core.v350)
    implementation(libs.google.firebase.database.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation("com.google.firebase:firebase-database:20.2.1")
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation("com.auth0.android:jwtdecode:2.0.1")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.kizitonwose.calendar:compose:2.4.0")

}
