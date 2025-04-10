pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral() // Để resolve Ktor và Kotlin libs
        maven("https://jitpack.io") // ✅ Thêm dòng này để resolve Supabase SDK
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // ✅ Thêm dòng này ở đây nữa
    }
}

rootProject.name = "YoloV9 Tflite"
include(":app")
