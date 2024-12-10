import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins{
    id("java")

    kotlin("jvm") version libs.versions.kotlin
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.serialization)
}

group = rootProject.group

repositories{
    mavenCentral()
    google()
    maven { url = uri("https://jogamp.org/deployment/maven") }
}

sourceSets{
    main{
        java{
            srcDirs("src")
        }
        kotlin{
            srcDirs("src")
        }
        resources{
            srcDirs("src","../build/shared/")
        }
    }
}

compose.desktop {
    application {
        mainClass = "processing.app.ui.Splash"

        nativeDistributions{
            includeAllModules = true
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Processing"
            packageVersion = rootProject.version as String

            macOS{
                bundleID = "org.processingfoundation.processing.app"
                iconFile = project.file("../build/macos/processing.icns")
            }
            windows{
                iconFile = project.file("../build/windows/processing.ico")
            }
            linux {
                iconFile = project.file("../build/linux/processing.png")
            }

            // Allow swing to use the system look and feel
            jvmArgs(
                "-Dapple.awt.application.appearance=system"
            )
        }
    }
}

val compottieVersion = "2.0.0-rc02"

dependencies {
    implementation("com.formdev:flatlaf:3.4.1")

    implementation("net.java.dev.jna:jna:5.12.1")
    implementation("net.java.dev.jna:jna-platform:5.12.1")

    implementation(project(":core"))
    runtimeOnly(project(":java"))
    implementation(project(":java:preprocessor"))

    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.ui)
    implementation(compose.components.resources)
    implementation(compose.components.uiToolingPreview)

    implementation(compose.desktop.currentOs)

    implementation("io.github.alexzhirkevich:compottie:${compottieVersion}")

    implementation("com.charleskorn.kaml:kaml:0.65.0")
}

tasks.register<Copy>("addCore"){
    dependsOn(project(":core").tasks.jar)
    from("../core/build/libs/")
    include("*.jar")
    into("build/resources/main/core/library")
}
tasks.jar { dependsOn("addCore") }
tasks.processResources{ finalizedBy("addCore") }

