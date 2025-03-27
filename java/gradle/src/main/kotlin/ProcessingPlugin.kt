package org.processing.java.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.tasks.TaskDependencyFactory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.JavaExec
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.desktop.DesktopExtension
import java.io.File
import java.util.*
import javax.inject.Inject

class ProcessingPlugin @Inject constructor(private val objectFactory: ObjectFactory) : Plugin<Project> {
    override fun apply(project: Project) {
        val isProcessing = project.findProperty("processing.version") != null
        val processingVersion = project.findProperty("processing.version") as String? ?: "4.0.0"
        val processingGroup = project.findProperty("processing.group") as String? ?: "org.processing"

        project.plugins.apply(JavaPlugin::class.java)

        if(isProcessing){
            project.layout.buildDirectory.set(File(project.findProperty("processing.workingDir") as String))
            project.tasks.findByName("wrapper")?.enabled = false
        }

        project.plugins.apply("org.jetbrains.compose")
        project.plugins.apply("org.jetbrains.kotlin.jvm")
        project.plugins.apply("org.jetbrains.kotlin.plugin.compose")

        // TODO: Add to tests
        project.dependencies.add("implementation", "$processingGroup:core:${processingVersion}")
        // TODO: Add tests to test if code jars are working
        project.dependencies.add("implementation", project.fileTree("src").apply { include("**/code/*.jar") })

        // Base JOGL and Gluegen dependencies
        // TODO: Add only if user is compiling for P2D or P3D
        project.dependencies.add("runtimeOnly", "org.jogamp.jogl:jogl-all-main:2.5.0")
        project.dependencies.add("runtimeOnly", "org.jogamp.gluegen:gluegen-rt-main:2.5.0")

        // TODO: Only add the native dependencies for the platform the user is building for

        // MacOS specific native dependencies
        project.dependencies.add("runtimeOnly", "org.jogamp.jogl:jogl-all:2.5.0:natives-macosx-universal")
        project.dependencies.add("runtimeOnly", "org.jogamp.gluegen:gluegen-rt:2.5.0:natives-macosx-universal")

        // Windows specific native dependencies
        project.dependencies.add("runtimeOnly", "org.jogamp.jogl:jogl-all:2.5.0:natives-windows-amd64")
        project.dependencies.add("runtimeOnly", "org.jogamp.gluegen:gluegen-rt:2.5.0:natives-windows-amd64")

        // Linux specific native dependencies
        project.dependencies.add("runtimeOnly", "org.jogamp.jogl:jogl-all:2.5.0:natives-linux-amd64")
        project.dependencies.add("runtimeOnly", "org.jogamp.gluegen:gluegen-rt:2.5.0:natives-linux-amd64")

        // NativeWindow dependencies for all platforms
        project.dependencies.add("implementation", "org.jogamp.jogl:nativewindow:2.5.0")
        project.dependencies.add("runtimeOnly", "org.jogamp.jogl:nativewindow:2.5.0:natives-macosx-universal")
        project.dependencies.add("runtimeOnly", "org.jogamp.jogl:nativewindow:2.5.0:natives-windows-amd64")
        project.dependencies.add("runtimeOnly", "org.jogamp.jogl:nativewindow:2.5.0:natives-linux-amd64")

        project.repositories.add(project.repositories.maven { it.setUrl("https://jogamp.org/deployment/maven") })
        project.repositories.add(project.repositories.mavenCentral())
        project.repositories.add(project.repositories.mavenLocal())

        project.extensions.configure(ComposeExtension::class.java) { extension ->
            extension.extensions.getByType(DesktopExtension::class.java).application { application ->
                application.mainClass = project.layout.projectDirectory.asFile.name.replace(Regex("[^a-zA-Z0-9_]"), "_")
                application.nativeDistributions.modules("java.management")
            }
        }

        project.tasks.create("sketch").apply {
            group = "processing"
            description = "Runs the Processing sketch"
            dependsOn("run")
        }
        project.tasks.create("present").apply {
            group = "processing"
            description = "Presents the Processing sketch"
            doFirst{
                project.tasks.withType(JavaExec::class.java).configureEach{ task ->
                    task.systemProperty("processing.fullscreen", "true")
                }
            }
            finalizedBy("run")
        }
        project.tasks.create("export").apply {
            group = "processing"
            description = "Creates a distributable version of the Processing sketch"

            dependsOn("createDistributable")
            doLast{
                project.copy {
                    it.from(project.tasks.named("createDistributable").get().outputs.files)
                    it.into(project.layout.projectDirectory)
                }
            }
        }

        project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.all { sourceSet ->
            // TODO: also supporting normal gradle setup
            val pdeSourceSet = objectFactory.newInstance(
                DefaultPDESourceDirectorySet::class.java,
                objectFactory.sourceDirectorySet("${sourceSet.name}.pde", "${sourceSet.name} Processing Source")
            ).apply {
                filter.include("**/*.pde")
                filter.exclude("${project.layout.buildDirectory.asFile.get().name}/**")

                srcDir("./")
            }
            sourceSet.allSource.source(pdeSourceSet)

            val outputDirectory = project.layout.buildDirectory.file( "generated/pde/" + sourceSet.name).get().asFile
            sourceSet.java.srcDir(outputDirectory)

            val taskName = sourceSet.getTaskName("preprocess", "PDE")
            project.tasks.register(taskName, ProcessingTask::class.java) { task ->
                task.description = "Processes the ${sourceSet.name} PDE"
                task.source = pdeSourceSet
                task.outputDirectory = outputDirectory
            }

            project.tasks.named(
                sourceSet.compileJavaTaskName
            ) { task -> task.dependsOn(taskName) }
        }

        // TODO: get this data from code used within the editor
        var settingsFolder = File(System.getProperty("user.home"),".processing")
        val osName = System.getProperty("os.name").lowercase()
        if (osName.contains("win")) {
            settingsFolder = File(System.getenv("APPDATA"), "Processing")
        } else if (osName.contains("mac")) {
            settingsFolder = File(System.getProperty("user.home"), "Library/Processing")
        }else if (osName.contains("nix") || osName.contains("nux")) {
            settingsFolder = File(System.getProperty("user.home"), ".processing")
        }

        val preferences = File(settingsFolder, "preferences.txt")
        val prefs = Properties()
        prefs.load(preferences.inputStream())
        prefs.setProperty("export.application.fullscreen", "false")
        prefs.setProperty("export.application.present", "false")
        prefs.setProperty("export.application.stop", "false")
        prefs.store(preferences.outputStream(), null)

        val sketchbook = prefs.getProperty("sketchbook.path.four")

        // TODO: Move to ProcessingTask after reading the libs from the sketch
        File(sketchbook, "libraries").listFiles { file -> file.isDirectory }?.forEach{
            project.dependencies.add("implementation", project.fileTree(it).apply { include("**/*.jar") })
        }
    }
    abstract class DefaultPDESourceDirectorySet @Inject constructor(
        sourceDirectorySet: SourceDirectorySet,
        taskDependencyFactory: TaskDependencyFactory
    ) : DefaultSourceDirectorySet(sourceDirectorySet, taskDependencyFactory), SourceDirectorySet
}

