@Suppress("DSL_SCOPE_VIOLATION") // Remove when fixed https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.diffplug.spotless) apply false
}

allprojects {

    apply(plugin = rootProject.libs.plugins.diffplug.spotless.get().pluginId)
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            ktlint(libs.versions.ktlint.get())
        }
        kotlinGradle {
            target("**/*.kts")
            ktlint(libs.versions.ktlint.get())
        }
    }
}
