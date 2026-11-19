plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.kmp) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

/** Enforce dependency direction for the Android feature layers and shared source. */
tasks.register("verifyArchitecture") {
    group = "verification"
    doLast {
        val features=setOf("home","tasks","garden","shop","tree","stats","inventory","settings")
        val violations=mutableListOf<String>()
        val projectDependency = Regex("""project\("([^"]+)"\)""")
        val featureImport = Regex("""(?m)^\s*import\s+com\.mamxanh\.app\.feature\.([a-z]+)\.""")
        val platformOrOuterImport = Regex(
            """(?m)^\s*import\s+(android\.|androidx\.|io\.ktor\.|com\.mamxanh\.app\.feature\.[a-z]+\.(data|presentation)\.)""",
        )
        val platformDependency = Regex(
            """\b(?:api|implementation|compileOnly|runtimeOnly)\s*\(\s*(?:libs\.(?:androidx|ktor)\.|"(?:androidx\.|com\.android\.|io\.ktor:))""",
        )
        features.forEach { owner->
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
                "" to setOf(":core-ui", ":core-presentation", ":$owner:domain"),
                "domain/" to if (owner in setOf("stats", "settings")) setOf(":core-domain") else setOf(":shared"),
                "data/" to (setOf(":$owner:domain") + when (owner) {
                    "inventory", "settings" -> setOf(":shared")
                    "stats" -> setOf(":core-data", ":shared")
                    else -> emptySet()
                }),
            )
            allowed.forEach { (layer, dependencies) ->
                val script = file("$owner/${layer}build.gradle.kts")
                if (script.exists()) {
                    val contents = script.readText()
                    projectDependency.findAll(contents).forEach { match ->
                        if (match.groupValues[1] !in dependencies) {
                            violations.add("Unexpected dependency in ${script.relativeTo(rootDir)}: ${match.groupValues[1]}")
                        }
                    }
                    if (layer == "domain/" && platformDependency.containsMatchIn(contents)) {
                        violations.add("Platform or transport dependency in ${script.relativeTo(rootDir)}")
                    }
                }
            }
            fileTree("$owner/domain/src").matching { include("**/*.kt") }.forEach { source ->
                if (platformOrOuterImport.containsMatchIn(source.readText())) {
                    violations.add("Platform or outer-layer import in ${source.relativeTo(rootDir)}")
                }
            }
        }
        val coreProjects = listOf("core-config", "core-domain", "core-data", "core-utils", "core-ui", "core-presentation", "core-network", "data")
        coreProjects.forEach { owner ->
            val script = file("$owner/build.gradle.kts")
            projectDependency.findAll(script.readText()).forEach { match ->
                if (match.groupValues[1].substringAfter(':').substringBefore(':') in features + "app") {
                    violations.add("Core dependency points to the app or a feature: ${script.relativeTo(rootDir)}")
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
