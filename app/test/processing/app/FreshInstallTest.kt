package processing.app

import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.Test


class FreshInstallTest {

    /**
     * Test to verify all the files that Processing will create when installing fresh on a new system.
     */
    @Test
    fun testScaffolding() {
        val directory = createTempDirectory("scaffolding")

        val preferences = directory.resolve("preferences")
        preferences.toFile().mkdirs()
        val sketchbook = directory.resolve("sketchbook")
        sketchbook.toFile().mkdirs()

        // TODO: Ultimately we would like to use java resources instead of a folder on the disk.
        val resources = File("../resources-bundled/common")
        if (!resources.exists()) {
            throw IllegalStateException("Resources folder not found at ${resources.absolutePath}")
        }
        System.setProperty("processing.resources.folder", resources.absolutePath)
        System.setProperty("processing.settings.folder", preferences.toAbsolutePath().toString())
        System.setProperty("processing.sketchbook.folder", sketchbook.toAbsolutePath().toString())
        System.setProperty("java.awt.headless", "true")

        // Add the debug flag to open the created directory for manual verification
        if (System.getenv().contains("DEBUG")) {
            // open directory in finder / explorer for manual verification
            if (System.getProperty("os.name").lowercase().contains("mac")) {
                Runtime.getRuntime().exec(arrayOf("open", directory.toAbsolutePath().toString()))
            } else if (System.getProperty("os.name").lowercase().contains("windows")) {
                Runtime.getRuntime().exec(arrayOf("explorer", directory.toAbsolutePath().toString()))
            }
        }

        // call the private static createAndShowGUI from Base
        val baseClass = Class.forName("processing.app.Base")
        val method = baseClass.getDeclaredMethod("createAndShowGUI", Array<String>::class.java)
        method.isAccessible = true
        method.invoke(null, arrayOf<String>())

        val preferencesFile = preferences.resolve("preferences.txt").toFile()
        assert(preferencesFile.exists()) { "preferences.txt file was not created" }
        val librariesFolder = sketchbook.resolve("libraries").toFile()
        assert(librariesFolder.exists()) { "libraries folder was not created" }
        val modesFolder = sketchbook.resolve("modes").toFile()
        assert(modesFolder.exists()) { "modes folder was not created" }
        val toolsFolder = sketchbook.resolve("tools").toFile()
        assert(toolsFolder.exists()) { "tools folder was not created" }
    }
}