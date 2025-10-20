import dev.zezula.books.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class MyLibraryDiConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            dependencies {
                add(
                    "implementation",
                    project.dependencies.platform(libs.findLibrary("koin.bom").get()),
                )
                add("implementation", libs.findLibrary("koin.androidx.compose").get())
                add("implementation", libs.findLibrary("koin.android").get())
                add("testImplementation", libs.findLibrary("koin.test").get())
                add("testImplementation", libs.findLibrary("koin.test.junit4").get())
                add("androidTestImplementation", libs.findLibrary("koin.test").get())
            }
        }
    }
}
