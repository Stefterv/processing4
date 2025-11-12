package processing.app.api

import processing.app.Mode
import java.io.File
import java.nio.file.Files
import kotlin.io.path.isDirectory

class Mode {
    companion object {
        /**
         * Find sketches in the given root folder for the specified mode.
         *
         * Based on ExamplesFrame.buildTree()
         *
         * @param root The root directory to search for sketches.
         * @param mode The mode to filter sketches by.
         * @return A list of sketch folders found in the root directory.
         */
        fun findExampleSketches(
            mode: Mode
        ): List<Sketch.Companion.Folder> {
            val baseExamples = mode.exampleCategoryFolders.mapNotNull {
                searchForSketches(it, mode)
            }

            val coreLibraryExamples = mode.coreLibraries.mapNotNull {
                searchForSketches(it.examplesFolder, mode)
            }

            val contributedLibraryExamples = mode.contribLibraries.mapNotNull {
                searchForSketches(it.examplesFolder, mode)
            }
//            val contributedExamplePacks = Base.getSketchbookExamplesFolder()?.let { root ->
//                ContributionType.EXAMPLES.listCandidates(root).mapNotNull {
//                    searchForSketches(it, mode)
//                }
//            }
            return emptyList()
        }

        /**
         * Find sketches in the given root folder for the specified mode.
         *
         * Based on Base.addSketches()
         *
         * @param root The root directory to search for sketches.
         * @param mode The mode to filter sketches by.
         * @return A list of sketch folders found in the root directory.
         */
        fun searchForSketches(
            root: File,
            mode: Mode,
            filter: ((File) -> Boolean) = { true }
        ): Sketch.Companion.Folder? {
            if (!root.isDirectory) return null
            if (!filter(root)) return null

            val stream = Files.newDirectoryStream(root.toPath())
            val (sketchFolders, subfolders) = stream
                .filter { path -> path.isDirectory() }
                .filter { path -> filter(path.toFile()) }
                .partition { path ->
                    val main = processing.app.Sketch.findMain(path.toFile(), listOf(mode))
                    main != null
                }
            val sketches = sketchFolders.map {
                Sketch.Companion.Sketch(
                    name = it.fileName.toString(),
                    path = it.toString(),
                    mode = mode.identifier
                )
            }
            val children = subfolders.mapNotNull {
                searchForSketches(it.toFile(), mode, filter)
            }
            if (sketches.isEmpty() && children.isEmpty()) return null
            return Sketch.Companion.Folder(
                name = root.name,
                path = root.toString(),
                sketches = sketches,
                children = children
            )
        }
    }
}