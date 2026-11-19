pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "HabiTree"
include(":core-config", ":core-utils")
include(":shared", ":app", ":core-ui", ":core-presentation", ":core-network", ":core-storage", ":home", ":tasks", ":garden", ":shop", ":tree", ":stats", ":inventory", ":settings")
include(":home:domain", ":home:data")
include(":tasks:domain", ":tasks:data")
include(":garden:domain", ":garden:data")
include(":shop:domain", ":shop:data")
include(":tree:domain", ":tree:data")
include(":stats:domain", ":stats:data")
include(":inventory:domain", ":inventory:data")
project(":core-storage").projectDir = file("data")
