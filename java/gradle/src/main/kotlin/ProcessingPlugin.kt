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

// TODO: CI/CD for publishing the plugin
class ProcessingPlugin @Inject constructor(private val objectFactory: ObjectFactory) : Plugin<Project> {
    override fun apply(project: Project) {
        val sketchName = project.layout.projectDirectory.asFile.name.replace(Regex("[^a-zA-Z0-9_]"), "_")

        val isProcessing = project.findProperty("processing.version") != null
        val processingVersion = project.findProperty("processing.version") as String? ?: "4.3.4"
        val processingGroup = project.findProperty("processing.group") as String? ?: "org.processing"
        val workingDir = project.findProperty("processing.workingDir") as String?
        val debugPort = project.findProperty("processing.debugPort") as String?

        val osName = System.getProperty("os.name").lowercase()

        // Grab the settings from the most likely location if not defined
        var settingsFolder = (project.findProperty("processing.settings") as String?)?.let { File(it) }
        if(settingsFolder == null) {
            if (osName.contains("win")) {
                settingsFolder = File(System.getenv("APPDATA"), "Processing")
            } else if (osName.contains("mac")) {
                settingsFolder = File(System.getProperty("user.home"), "Library/Processing")
            } else if (osName.contains("nix") || osName.contains("nux")) {
                settingsFolder = File(System.getProperty("user.home"), ".processing")
            }
        }

        // Overwrite the preferences as the preprocessor still uses the old PDE settings
        // TODO: Replace these settings in the preprocessor instead
        val preferences = File(settingsFolder, "preferences.txt")
        val prefs = Properties()
        if(preferences.exists()) prefs.load(preferences.inputStream())
        prefs.setProperty("export.application.fullscreen", "false")
        prefs.setProperty("export.application.present", "false")
        prefs.setProperty("export.application.stop", "false")
        if(preferences.exists()) prefs.store(preferences.outputStream(), null)

        val sketchbook = project.findProperty("processing.sketchbook") as String?
                                ?: prefs.getProperty("sketchbook.path.four")
                                ?: ("${System.getProperty("user.home")}/.processing")

        // Replace slashes with backslashes on Windows
        if (osName.contains("win")) {
            sketchbook.replace("/", "\\")
        }

        // Apply the Java plugin to the Project
        project.plugins.apply(JavaPlugin::class.java)

        if(isProcessing){
            // Set the build directory to a temp file so it doesn't clutter up the sketch folder
            // Only if the build directory doesn't exist, otherwise proceed as normal
            if(!project.layout.buildDirectory.asFile.get().exists()) {
                project.layout.buildDirectory.set(File(project.findProperty("processing.workingDir") as String))
            }
            // Disable the wrapper in the sketch to keep it cleaner
            project.tasks.findByName("wrapper")?.enabled = false
        }

        // Add the compose plugin to wrap the sketch in an executable
        project.plugins.apply("org.jetbrains.compose")

        // Add kotlin support
        project.plugins.apply("org.jetbrains.kotlin.jvm")
        // Add jetpack compose support
        project.plugins.apply("org.jetbrains.kotlin.plugin.compose")

        // Add the Processing core library (within Processing from the internal maven repo and outside from the internet)
        project.dependencies.add("implementation", "$processingGroup:core:${processingVersion}")

        // Add the jars in the code folder
        project.dependencies.add("implementation", project.fileTree("src").apply { include("**/code/*.jar") })

        // TODO: Add support for grabbing Processing internal maven repo even if the user is not using the IDE
        // Add the repositories necessary for building the sketch
        project.repositories.add(project.repositories.maven { it.setUrl("https://jogamp.org/deployment/maven") })
        project.repositories.add(project.repositories.mavenCentral())
        project.repositories.add(project.repositories.mavenLocal())

        // Configure the compose Plugin
        project.extensions.configure(ComposeExtension::class.java) { extension ->
            extension.extensions.getByType(DesktopExtension::class.java).application { application ->
                // Set the class to be executed initially
                application.mainClass = sketchName
                application.nativeDistributions.modules("java.management")
                if(debugPort != null) {
                    application.jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=$debugPort")
                }
            }
        }

        // TODO: Add support for top level .java files
        // TODO: Add support for customizing distributables
        // TODO: Setup sensible defaults for the distributables

        // Add convenience tasks for running, presenting, and exporting the sketch outside of Processing
        if(!isProcessing) {
            project.tasks.create("sketch").apply {
                group = "processing"
                description = "Runs the Processing sketch"
                dependsOn("run")
            }
            project.tasks.create("present").apply {
                group = "processing"
                description = "Presents the Processing sketch"
                doFirst {
                    project.tasks.withType(JavaExec::class.java).configureEach { task ->
                        task.systemProperty("processing.fullscreen", "true")
                    }
                }
                finalizedBy("run")
            }
            project.tasks.create("export").apply {
                group = "processing"
                description = "Creates a distributable version of the Processing sketch"

                dependsOn("createDistributable")

            }
        }

        project.afterEvaluate {
            // Copy the result of create distributable to the project directory
            project.tasks.named("createDistributable") { task ->
                task.doLast {
                    project.copy {
                        it.from(project.tasks.named("createDistributable").get().outputs.files)
                        it.into(project.layout.projectDirectory)
                    }
                }
            }
        }

        // Move the processing variables into javaexec tasks so they can be used in the sketch as well
        project.tasks.withType(JavaExec::class.java).configureEach { task ->
            project.properties
                .filterKeys { it.startsWith("processing") }
                .forEach { (key, value) -> task.systemProperty(key, value) }
        }

        project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.all { sourceSet ->
            // For each java source set (mostly main) add a new source set for the PDE files
            val pdeSourceSet = objectFactory.newInstance(
                DefaultPDESourceDirectorySet::class.java,
                objectFactory.sourceDirectorySet("${sourceSet.name}.pde", "${sourceSet.name} Processing Source")
            ).apply {
                filter.include("**/*.pde")
                filter.exclude("${project.layout.buildDirectory.asFile.get().name}/**")

                srcDir("./")
                srcDir("$workingDir/unsaved")
            }
            sourceSet.allSource.source(pdeSourceSet)

            val librariesTaskName = sourceSet.getTaskName("scanLibraries", "PDE")
            val librariesScan = project.tasks.register(librariesTaskName, LibrariesTask::class.java) { task ->
                task.description = "Scans the libraries in the sketchbook"
                task.librariesDirectory.set(File(sketchbook, "libraries"))
            }

            val pdeTaskName = sourceSet.getTaskName("preprocess", "PDE")
            val pdeTask = project.tasks.register(pdeTaskName, PDETask::class.java) { task ->
                task.description = "Processes the ${sourceSet.name} PDE"
                task.source = pdeSourceSet
                task.sketchName = sketchName
                task.workingDir = workingDir
                task.sketchBook = sketchbook

                // Set the output of the pre-processor as the input for the java compiler
                sourceSet.java.srcDir(task.outputDirectory)
            }

            val depsTaskName = sourceSet.getTaskName("addLegacyDependencies", "PDE")
            project.tasks.register(depsTaskName, DependenciesTask::class.java){ task ->
                task.dependsOn(pdeTask, librariesScan)
            }

            project.tasks.named(
                sourceSet.compileJavaTaskName
            ) { task ->
                task.dependsOn(pdeTaskName, depsTaskName)
            }
        }
    }
    abstract class DefaultPDESourceDirectorySet @Inject constructor(
        sourceDirectorySet: SourceDirectorySet,
        taskDependencyFactory: TaskDependencyFactory
    ) : DefaultSourceDirectorySet(sourceDirectorySet, taskDependencyFactory), SourceDirectorySet
}

