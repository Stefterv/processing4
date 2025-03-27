package org.processing.java.gradle

import org.gradle.api.file.*
import org.gradle.api.tasks.*
import org.gradle.internal.file.Deleter
import org.gradle.work.InputChanges
import processing.mode.java.preproc.PdePreprocessor
import java.io.File
import java.util.concurrent.Callable
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
        File(outputDirectory, "$sketchName.java")
            .bufferedWriter()
            .use { out ->
                val meta = PdePreprocessor
                    .builderFor(sketchName)
                    .build()
                    .write(out, combined)


                val importStatement = meta.importStatements
                println(sketchBook)

//                for (import in importStatement) {
//                    project.dependencies.add("implementation", import)
//                }
            }
    }

    @get:Inject
    open val deleter: Deleter
        get() {
            throw UnsupportedOperationException("Decorator takes care of injection")
        }
}