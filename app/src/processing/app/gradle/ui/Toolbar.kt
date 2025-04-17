package processing.app.gradle.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import groovyjarjarantlr4.v4.runtime.misc.Args
import processing.app.gradle.helpers.ActionGradleJob
import processing.app.gradle.GradleJob
import processing.app.gradle.ScreenshotService
import processing.app.ui.Editor
import processing.app.ui.EditorToolbar
import processing.app.ui.Theme
import processing.app.ui.theme.toColorInt
import java.io.File
import javax.swing.JComponent

class Toolbar(val editor: Editor?) {
    companion object {
        @JvmStatic
        fun legacyWrapped(editor: Editor, toolbar: EditorToolbar): JComponent {
            val bar = Toolbar(editor)
            val panel = ComposePanel().apply {
                setContent {
                    val displayNew = editor.service.active.value
                    if (displayNew) {
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

    // TODO: Split into multiple files
    // TODO: Make runnable outside of Processing IDE
    @OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
    @Composable
    fun display() {
        val startColor = Theme.get("toolbar.gradient.top").toColorInt()
        val endColor = Theme.get("toolbar.gradient.bottom").toColorInt()
        val colorStops = arrayOf(
            0.0f to Color(startColor),
            1f to Color(endColor)
        )
        Row(
            modifier = Modifier.background(Brush.verticalGradient(colorStops = colorStops))
                .fillMaxWidth()
                .padding(start = Editor.LEFT_GUTTER.dp, end = Editor.RIGHT_GUTTER.dp)
                .padding(vertical = 11.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            val windowState = rememberWindowState(
                width = Dp.Unspecified,
                height = Dp.Unspecified,
                position = WindowPosition(alignment = Alignment.Center),
            )

            SketchButtons()

            var screenshot by remember { mutableStateOf<File?>(null) }
            screenshot?.apply {
                Window(
                    onCloseRequest = {
                        screenshot = null
                    },
                    resizable = true,
                    title = "Screenshot",
                    state = windowState,
                ) {
                    Column(modifier = Modifier.padding(16.dp).defaultMinSize(400.dp, 400.dp)) {
                        val bitmap = remember { loadImageBitmap(screenshot!!.inputStream()) }
                        screenshot?.let {
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Screenshot",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

            }
            var showSketchSettings by remember { mutableStateOf(false) }
            Row {
                hoverPill(actions = {
                    actionButton(
                        onClick = {

                        }
                    ) {
                        val icon = useResource("toolbar/Settings.svg") { loadSvgPainter(it, Density(1f)) }
                        val color = LocalContentColor.current
                        Icon(
                            painter = icon,
                            contentDescription = "Settings",
                            tint = color
                        )
                    }
                    val vm = editor?.service?.jobs?.lastOrNull()?.vm?.value
                    actionButton(
                        enabled = vm != null,
                        onClick = {
                            vm ?: return@actionButton
                            ScreenshotService.takeScreenshot(vm) { file ->
                                screenshot = file.toFile()
                            }
                        }
                    ) {
                        val icon = useResource("toolbar/Screenshot.svg") { loadSvgPainter(it, Density(1f)) }
                        val color = LocalContentColor.current
                        Icon(
                            painter = icon,
                            contentDescription = "Screenshot",
                            tint = color
                        )
                    }



                    actionButton(
                        active = showSketchSettings,
                        modifier = Modifier
                            .onClick {
                                editor ?: return@onClick
                                val x = editor.location.x + editor.width
                                val y = editor.location.y
                                windowState.position = WindowPosition(
                                    x = x.dp,
                                    y = y.dp,
                                )
                                showSketchSettings = !showSketchSettings
                            }
                    ) {
                        val icon = useResource("toolbar/Sketch Settings.svg") { loadSvgPainter(it, Density(1f)) }
                        val color = LocalContentColor.current
                        Icon(
                            painter = icon,
                            contentDescription = "Sketch Settings",
                            tint = color
                        )
                    }

                }, base = {
                    actionButton {
                        val icon = useResource("toolbar/More.svg") { loadSvgPainter(it, Density(1f)) }
                        val color = LocalContentColor.current
                        Icon(
                            painter = icon,
                            contentDescription = "More",
                            tint = color
                        )
                    }
                })


                Window(
                    visible = showSketchSettings,
                    onCloseRequest = {
                        showSketchSettings = false
                    },
                    resizable = true,
                    title = "Sketch Settings",
                    state = windowState,
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Chip(onClick = {
                            editor?.service?.active?.value = false
                        }) {
                            Text("Switch back to legacy")
                        }
                    }
                }
            }
        }

    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun SketchButtons() {
        val job = editor?.service?.jobs?.filterIsInstance<ActionGradleJob>()?.lastOrNull()
        val state = job?.state?.value ?: GradleJob.State.NONE
        val isActive = state != GradleJob.State.NONE && state != GradleJob.State.DONE
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            actionButton(
                active = isActive,
                modifier = Modifier
                    .onPointerEvent(PointerEventType.Press) {
                        editor?.service?.run()
                    }
                    .padding(2.dp)
            ) {
                val color = LocalContentColor.current
                Fading(visible = state == GradleJob.State.BUILDING, delayMillis = 2_500) {
                    // TODO: Add progress tracking
                    CircularProgressIndicator(
                        color = color,
                        strokeCap = StrokeCap.Round,
                        strokeWidth = 3.dp
                    )
                }
                val icon = useResource("toolbar/Play.svg") { loadSvgPainter(it, Density(1f)) }
                Icon(
                    painter = icon,
                    contentDescription = "Play",
                    tint = color
                )
            }
            Fading(visible = isActive) {
                actionButton(
                    modifier = Modifier
                        .onPointerEvent(PointerEventType.Press) {
                            editor?.service?.stop()
                        }
                ) {
                    val icon = useResource("toolbar/Stop.svg") { loadSvgPainter(it, Density(1f)) }
                    val color = LocalContentColor.current
                    Icon(
                        painter = icon,
                        contentDescription = "Stop",
                        tint = color
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun hoverPill(actions: @Composable () -> Unit, base: @Composable () -> Unit) {
        var hover by remember { mutableStateOf(false) }
        val baseColor = Theme.get("toolbar.button.enabled.field").toColorInt().let { Color(it) }

        Row(
            modifier = Modifier
                .onPointerEvent(PointerEventType.Enter) {
                    hover = true
                }
                .onPointerEvent(PointerEventType.Exit) {
                    hover = false
                }
                .clip(CircleShape)
                .background(baseColor)


        ){
            if(hover) actions()
            base()
        }
    }


    @OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
    @Composable
    fun actionButton(
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        active: Boolean = false,
        onClick: () -> Unit = {},
        content: @Composable () -> Unit
    ) {
        val baseColor = Theme.get("toolbar.button.enabled.field")
        val baseTextColor = Theme.get("toolbar.button.enabled.glyph")

        var hover by remember { mutableStateOf(false) }
        val hoverColor = Theme.get("toolbar.button.rollover.field")
        val hoverTextColor = Theme.get("toolbar.button.rollover.glyph")

        var pressed by remember { mutableStateOf(false) }
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

        val textColor = when {
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
                .onClick{
                    if (enabled) {
                        onClick()
                    }
                }
                .then(modifier)
        ) {
            CompositionLocalProvider(LocalContentColor provides Color(textColor.toColorInt())) {
                content()
            }
        }
    }
    @Composable
    fun Fading(visible: Boolean, delayMillis: Int = 0, content: @Composable () -> Unit) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(
                animationSpec = tween(
                    delayMillis = delayMillis,
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
}