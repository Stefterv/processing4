plugins {
    id("java")
    kotlin("jvm") version libs.versions.kotlin
    kotlin("plugin.serialization") version "1.9.0"
}


repositories {
    mavenCentral()
    google()
    maven { url = uri("https://jogamp.org/deployment/maven") }
}

dependencies {
    compileOnly(project(":app"))
    implementation(project(":core"))


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
}

tasks.register<Copy>("createMode"){
    dependsOn("jar")
    into(layout.buildDirectory.dir("mode"))

    from(layout.projectDirectory){
        include ("mode.properties")
        include("examples/**")
    }

    from(configurations.runtimeClasspath){
        into("mode")
    }

    from(tasks.jar) {
        into("mode")
    }
}

tasks.register<Copy>("includeMode"){
    dependsOn("createMode")
    from(tasks.named("createMode"))
    into(project(":app").layout.buildDirectory.dir("resources-bundled/common/modes/p5js"))
    project(":app") {
        tasks.named("includeProcessingResources").configure {
            dependsOn(this@register)
        }
    }
}

