pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "My Library"

include(":app")
include(":domain")
include(":core-model")
include(":core-utils")
include(":data")
include(":data-database")
include(":data-network")
include(":legacy")
