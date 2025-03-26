package processing.app.gradle.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpace
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import processing.app.ui.Editor
import processing.app.ui.EditorToolbar
import processing.app.ui.Theme
import javax.swing.JComponent

class Toolbar(val editor: Editor) {
    companion object{
        @JvmStatic
        fun legacyWrapped(editor: Editor, toolbar: EditorToolbar): JComponent {
            val bar = Toolbar(editor)
            val panel = ComposePanel().apply {
                setContent {
                    // TODO: Dynamically switch between the toolbars
                    val displayNew = true
                    if(displayNew){
                        bar.display()
                        return@setContent
                    }
                    SwingPanel(factory = {
                        toolbar
                    }, modifier = Modifier.fillMaxWidth().height(56.dp))
                }
            }

            return panel
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun display(){

        val startColor = Theme.getColor("toolbar.gradient.top")
        val endColor = Theme.getColor("toolbar.gradient.bottom")
        val colorStops = arrayOf(
            0.0f to startColor.toComposeColor(),
            1f to endColor.toComposeColor()
        )
        Row(
            modifier = Modifier.background(Brush.verticalGradient(colorStops = colorStops))
                .fillMaxWidth()
                .padding(start = Editor.LEFT_GUTTER.dp)
                .padding(vertical = 11.dp)
            ,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            val available = editor.service.availableTasks
            val finished = editor.service.finishedTasks
            val isRunning = editor.service.running.value
            Surface(
                modifier = Modifier
                    .onPointerEvent(PointerEventType.Press){
                        editor.service.run()
                    }
                    .height(34.dp)
                    .clip(CircleShape)
                    .aspectRatio(1f)
                    .background(Color.White)

            ){
                if(isRunning) {
                    CircularProgressIndicator(
                        progress = finished.count().toFloat() / available.count() ,
                        color = startColor.toComposeColor()
                    )
                }
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(2.dp)
                )
            }
            if(isRunning){
                Surface(
                    modifier = Modifier
                        .onPointerEvent(PointerEventType.Press){
                            editor.service.stop()
                        }
                        .height(34.dp)
                        .clip(CircleShape)
                        .aspectRatio(1f)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Stop",
                        tint = Color.Black,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

fun java.awt.Color.toComposeColor(): Color {
    return Color(
        red = this.red / 255f,
        green = this.green / 255f,
        blue = this.blue / 255f,
        alpha = this.alpha / 255f
    )
}