plugins {
    alias(libs.plugins.mylibrary.android.library)
}

android {
    namespace = "dev.zezula.domain"
}

dependencies {
    implementation(project(":core-model"))
    implementation(project(":core-utils"))

    implementation(libs.squareup.retrofit2)

    // DI
    androidTestImplementation(project.dependencies.platform(libs.koin.bom))
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.android)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
    androidTestImplementation(libs.koin.test)
}
