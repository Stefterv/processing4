package processing.app.gradle.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import processing.app.Base
import processing.app.ui.Editor
import processing.app.ui.EditorToolbar
import processing.app.ui.Theme
import processing.app.ui.theme.toColorInt
import javax.swing.JComponent

class Toolbar(val editor: Editor) {
    companion object{
        @JvmStatic
        fun legacyWrapped(editor: Editor, toolbar: EditorToolbar): JComponent {
            // TODO: Somehow override the menubar items as well

            val bar = Toolbar(editor)
            val panel = ComposePanel().apply {
                setContent {
                    // TODO: Dynamically switch between the toolbars
                    val displayNew = Base.GRADLE
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
        val startColor = Theme.get("toolbar.gradient.top").toColorInt()
        val endColor = Theme.get("toolbar.gradient.bottom").toColorInt()
        val colorStops = arrayOf(
            0.0f to Color(startColor),
            1f to Color(endColor)
        )

        Row(
            modifier = Modifier.background(Brush.verticalGradient(colorStops = colorStops))
                .fillMaxWidth()
                .padding(start = Editor.LEFT_GUTTER.dp)
                .padding(vertical = 11.dp)
            ,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            val available = editor.service.availableTasks.count()
            val finished = editor.service.finishedTasks.count()
            val isRunning = editor.service.running.value
            ActionButton(
                active = isRunning,
                modifier = Modifier
                    .onPointerEvent(PointerEventType.Press){
                        editor.service.run()
                    }
                    .padding(2.dp)
            ) {
                val color = LocalContentColor.current
                Fading(visible = isRunning) {
                    if(finished == 0) {
                        CircularProgressIndicator(
                            color = color,
                            strokeCap = StrokeCap.Round,
                            strokeWidth = 3.dp
                        )
                    }else{
                        CircularProgressIndicator(
                            progress = finished.toFloat() / available,
                            color = color,
                            strokeCap = StrokeCap.Round,
                            strokeWidth = 3.dp
                        )
                    }
                }
                Box(modifier = Modifier.padding(4.dp)) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play",
                        tint = color
                    )
                }
            }
            Fading(visible = isRunning) {
                ActionButton(
                    modifier = Modifier
                        .onPointerEvent(PointerEventType.Press){
                            editor.service.stop()
                        }
                ){
                    val color = LocalContentColor.current
                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .size(12.dp)
                            .background(color)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ActionButton(modifier: Modifier = Modifier, active: Boolean = false, content: @Composable () -> Unit){
    val baseColor = Theme.get("toolbar.button.enabled.field")
    val baseTextColor = Theme.get("toolbar.button.enabled.glyph")

    var hover by remember{ mutableStateOf(false) }
    val hoverColor = Theme.get("toolbar.button.rollover.field")
    val hoverTextColor = Theme.get("toolbar.button.rollover.glyph")

    var pressed by remember{ mutableStateOf(false) }
    val pressedColor = Theme.get("toolbar.button.pressed.field")
    val pressedTextColor = Theme.get("toolbar.button.pressed.glyph")

    val activeColor = Theme.get("toolbar.button.selected.field")
    val activeTextColor = Theme.get("toolbar.button.pressed.glyph")

    val color = when {
        active -> activeColor
        pressed -> pressedColor
        hover -> hoverColor
        else -> baseColor
    }.toColorInt()

    val textColor = when{
        active -> activeTextColor
        pressed -> pressedTextColor
        hover -> hoverTextColor
        else -> baseTextColor
    }

    Box(
        modifier = Modifier
            .onPointerEvent(PointerEventType.Enter) {
                hover = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                hover = false
            }
            .onPointerEvent(PointerEventType.Press) {
                pressed = true
            }
            .onPointerEvent(PointerEventType.Release) {
                pressed = false
            }
            .height(34.dp)
            .clip(CircleShape)
            .aspectRatio(1f)
            .background(color = Color(color))
            .then(modifier)
    ) {
        CompositionLocalProvider(LocalContentColor provides Color(textColor.toColorInt())) {
            content()
        }
    }
}

@Composable
fun Fading(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 250,
                easing = LinearEasing
            )
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = 250,
                easing = LinearEasing
            )
        )
    ) {
        content()
    }
}