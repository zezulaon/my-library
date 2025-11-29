import com.android.build.api.dsl.ApplicationExtension
import dev.zezula.books.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class MyLibraryAppConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.android")

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                buildToolsVersion = "35.0.0"

                defaultConfig {
                    applicationId = "dev.zezula.books"
                    targetSdk = 35

                    vectorDrawables {
                        useSupportLibrary = true
                    }
                }
            }
        }
    }
}
