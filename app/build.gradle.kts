plugins {
    alias(libs.plugins.android.application)
}

// Omit these variables for an unsigned release (e.g. an F-Droid source build).
// Signing secrets stay outside the repository and Gradle command line.
val releaseStoreFile = providers.environmentVariable("ANDROID_RELEASE_STORE_FILE").orNull

android {
    namespace = "com.moba.atombergremote"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "io.github.vishnusrivatsava.leafirremote"
        minSdk = 24
        targetSdk = 36
        versionCode = 6
        versionName = "1.4.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (releaseStoreFile != null) {
            create("release") {
                storeFile = file(releaseStoreFile)
                storePassword = providers.environmentVariable("ANDROID_RELEASE_STORE_PASSWORD").get()
                keyAlias = providers.environmentVariable("ANDROID_RELEASE_KEY_ALIAS").get()
                keyPassword = providers.environmentVariable("ANDROID_RELEASE_KEY_PASSWORD").get()
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    sourceSets.getByName("test").resources.srcDir("../remote-configs")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
