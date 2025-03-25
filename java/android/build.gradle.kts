plugins{
    `java-gradle-plugin`
    alias(libs.plugins.gradlePublish)

    kotlin("jvm") version libs.versions.kotlin
}

repositories {
    mavenCentral()
    google()
}

dependencies{
    implementation(project(":java:preprocessor"))
    implementation(project(":java:gradle"))

    implementation(libs.androidPlugin)
    implementation(libs.androidKotlinPlugin)
}

gradlePlugin{
    plugins{
        create("processing"){
            id = "org.processing.java.android"
            implementationClass = "org.processing.java.android.ProcessingAndroidPlugin"
            repositories{
                mavenCentral()
                google()
            }
        }
    }
}
publishing{
    repositories{
        mavenLocal()
    }
}