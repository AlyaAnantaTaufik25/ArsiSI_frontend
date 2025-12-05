plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
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
    implementation(libs.androidx.core.ktx) // Sudah ada
    implementation(libs.androidx.activity.compose) // Sudah ada

    // 2. Compose UI & Material (Menggantikan XML/View)
    implementation(platform(libs.androidx.compose.bom)) // Sudah ada
    implementation(libs.androidx.ui) // Sudah ada
    implementation(libs.androidx.ui.graphics) // Sudah ada
    implementation(libs.androidx.ui.tooling.preview) // Sudah ada
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.material3) // Sudah ada
    debugImplementation(libs.androidx.ui.tooling) // Sudah ada

    // 3. Navigation Compose (WAJIB untuk NavGraph dan rememberNavController)
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // 4. Lifecycle & ViewModel Compose (WAJIB untuk ViewModel dan collectAsStateWithLifecycle)
    implementation(libs.androidx.lifecycle.runtime.ktx) // Sudah ada
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // 5. Coroutines (Wajib untuk Flow di Repository)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // 6. Retrofit dan OkHttp (Wajib untuk API Service)
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Sudah ada
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Sudah ada
    implementation("com.squareup.okhttp3:okhttp:4.12.0")


    // === Testing Dependencies ===
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
}