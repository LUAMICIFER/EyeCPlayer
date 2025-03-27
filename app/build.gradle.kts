plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.eyecplayer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.eyecplayer"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.camera.view)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //for permission handelling
    implementation("com.google.accompanist:accompanist-permissions:0.31.1-alpha")
    implementation("androidx.media3:media3-exoplayer:1.5.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.5.1")
    implementation("androidx.media3:media3-ui:1.5.1")
//    implementation("androidx.media3:media3-exoplayer-ffmpeg:1.1.1")    // for ffmpeg support (mkv )
    implementation("androidx.camera:camera-core:1.3.0")  // Core CameraX functionality
    implementation("androidx.camera:camera-camera2:1.3.0")  // Camera2 support
    implementation("androidx.camera:camera-lifecycle:1.3.0")  // Lifecycle-aware CameraX
    implementation ("com.google.mlkit:face-detection:16.1.7") // Face Detection kit
    val nav_version = "2.8.9"
    implementation("androidx.navigation:navigation-compose:$nav_version")
    //shimmer effect
    implementation("com.valentinilk.shimmer:compose-shimmer:1.2.0")

    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))
}