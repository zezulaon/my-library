plugins {
    alias(libs.plugins.mylibrary.android.library)
    alias(libs.plugins.mylibrary.di)
}

android {
    namespace = "dev.zezula.domain"
}

dependencies {
    implementation(project(":core-model"))
    implementation(project(":core-utils"))

    implementation(libs.squareup.retrofit2)
}
