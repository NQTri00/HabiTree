plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp)
}

kotlin {
    android {
        namespace = "com.mamxanh.shared"
        compileSdk = 36
        minSdk = 24
        withHostTestBuilder {}.configure {}
        compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) }
    }
    listOf(iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework {
            baseName = "MamXanhShared"
            isStatic = true
            export(project(":core-network"))
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            api(project(":core-network"))
        }
        commonTest.dependencies { implementation(kotlin("test")) }
        iosMain.dependencies {
            implementation(libs.sqldelight.native)
        }
    }
}
