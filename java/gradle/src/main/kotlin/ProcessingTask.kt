package org.processing.java.gradle

import org.gradle.api.file.*
import org.gradle.api.tasks.*
import org.gradle.internal.file.Deleter
import org.gradle.work.InputChanges
import processing.mode.java.preproc.PdePreprocessor
import java.io.File
import java.util.concurrent.Callable
import java.util.jar.JarFile
import javax.inject.Inject

abstract class ProcessingTask : SourceTask() {
    @get:OutputDirectory
    var outputDirectory: File? = null

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:IgnoreEmptyDirectories
    @get:SkipWhenEmpty
    open val stableSources: FileCollection = project.files(Callable<Any> { this.source })

    @get:Input
    @get:Optional
    var workingDir: String? = null

    @get:Input
    var sketchName: String = "processing"

    @get:Input
    @get:Optional
    var sketchBook: String? = null

    @TaskAction
    fun execute(inputChanges: InputChanges) {
        // Using stableSources since we can only run the pre-processor on the full set of sources
        // TODO: Allow pre-processor to run on individual files

        val unsaved = (project.findProperty("processing.unsaved") as String?)?.split(",") ?: emptyList()

        val combined = stableSources.map { file ->
            if (file.name in unsaved)
                File(workingDir, "unsaved").resolve(file.name)
            else
                file
        }.joinToString("\n"){
            it.readText()
        }
        val javaFile = File(outputDirectory, "$sketchName.java").bufferedWriter()

        val meta = PdePreprocessor
            .builderFor(sketchName)
            .build()
            .write(javaFile, combined)

        javaFile.flush()
        javaFile.close()

        // Scan all the libaries in the sketchbook
        // TODO: Move scanning the libraries to a separate task to avoid running this every time
        val libraries = File(sketchBook, "libraries")
            .listFiles { file -> file.isDirectory }
            ?.map { folder ->
                // Find all the jars in the sketch book
                val jars = folder.resolve("library")
                    .listFiles{ file -> file.extension == "jar" }
                    ?.map{ file ->

                        // Inside of each jar, look for the defined classes
                        val jar = JarFile(file)
                        val classes = jar.entries().asSequence()
                            .filter { entry -> entry.name.endsWith(".class") }
                            .map { entry -> entry.name }
                            .map { it.substringBeforeLast('/').replace('/', '.') }
                            .distinct()
                            .toList()

                        // Return a reference to the jar and its classes
                        return@map object {
                            val name = file.name
                            val path = file
                            val classes = classes
                        }
                    }?: emptyList()

                // Save the parsed jars and which folder
                return@map object {
                    val name = folder.name
                    val path = folder
                    val jars = jars
                }
            }

        // Loop over the import statements and find the library jars that provide those imports
        val dependencies = mutableSetOf<File>()
        meta.importStatements.map { import ->
            libraries?.map { library ->
                library.jars.map { jar ->
                    jar.classes
                        .filter { className -> className.startsWith(import.packageName) }
                        .map { _ ->
                            dependencies.add(jar.path)
                        }
                }
            }
        }
        // Write the dependencies to a file
        val deps = File(outputDirectory, "$sketchName.dependencies")
        deps.writeText(dependencies.joinToString("\n") { it.absolutePath })

        // TODO: Add to the dependencies
        val renderer = meta.sketchRenderer
    }

    @get:Inject
    open val deleter: Deleter
        get() {
            throw UnsupportedOperationException("Decorator takes care of injection")
        }
}