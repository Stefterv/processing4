rootProject.name = "processing"
include(
    "core",
    "core:different",
    "app",
    "java",
    "java:preprocessor",
)

buildscript {
    repositories {
        mavenCentral()
    }
}