plugins {
    alias(libs.plugins.mylibrary.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "dev.zezula.data.database"
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.generateKotlin", "true")
    }
}

dependencies {
    implementation(project(":core-model"))
    implementation(project(":core-utils"))

    // Room DB
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // DI
    implementation(project.dependencies.platform(libs.koin.bom))
    androidTestImplementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.android)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
    androidTestImplementation(libs.koin.test)
}
