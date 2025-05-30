plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
}

fun validateApiKey(): String {
    val apiKey = project.findProperty("TMDB_API_KEY") as String?

    return when {
        apiKey == null -> {
            throw GradleException(
                "TMDB_API_KEY not found in gradle.properties. \n" +
                        "Add TMDB_API_KEY=your_key to /gradle.properties"
            )
        }
        apiKey.isEmpty() -> {
            throw GradleException(
                "TMDB_API_KEY cannot be empty. \n" +
                        "Add TMDB_API_KEY=your_key to /gradle.properties"
            )
        }
        else -> apiKey
    }
}

android {
    namespace = "anugrah.rochmat.moviequ"
    compileSdk = 35

    defaultConfig {
        applicationId = "anugrah.rochmat.moviequ"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Setup TMDB API key
        val apiKey = validateApiKey()
        buildConfigField("String", "TMDB_API_KEY", "\"$apiKey\"")
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
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.runtime.livedata)

    // Navigation compose
    implementation(libs.androidx.navigation.compose)

    // ViewModel & LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // RxJava
    implementation(libs.reactivex.rxjava3.rxjava)
    implementation(libs.reactivex.rxjava3.rxandroid)
    implementation(libs.androidx.lifecycle.reactivestreams.ktx)

    // Retrofit
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.adapter.rxjava3)
    implementation(libs.squareup.retrofit.converter.moshi)
    implementation(libs.squareup.okhttp.logging.interceptor)

    // Moshi (JSON Parsing)
    implementation(libs.squareup.moshi)
    implementation(libs.squareup.moshi.kotlin)
    kapt(libs.squareup.moshi.kotlin.codegen)

    // KOIN
    implementation(libs.insert.koin.android)
    implementation(libs.insert.koin.androix.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.rxjava3)
    kapt(libs.androidx.room.compiler)

    // GMS Play Services
    implementation(libs.google.gms.services.location)

    // Permission
    implementation(libs.google.accompanist.permissions)

    // Coil-kt Coil Compose
    implementation(libs.io.coilkt.coil.compose)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.android)
    testImplementation(libs.reactivex.rxjava3.rxjava)
    testImplementation(libs.kotlinx.coroutines.test)

    // Android Testing
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug Implementation
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}