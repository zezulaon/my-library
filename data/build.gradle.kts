plugins {
    alias(libs.plugins.mylibrary.android.library)
}

android {
    namespace = "dev.zezula.data"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core-model"))
    implementation(project(":core-utils"))
    implementation(project(":data-database"))
    implementation(project(":data-network"))

    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.auth)
    implementation(libs.google.firebase.firestore)

    // DI
    implementation(project.dependencies.platform(libs.koin.bom))
    androidTestImplementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.android)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
    androidTestImplementation(libs.koin.test)
}
