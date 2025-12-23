plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.0.20-1.0.25"
}

android {
    namespace = "com.example.arsisi_frontend"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.arsisi_frontend"
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
        compose = true
    }
}

dependencies {
    // === DEPENDENCIES YANG DIBUTUHKAN UNTUK COMPOSE & ARSITEKTUR KITA ===

    // 1. Core Kotlin & Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // 2. Compose UI & Material (Menggantikan XML/View)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)

    // 3. Navigation Compose (WAJIB untuk NavGraph dan rememberNavController)
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // 4. Lifecycle & ViewModel Compose (WAJIB untuk ViewModel dan collectAsStateWithLifecycle)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // 5. Coroutines (Wajib untuk Flow di Repository)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // 6. Retrofit dan OkHttp (Wajib untuk API Service)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // 7. WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // 8. Room Database (TAMBAHAN BARU)
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")


    // === Testing Dependencies ===
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
}
