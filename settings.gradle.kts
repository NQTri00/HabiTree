pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "MamXanh"
include(":app", ":core-ui", ":domain", ":data", ":home", ":tasks", ":garden", ":shop", ":tree", ":stats")
