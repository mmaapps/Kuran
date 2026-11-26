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

rootProject.name = "KuranKerim"
include(":app")
// Tilâvet sesleri: Google Play üzerinden uygulamayla birlikte yüklenen install-time asset pack
include(":quran_audio")
