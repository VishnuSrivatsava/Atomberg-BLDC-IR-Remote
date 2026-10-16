plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.moba.atombergremote"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.moba.atombergremote"
        minSdk = 24
        targetSdk = 36
        versionCode = 5
        versionName = "1.3"

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
