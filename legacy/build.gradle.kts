plugins {
    alias(libs.plugins.mylibrary.android.library)
    alias(libs.plugins.mylibrary.android.room)
}

android {
    namespace = "dev.zezula.books.legacy"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core-model"))
    implementation(project(":core-utils"))
    implementation(project(":data-network"))
    implementation(project(":data-database"))

    implementation(libs.androidx.core.ktx)

    // DI
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.android)
}
