plugins {
    alias(libs.plugins.mylibrary.android.library)
}

android {
    namespace = "dev.zezula.data.network"
    compileSdk = 34

    defaultConfig {
        minSdk = 23

        consumerProguardFiles("consumer-rules.pro")

        buildConfigField(
            type = "String",
            name = "ML_GOODREADS_API_KEY",
            value = getStringProperty("myLibrary.goodreadsApiKey", true),
        )

        buildConfigField(
            type = "String",
            name = "ML_GOOGLE_API_KEY",
            value = getStringProperty("myLibrary.googleApiKey", true),
        )

        buildConfigField(
            type = "String",
            name = "ML_BASE_API_URL",
            value = getStringProperty("myLibrary.myLibraryBaseApiUrl", true),
        )
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

    // DI
    implementation(project.dependencies.platform(libs.koin.bom))
    androidTestImplementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.android)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
    androidTestImplementation(libs.koin.test)

    // Tests
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
}

fun getStringProperty(propertyName: String, wrap: Boolean = false): String {
    val prop = providers.gradleProperty(propertyName).get()
    return if (wrap) {
        "\"" + prop + "\""
    } else {
        prop
    }
}
