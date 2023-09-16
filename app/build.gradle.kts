@Suppress("DSL_SCOPE_VIOLATION") // Remove when fixed https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.gms.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "dev.zezula.books"
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    defaultConfig {
        applicationId = "dev.zezula.books"
        minSdk = 23
        targetSdk = 33
        versionCode = 104
        versionName = "1.0.4"

        testInstrumentationRunner = "dev.zezula.books.InstrumentationTestRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField(
            type = "String",
            name = "ML_GOODREADS_API_KEY",
            value = getStringProperty("myLibrary.goodreadsApiKey", true),
        )

        buildConfigField(
            type = "String",
            name = "ML_FIREBASE_CLIENT_ID",
            value = getStringProperty("myLibrary.firebaseClientId", true),
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

    compileOptions {
        // Desugaring for access to LocalDateTime APIs (this can be removed once the app the minSdk=API 26)
        isCoreLibraryDesugaringEnabled = true
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
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

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

    // Room DB
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // DI
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.android)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
    androidTestImplementation(libs.koin.test)

    // Logging
    implementation(libs.jakewharton.timber)

    // Firebase
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.firestore)
    implementation(libs.google.firebase.auth)
    implementation(libs.google.firebase.crashlytics)

    // Glide
    implementation(libs.bumptech.glide)
    ksp(libs.bumptech.glide.compiler)
    implementation(libs.bumptech.glide.compose)

    // HTTP/REST/XML/JSON
    implementation(libs.squareup.retrofit2)
    implementation(libs.squareup.retrofit2.converter.gson)
    implementation(libs.squareup.retrofit2.converter.simplexml) {
        // XmlPullParser (Since it is already included: https://issuetracker.google.com/issues/289087852)
        exclude(group = "xpp3", module = "xpp3")
    }

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

    coreLibraryDesugaring(libs.android.tools.desugar.jdk)
}

fun getStringProperty(propertyName: String, wrap: Boolean = false): String {
    val prop = providers.gradleProperty(propertyName).get()
    return if (wrap) {
        "\"" + prop + "\""
    } else {
        prop
    }
}
