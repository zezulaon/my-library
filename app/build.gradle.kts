plugins {
    alias(libs.plugins.mylibrary.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.mylibrary.di)

    alias(libs.plugins.google.gms.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "dev.zezula.books"

    defaultConfig {
        versionCode = 306
        versionName = "3.2.2"

        testInstrumentationRunner = "dev.zezula.books.InstrumentationTestRunner"
        testInstrumentationRunnerArguments["useTestStorageService"] = "true"

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

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        managedDevices {
            localDevices {
                create("mediumPhoneApi35") {
                    device = "Medium Phone"
                    apiLevel = 35
                    systemImageSource = "aosp"
                }
            }
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

    // Firebase
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.firestore)
    implementation(libs.google.firebase.auth)
    implementation(libs.google.firebase.crashlytics)

    // Glide
    implementation(libs.bumptech.glide)
    implementation(libs.bumptech.glide.compose)

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
    // Used to access Test Storage Service (for example to save screenshots)
    androidTestUtil(libs.androidx.test.services)

    testImplementation(libs.junit)
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
