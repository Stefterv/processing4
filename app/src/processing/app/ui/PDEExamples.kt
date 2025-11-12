package processing.app.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import processing.app.Base
import processing.app.Language
import processing.app.Mode
import processing.app.Platform
import processing.app.api.Sketch
import processing.app.ui.PDEExamples.Companion.examples
import processing.app.ui.theme.PDEComposeWindow
import processing.app.ui.theme.PDESwingWindow
import java.awt.Dimension
import java.io.File
import javax.swing.SwingUtilities


class PDEExamples {
    companion object {
        @Composable
        fun examples(mode: Mode) {
            val sketches = mutableStateListOf<Sketch.Companion.Folder>()
            LaunchedEffect(mode) {
                val foundSketches = processing.app.api.Mode.findExampleSketches(mode)
                sketches.clear()
                sketches.addAll(foundSketches)
            }
            Text(Language.text("menu.file.examples"))
        }
    }
}

fun show(mode: Mode) {
    SwingUtilities.invokeLater {
        PDESwingWindow(
            unique = mode::class,
            titleKey = "examples",
            size = Dimension(850, 600),
            minSize = Dimension(700, 500),
        ) {
            examples(mode)
        }
    }
}

/**
 * Make sure you run Processing with
 * ```
 * ./gradlew run
 * ```
 * at least once so that the java folder exists
 *
 * or
 *
 * use the Processing run configuration in IDEA
 */
fun main() {
    application {
        // TODO: Migrate to using the actual Java mode from the application
        val folder = File("app/build/resources-bundled/common/modes/java")
        if (!folder.exists()) {
            error("The java mode folder does not exist: ${folder.absolutePath}\nMake sure to run Processing at least once using './gradlew run' or the Processing run configuration in IDEA")
        }
        val javaMode = object : Mode(folder) {
            override fun getIdentifier() = "java"
            override fun getTitle() = "Java"
            override fun createEditor(base: Base?, path: String?, state: EditorState?) = TODO("Not yet implemented")
            override fun getDefaultExtension() = "pde"
            override fun getExtensions() = arrayOf("pde", "java")
            override fun getIgnorable() = Platform.getSupportedVariants().keyArray()
        }
        PDEComposeWindow(
            titleKey = "pde.examples.title",
            size = DpSize(1000.dp, 750.dp)
        ) {
            examples(javaMode)
        }
    }
}