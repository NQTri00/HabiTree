import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ProjectDependency

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.kmp) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// Each feature ships three artifacts at one version. Core libraries version independently.
val androidArtifactVersions = mapOf(
    ":core-config" to "0.1.0",
    ":core-domain" to "0.1.0",
    ":core-utils" to "0.1.0",
    ":core-data" to "0.1.0",
    ":core-ui" to "0.1.1",
    ":core-presentation" to "0.1.0",
    ":core-storage" to "0.1.1",
    ":core-network" to "0.1.0",
    ":shared" to "0.1.1",
    ":home" to "0.1.1",
    ":garden" to "0.1.1",
    ":tasks" to "0.1.1",
    ":shop" to "0.1.1",
    ":tree" to "0.1.1",
    ":stats" to "0.1.1",
    ":inventory" to "0.1.1",
    ":settings" to "0.1.1",
)
val localPublicationRepository = providers.gradleProperty("habitreePublicationRepository")
    .map { layout.projectDirectory.dir(it) }
    .orElse(layout.buildDirectory.dir("habitree-maven"))

subprojects {
    val projectPath = path
    val segments = projectPath.removePrefix(":").split(':')
    val owner = segments.first()
    val artifactVersion = androidArtifactVersions[":$owner"]
    if (artifactVersion != null) {
        val publicationGroup = "com.mamxanh.habitree.android"
        // Local Gradle identities for sibling projects named "domain" or "data" must be distinct.
        group = if (segments.size == 1) publicationGroup else "$publicationGroup.$owner"
        version = artifactVersion
        pluginManager.apply("maven-publish")
        val publishing = extensions.getByType<PublishingExtension>()
        publishing.repositories.maven {
            name = "HabiTreeLocal"
            url = localPublicationRepository.get().asFile.toURI()
        }
        val artifact = if (segments.size == 1 && findProject("$projectPath:domain") != null) {
            "$owner-presentation"
        } else {
            segments.joinToString("-")
        }

        pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            publishing.publications.create<MavenPublication>("habitree") {
                groupId = publicationGroup
                artifactId = artifact
                from(components.getByName("java"))
            }
        }
        pluginManager.withPlugin("com.android.library") {
            extensions.getByType<LibraryExtension>().publishing.singleVariant("release")
            components.matching { it.name == "release" }.all {
                val releaseComponent = this
                publishing.publications.create<MavenPublication>("habitree") {
                    groupId = publicationGroup
                    artifactId = artifact
                    from(releaseComponent)
                }
            }
        }
    }
}

/** Enforce dependency direction for the Android feature layers and shared source. */
tasks.register("verifyArchitecture") {
    group = "verification"
    doLast {
        val features=setOf("home","tasks","garden","shop","tree","stats","inventory","settings")
        val violations=mutableListOf<String>()
        val declarationSuffixes = listOf("api", "implementation", "compileOnly", "runtimeOnly")
        fun declarations(path: String) = project(path).configurations
            .filter { configuration -> declarationSuffixes.any { configuration.name.endsWith(it, ignoreCase = true) } }
            .flatMap { it.dependencies }
            .distinct()
        val featureImport = Regex("""(?m)^\s*import\s+com\.mamxanh\.app\.feature\.([a-z]+)\.""")
        val platformOrOuterImport = Regex(
            """(?m)^\s*import\s+(android\.|androidx\.|io\.ktor\.|com\.mamxanh\.shared\.|com\.mamxanh\.app\.feature\.[a-z]+\.(data|presentation)\.)""",
        )
        features.forEach { owner ->
            val domainPlugins = project(":$owner:domain").pluginManager
            if (!domainPlugins.hasPlugin("org.jetbrains.kotlin.jvm") ||
                domainPlugins.hasPlugin("com.android.library") ||
                domainPlugins.hasPlugin("com.android.kotlin.multiplatform.library")
            ) {
                violations.add("Feature domain must use Kotlin/JVM: :$owner:domain")
            }
            listOf("src", "domain/src", "data/src").forEach { sourceRoot ->
                fileTree("$owner/$sourceRoot").matching { include("**/*.kt") }.forEach { source ->
                    featureImport.findAll(source.readText()).forEach { match ->
                        if (match.groupValues[1] != owner) {
                            violations.add("Cross-feature import: ${source.relativeTo(rootDir)}")
                        }
                    }
                }
            }
            val allowed = mapOf(
                ":$owner" to setOf(":core-ui", ":core-presentation", ":$owner:domain"),
                ":$owner:domain" to when (owner) {
                    "shop", "tree", "stats", "settings", "tasks", "home" -> setOf(":core-domain")
                    else -> emptySet()
                },
                ":$owner:data" to (setOf(":$owner:domain") + when (owner) {
                    "inventory", "shop", "tree", "settings", "home", "garden", "tasks" -> setOf(":shared")
                    "stats" -> setOf(":core-data", ":shared")
                    else -> emptySet()
                }),
            )
            allowed.forEach { (path, dependencies) ->
                val declared = declarations(path)
                declared.filterIsInstance<ProjectDependency>().forEach { dependency ->
                    if (dependency.path != path && dependency.path !in dependencies) {
                        violations.add("Unexpected dependency in $path: ${dependency.path}")
                    }
                }
                if (path.endsWith(":domain")) {
                    declared.filterIsInstance<ExternalModuleDependency>().forEach { dependency ->
                        val group = dependency.group.orEmpty()
                        if (group.startsWith("androidx.") || group.startsWith("com.android.") || group == "io.ktor") {
                            violations.add("Platform or transport dependency in $path: $group:${dependency.name}")
                        }
                    }
                }
            }
            fileTree("$owner/domain/src").matching { include("**/*.kt") }.forEach { source ->
                if (platformOrOuterImport.containsMatchIn(source.readText())) {
                    violations.add("Platform, shared storage, or outer-layer import in ${source.relativeTo(rootDir)}")
                }
            }
        }
        val coreProjects = listOf("core-config", "core-domain", "core-data", "core-utils", "core-ui", "core-presentation", "core-network", "data")
        coreProjects.forEach { owner ->
            val path = if (owner == "data") ":core-storage" else ":$owner"
            declarations(path).filterIsInstance<ProjectDependency>().forEach { dependency ->
                if (dependency.path.substringAfter(':').substringBefore(':') in features + "app") {
                    violations.add("Core dependency points to the app or a feature: $path -> ${dependency.path}")
                }
            }
            fileTree("$owner/src").matching { include("**/*.kt") }.forEach { source ->
                if (featureImport.containsMatchIn(source.readText())) {
                    violations.add("Feature import in core: ${source.relativeTo(rootDir)}")
                }
            }
        }
        fileTree("shared/src/commonMain").matching{include("**/*.kt")}.forEach { source->
            if(Regex("""(?m)^import (android\.|androidx\.|java\.|javax\.)""").containsMatchIn(source.readText()))
                violations.add("Platform import in commonMain: ${source.relativeTo(rootDir)}")
        }
        check(violations.isEmpty()){violations.joinToString("\n")}
        println("Shared/platform ownership and feature dependency boundaries passed")
    }
}
