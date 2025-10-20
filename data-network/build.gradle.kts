plugins {
    alias(libs.plugins.mylibrary.android.library)
    alias(libs.plugins.mylibrary.di)
}

android {
    namespace = "dev.zezula.data.network"
}

dependencies {
    implementation(project(":core-model"))
    implementation(project(":core-utils"))

    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.firestore)
    implementation(libs.google.firebase.auth)

    // HTTP/REST/XML/JSON
    implementation(libs.squareup.retrofit2)
    implementation(libs.squareup.retrofit2.converter.gson)
    implementation(libs.squareup.retrofit2.converter.simplexml) {
        // XmlPullParser (Since it is already included: https://issuetracker.google.com/issues/289087852)
        exclude(group = "xpp3", module = "xpp3")
    }
    implementation(libs.squareup.okhttp3.okhttp)
    implementation(libs.squareup.okhttp3.logging)

    // Tests
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
}

