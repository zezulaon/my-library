@Suppress("DSL_SCOPE_VIOLATION") // Remove when fixed https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.mylibrary.android.application)
    alias(libs.plugins.compose.compiler)

    alias(libs.plugins.google.gms.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "dev.zezula.books"
    compileSdk = 35
    buildToolsVersion = "34.0.0"

    defaultConfig {
        applicationId = "dev.zezula.books"
        minSdk = 23
        targetSdk = 34
        versionCode = 306
        versionName = "3.2.2"

        testInstrumentationRunner = "dev.zezula.books.InstrumentationTestRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField(
            type = "String",
            name = "ML_FIREBASE_CLIENT_ID",
            value = getStringProperty("myLibrary.firebaseClientId", true),
        )

        buildConfigField(
            type = "String",
            name = "ML_CONTACT_EMAIL",
            value = getStringProperty("myLibrary.contactEmail", true),
        )

        buildConfigField(
            type = "String",
            name = "ML_URL_RELEASE_INFO",
            value = getStringProperty("myLibrary.releaseInfo", true),
        )

        buildConfigField(
            type = "String",
            name = "ML_URL_AMAZON_SEARCH",
            value = getStringProperty("myLibrary.linkAmazonSearch", true),
        )
    }

    signingConfigs {
        create("release") {
            keyAlias = getStringProperty("myLibrary.release.keyAlias")
            storeFile = file(getStringProperty("myLibrary.release.pathToKeystore"))
            keyPassword = getStringProperty("myLibrary.release.keyPswd")
            storePassword = getStringProperty("myLibrary.release.storePswd")
        }
    }

    buildTypes {
        getByName("release") {
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            // Enables code shrinking, obfuscation, and optimization
            isMinifyEnabled = true
            // Enables resource shrinking
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    flavorDimensions += "booksAppDimension"

    productFlavors {
        create("standard") {
            dimension = "booksAppDimension"
            versionNameSuffix = "-s"
        }
        create("bookdiary") {
            dimension = "booksAppDimension"
            applicationId = "org.zezula.bookdiary"
            versionNameSuffix = "-mbd"
        }
        create("gb") {
            dimension = "booksAppDimension"
            applicationId = "org.zezi.gb"
            versionNameSuffix = "-gb"
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

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core-model"))
    implementation(project(":core-utils"))
    implementation(project(":data"))
    implementation(project(":legacy"))

    implementation(libs.androidx.core.ktx)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // UI/Compose
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)

    // UI tests
    // Test rules and transitive dependencies
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    // Required for createAndroidComposeRule (also ads generic ComponentActivity to Manifest during tests)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // DI
    implementation(project.dependencies.platform(libs.koin.bom))
    androidTestImplementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.android)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
    androidTestImplementation(libs.koin.test)

    // Firebase
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.firestore)
    implementation(libs.google.firebase.auth)
    implementation(libs.google.firebase.crashlytics)

    // Glide
    implementation(libs.bumptech.glide)
    implementation(libs.bumptech.glide.compose)

    // HTTP/REST/XML/JSON
    implementation(libs.squareup.retrofit2)
    implementation(libs.squareup.retrofit2.converter.gson)
    implementation(libs.squareup.retrofit2.converter.simplexml) {
        // XmlPullParser (Since it is already included: https://issuetracker.google.com/issues/289087852)
        exclude(group = "xpp3", module = "xpp3")
    }
    implementation(libs.squareup.okhttp3.okhttp)
    implementation(libs.squareup.okhttp3.logging)

    // Accompanist - permissions
    implementation(libs.google.accompanist.permissions)

    // GMS services (used for Google SignIn)
    implementation(libs.google.android.gms.services.auth)

    // Barcode scanning (ML model/lib for ) + Camera
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.google.mlkit.barcode.scanning)

    // Tests
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.app.cash.turbine)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)

    // Instrumented tests: jUnit rules and runners
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.espresso)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

fun getStringProperty(propertyName: String, wrap: Boolean = false): String {
    val prop = providers.gradleProperty(propertyName).get()
    return if (wrap) {
        "\"" + prop + "\""
    } else {
        prop
    }
}
