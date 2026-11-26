plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.atasoyyazilim.kurankerim"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.atasoyyazilim.kurankerim"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    // Tilâvet sesleri (install-time asset pack)
    assetPacks.add(":quran_audio")

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    androidResources {
        // Ses dosyaları sıkıştırılmadan paketlenir (MediaPlayer/ExoPlayer doğrudan okur)
        noCompress.addAll(listOf("opus", "m4a"))
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Tilâvet oynatıcı (ekran kapalıyken de çalar)
    implementation("androidx.media3:media3-exoplayer:1.5.0")
    implementation("androidx.media3:media3-session:1.5.0")

    // Ana ekran widget'ları
    implementation("androidx.glance:glance-appwidget:1.1.1")

    // Namaz vakti ve kıble hesabı — MIT lisanslı, internetsiz
    implementation("com.batoulapps.adhan:adhan:1.2.1")
}
