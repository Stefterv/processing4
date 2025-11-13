package processing.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import java.io.File

val LocalSketchbooks = compositionLocalOf<List<File>> { error("No sketchbook provided") }

@Composable
fun SketchbookProvider() {
    val sketchbookFileOverride: File? = System.getProperty("processing.app.sketchbook.file")?.let { File(it) }

    val prefs = LocalPreferences.current




}