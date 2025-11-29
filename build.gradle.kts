import dev.iurysouza.modulegraph.Orientation
import dev.iurysouza.modulegraph.Theme

@Suppress("DSL_SCOPE_VIOLATION") // Remove when fixed https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.diffplug.spotless) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.androidx.room) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.modulegraph)
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

moduleGraphConfig {
    readmePath.set("$rootDir/README.md")
    showFullPath.set(false)
    orientation.set(Orientation.LEFT_TO_RIGHT)

    setStyleByModuleType.set(true)
    nestingEnabled.set(false)
    theme.set(Theme.NEUTRAL)

    excludedModulesRegex.set(".*legacy.*")
    rootModulesRegex.set("^(?!.*legacy).*$")
}
