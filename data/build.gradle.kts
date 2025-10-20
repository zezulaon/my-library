plugins {
    alias(libs.plugins.mylibrary.android.library)
    alias(libs.plugins.mylibrary.di)
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
}
