plugins {
    alias(libs.plugins.mylibrary.android.library)
}

android {
    namespace = "dev.zezula.core.utils"
    compileSdk = 34

    defaultConfig {
        minSdk = 23

        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    // Logging
    api(libs.jakewharton.timber)

    // Date and time
    api(libs.kotlinx.datetime)

    api(libs.kotlinx.coroutines.android)

    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.auth)
    implementation(libs.google.firebase.crashlytics)

    // Tests
    implementation(libs.junit)
}
