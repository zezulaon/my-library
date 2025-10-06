import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class MyLibraryLibConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        println("#### MyLibraryLibConventionPlugin applied to ${target.name} ####")
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.android")
        }
    }
}
