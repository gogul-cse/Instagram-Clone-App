plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    //ksp
    id("com.google.devtools.ksp")
    //Hilt
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.application.instagramcloneapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.application.instagramcloneapp"
        minSdk = 30
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

    //Hilt
    implementation("com.google.dagger:hilt-android:2.57.1")
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    ksp("com.google.dagger:hilt-android-compiler:2.57.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    // Room
    implementation("androidx.room:room-runtime:2.8.4")
    // KSP
    ksp("androidx.room:room-compiler:2.8.4")
    // annotationProcessor
    annotationProcessor("androidx.room:room-compiler:2.8.4")
    // Coroutines
    implementation("androidx.room:room-ktx:2.8.4")
    //coil
    implementation("io.coil-kt:coil-compose:2.6.0")
    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    //Json converter
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    //okHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    //Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    implementation("androidx.compose.material:material-icons-extended:1.5.4")
// Note: Check for the version matching your other Compose libraries (usually 1.5.x or 1.6.x)
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
    //shared-preference
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation("androidx.compose.runtime:runtime-livedata:1.6.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}