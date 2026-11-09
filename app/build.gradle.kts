plugins {
    id("com.android.application")
}

android {
    namespace = "com.alphanicholson.qrmenu"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.alphanicholson.qrmenu"
        minSdk = 21          // Android 5.0 — covers virtually every Android TV in hotels
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Sign via Build > Generate Signed Bundle / APK in Android Studio
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Intentionally none — plain Android framework WebView, nothing to resolve at build time.
}
