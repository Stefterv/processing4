package org.processing.java.android

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProcessingAndroidPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply("com.android.application")

        project.extensions.configure(AppExtension::class.java){ extension ->
            extension.compileSdkVersion(34)
            extension.namespace = "org.processing.discovery.minimal"

        }
    }
}