pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri( "https://s3-ap-southeast-1.amazonaws.com/godel-release/godel/")
        }

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven { url = uri("https://jitpack.io") }
        maven(url = "<https://jitpack.io>")
        maven {
            url = uri( "https://maven.zohodl.com")
        }
        maven {
            url = uri( "https://s3-ap-southeast-1.amazonaws.com/godel-release/godel/")
        }


        mavenCentral()

        maven { url= uri("https://storage.zego.im/maven") }   // <- Add this line.

    }
}

rootProject.name = "DudeWays_MVVM"
include(":app")
