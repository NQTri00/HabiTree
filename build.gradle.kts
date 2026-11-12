plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.kmp) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

/** Enforce Phase 5 ownership without moving stable feature APIs or gameplay rules. */
tasks.register("verifyArchitecture") {
    group = "verification"
    doLast {
        val features=setOf("home","tasks","garden","shop","tree","stats","inventory")
        val violations=mutableListOf<String>()
        features.forEach { owner->
            fileTree("$owner/src").matching{include("**/*.kt")}.forEach { source->
                Regex("""import com\.mamxanh\.app\.feature\.([a-z]+)\..*ViewModel""").findAll(source.readText()).forEach { match->
                    if(match.groupValues[1]!=owner)violations.add("Cross-feature ViewModel: ${source.relativeTo(rootDir)}")
                }
            }
            val script=file("$owner/build.gradle.kts").readText()
            features.filter{it!=owner}.forEach{other->
                if(script.contains("project(\":$other\")"))violations.add("$owner depends on feature $other")
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
