package processing.mode.java.gradle

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.model.GradleProject
import org.gradle.tooling.model.build.BuildEnvironment
import processing.app.Base
import processing.app.Platform
import processing.app.Sketch
import java.awt.Desktop
import java.io.File


class GradleRunner {
    companion object{
        @JvmStatic
        fun build(sketch: Sketch) : String {
            val buildGradle = """
                plugins{
                    id("processing.java.gradle") version "${Base.getVersionName()}"
                }
            """.trimIndent()
            val buildGradleFile = sketch.folder.resolve("build.gradle.kts")
            buildGradleFile.writeText(buildGradle)

            val settingsGradle = """
                pluginManagement {
                    repositories {
                        mavenLocal()
                        mavenCentral()
                    }
                }
            """.trimIndent()
            sketch.folder.resolve("settings.gradle.kts").writeText(settingsGradle)

            // open folder in file explorer
            Desktop.getDesktop().open(sketch.folder)

            val connection = GradleConnector.newConnector()
                .forProjectDirectory(sketch.folder)
                .connect()

            try {
                connection.newBuild()
                    .setJavaHome(Platform.getJavaHome())
                    .forTasks("clean", "sketch")
                    .setStandardOutput(System.out)
                    .run()
            }
            finally {
                connection.close()
            }

            return ""
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val connection = GradleConnector.newConnector()
                .forProjectDirectory(File("java/gradle/example"))
                .connect()

            try {
                val result = connection.newBuild()
                    .forTasks("sketch")
                    .setStandardOutput(System.out)
                    .run()


                println(result)
            } finally {
                connection.close()
            }

        }
    }
}