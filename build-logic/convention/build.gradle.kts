plugins {
    `kotlin-dsl`
}

group = "dev.zezula.books.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.gradleApiPlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "mylibrary.android.application"
            implementationClass = "MyLibraryAppConventionPlugin"
        }
        register("androidLibrary") {
            id = "mylibrary.android.library"
            implementationClass = "MyLibraryLibConventionPlugin"
        }
        register("androidRoom") {
            id = "mylibrary.android.room"
            implementationClass = "MyLibraryRoomConventionPlugin"
        }
    }
}
