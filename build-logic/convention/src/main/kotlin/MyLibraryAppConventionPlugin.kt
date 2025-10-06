import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class MyLibraryAppConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        println("#### MyLibraryAppConventionPlugin applied to ${target.name} ####")
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.android")
        }
    }
}
