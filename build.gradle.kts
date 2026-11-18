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
        features.forEach { owner->
            fileTree("$owner/src").matching{include("**/*.kt")}.forEach { source->
                Regex("""(?m)^import com\.mamxanh\.app\.feature\.([a-z]+)\.""").findAll(source.readText()).forEach { match->
                    if(match.groupValues[1]!=owner)violations.add("Cross-feature import: ${source.relativeTo(rootDir)}")
                }
            }
            val allowed = mapOf(
                "" to setOf(":core-ui", ":core-presentation", ":$owner:domain"),
                "domain/" to setOf(":shared"),
                "data/" to (setOf(":$owner:domain") + if (owner == "inventory") setOf(":shared") else emptySet()),
            )
            allowed.forEach { (layer, dependencies) ->
                val script = file("$owner/${layer}build.gradle.kts")
                if (script.exists()) {
                    Regex("""project\("([^"]+)"\)""").findAll(script.readText()).forEach { match ->
                        if (match.groupValues[1] !in dependencies) {
                            violations.add("Unexpected dependency in ${script.relativeTo(rootDir)}: ${match.groupValues[1]}")
                        }
                    }
                }
            }
            fileTree("$owner/domain/src").matching { include("**/*.kt") }.forEach { source ->
                if (Regex("""(?m)^import (android\.|androidx\.|com\.mamxanh\.app\.feature\.[a-z]+\.(data|presentation)\.)""")
                        .containsMatchIn(source.readText())) {
                    violations.add("Platform or outer-layer import in ${source.relativeTo(rootDir)}")
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
