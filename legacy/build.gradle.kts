plugins {
    alias(libs.plugins.mylibrary.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "dev.zezula.books.legacy"

    defaultConfig {

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.generateKotlin", "true")
        }
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core-model"))
    implementation(project(":core-utils"))
    implementation(project(":data-network"))
    implementation(project(":data-database"))

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.core.ktx)

    // DI
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.android)
}
