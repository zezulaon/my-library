plugins {
    alias(libs.plugins.mylibrary.android.library)
}

android {
    namespace = "dev.zezula.core.model"
}

dependencies {
    implementation(project(":core-utils"))
}
