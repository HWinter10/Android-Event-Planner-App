plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.eventplanner"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.eventplanner"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
                arguments["room.incremental"] = "true"
            }
        }
    }

    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.0" }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions { jvmTarget = "11" }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("com.google.android.material:material:1.8.0")

    // Compose
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.compose.material:material:1.5.0")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.navigation:navigation-compose:2.7.0")

    // Lifecycle / ViewModel
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    // Room
    implementation("androidx.room:room-runtime:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")
    ksp("androidx.room:room-compiler:2.5.2")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // Coroutines / Flow
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    // Jetpack Security
    implementation("androidx.security:security-crypto:1.1.0-alpha05")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Google Places SDK
    implementation("com.google.android.libraries.places:places:3.5.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.0")
}