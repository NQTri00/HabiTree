plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}
android {
    namespace = "com.mamxanh.app"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.mamxanh.app"
        minSdk = 24
        targetSdk = 36
        // Ship Vietnamese consistently until legacy feature copy is fully translated.
        // English resource translations remain prepared in source, but are not yet a shipping locale.
        resourceConfigurations += listOf("vi")
        versionCode = providers.gradleProperty("mamxanh.versionCode").orElse("2").get().toInt().also { require(it>0) }
        versionName = providers.gradleProperty("mamxanh.versionName").orElse("0.2.0").get()
        val apiBaseUrl = providers.gradleProperty("mamxanh.apiBaseUrl").orElse("").get()
        val encodedUrl = "\"" + apiBaseUrl.replace("\\", "\\\\").replace("\"", "\\\"")
            .replace("\n", "\\n").replace("\r", "\\r") + "\""
        buildConfigField("String", "API_BASE_URL", encodedUrl)
    }
    val signingValues=listOf("MAMXANH_KEYSTORE","MAMXANH_STORE_PASSWORD","MAMXANH_KEY_ALIAS","MAMXANH_KEY_PASSWORD")
        .map{providers.environmentVariable(it).orNull}
    if(signingValues.all{!it.isNullOrBlank()}) {
        signingConfigs.create("production") {
            storeFile=file(signingValues[0]!!)
            storePassword=signingValues[1]
            keyAlias=signingValues[2]
            keyPassword=signingValues[3]
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig=signingConfigs.findByName("production")
        }
        create("verification") {
            initWith(getByName("debug"))
            // A distinct package allows fresh-data checks without resetting an existing garden.
            applicationIdSuffix = providers.gradleProperty("mamxanh.verificationSuffix")
                .orElse(".verification").get().also { suffix ->
                    require(suffix.startsWith(".") && suffix.drop(1).split('.').all {
                        it.matches(Regex("[A-Za-z][A-Za-z0-9_]*"))
                    }) {
                        "Verification suffix must contain dot-separated package segments."
                    }
                }
            matchingFallbacks += listOf("debug")
        }
    }
    buildFeatures { compose = true; buildConfig = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
tasks.configureEach {
    if(name=="preReleaseBuild")doFirst {
        check(listOf("MAMXANH_KEYSTORE","MAMXANH_STORE_PASSWORD","MAMXANH_KEY_ALIAS","MAMXANH_KEY_PASSWORD")
            .all{!providers.environmentVariable(it).orNull.isNullOrBlank()}) {
            "Release requires externally managed signing credentials; debug signing is forbidden."
        }
        check(providers.gradleProperty("mamxanh.versionCode").isPresent &&
            providers.gradleProperty("mamxanh.versionName").isPresent) {
            "Release requires explicit mamxanh.versionCode and mamxanh.versionName."
        }
    }
}
kotlin {
    compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) }
}
dependencies {
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(project(":core-ui"))
    implementation(project(":data"))
    implementation(project(":home"))
    implementation(project(":tasks"))
    implementation(project(":garden"))
    implementation(project(":shop"))
    implementation(project(":tree"))
    implementation(project(":stats"))
    implementation(project(":inventory"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
