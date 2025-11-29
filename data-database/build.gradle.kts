plugins {
    alias(libs.plugins.mylibrary.android.library)
    alias(libs.plugins.mylibrary.android.room)
    alias(libs.plugins.mylibrary.di)
}

android {
    namespace = "dev.zezula.data.database"
}

dependencies {
    implementation(project(":core-model"))
    implementation(project(":core-utils"))
}
