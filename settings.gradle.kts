pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "mediaplayer-mvvm-clean-xml"

include(":app")
include(":domain")
include(":data")
include(":core:common")
include(":core:ui")
include(":core:database")
include(":core:datastore")
include(":core:media")
include(":feature:library")
include(":feature:player")
include(":feature:videoplayer")
