plugins {
    alias(libs.plugins.mylibrary.android.library)
}

android {
    namespace = "dev.zezula.core.utils"
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
