package processing.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.LocalRippleConfiguration
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.RippleConfiguration
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TriStateCheckbox
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import processing.app.LocalPreferences
import processing.app.PreferencesProvider
import java.io.InputStream
import java.util.Properties

class ProcessingTheme(themeFile: String? = "") : Properties() {
    init {
        load(ClassLoader.getSystemResourceAsStream("theme.txt"))
        load(ClassLoader.getSystemResourceAsStream(themeFile ?: "") ?: InputStream.nullInputStream())
    }
    fun getColor(key: String): Color {
        return Color(getProperty(key).toColorInt())
    }

    fun String.toColorInt(): Int {
        if (this[0] == '#') {
            var color = substring(1).toLong(16)
            if (length == 7) {
                color = color or 0x00000000ff000000L
            } else if (length != 9) {
                throw IllegalArgumentException("Unknown color")
            }
            return color.toInt()
        }
        throw IllegalArgumentException("Unknown color")
    }
}

val LocalProcessingTheme = compositionLocalOf<ProcessingTheme> { error("No theme provided") }

@Deprecated("Use PDETheme instead", ReplaceWith("PDETheme"))
@Composable
fun ProcessingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    PreferencesProvider {
        val preferences = LocalPreferences.current
        val theme = ProcessingTheme(preferences.getProperty("theme"))
        CompositionLocalProvider(LocalProcessingTheme provides theme,LocalDensity provides Density(1.25f, 1.25f),) {
            LocaleProvider {
                MaterialTheme(
                    colors = if (darkTheme) PDE2DarkColors else PDE2LightColors,
                    typography = PDE2Typography,
                    shapes = PDE2Shapes
                ) {
                    CompositionLocalProvider(
                        LocalRippleConfiguration provides RippleConfiguration(
                            color = MaterialTheme.colors.primary,
                        )
                    ) {
                        Box(modifier = Modifier.background(MaterialTheme.colors.background).fillMaxSize()) {
                            Surface(
                                color = MaterialTheme.colors.background,
                                contentColor = MaterialTheme.colors.onBackground
                            ) {
                                content()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
fun main(){
    application {
        val windowState = rememberWindowState(
            size = DpSize(800.dp, 600.dp),
            position = WindowPosition(Alignment.Center)
        )
        var darkTheme by remember { mutableStateOf(false) }
        Window(onCloseRequest = ::exitApplication, state = windowState, title = "Processing Theme") {
            ProcessingTheme(darkTheme) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Processing Theme Components", style = MaterialTheme.typography.h1)
                    Card {
                        Row {
                            Checkbox(darkTheme, onCheckedChange = { darkTheme = !darkTheme })
                            Text(
                                "Dark Theme",
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                    val scrollable = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollable),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        ComponentPreview("Colors") {
                            Column {
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Button(
                                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                                        onClick = {}) {
                                        Text("Primary", color = MaterialTheme.colors.onPrimary)
                                    }
                                    Button(
                                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primaryVariant),
                                        onClick = {}) {
                                        Text("Primary Variant", color = MaterialTheme.colors.onPrimary)
                                    }
                                    Button(
                                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
                                        onClick = {}) {
                                        Text("Secondary", color = MaterialTheme.colors.onSecondary)
                                    }
                                    Button(
                                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondaryVariant),
                                        onClick = {}) {
                                        Text("Secondary Variant", color = MaterialTheme.colors.onSecondary)
                                    }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Button(
                                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background),
                                        onClick = {}) {
                                        Text("Background", color = MaterialTheme.colors.onBackground)
                                    }
                                    Button(
                                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface),
                                        onClick = {}) {
                                        Text("Surface", color = MaterialTheme.colors.onSurface)
                                    }
                                    Button(
                                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
                                        onClick = {}) {
                                        Text("Error", color = MaterialTheme.colors.onError)
                                    }
                                }
                            }
                        }
                        ComponentPreview("Text & Fonts") {
                            Column {
                                Text("Heading 1", style = MaterialTheme.typography.h1)
                                Text("Heading 2", style = MaterialTheme.typography.h2)
                                Text("Heading 3", style = MaterialTheme.typography.h3)
                                Text("Heading 4", style = MaterialTheme.typography.h4)
                                Text("Heading 5", style = MaterialTheme.typography.h5)
                                Text("Heading 6", style = MaterialTheme.typography.h6)

                                Text("Subtitle 1", style = MaterialTheme.typography.subtitle1)
                                Text("Subtitle 2", style = MaterialTheme.typography.subtitle2)

                                Text("Body 1", style = MaterialTheme.typography.body1)
                                Text("Body 2", style = MaterialTheme.typography.body2)

                                Text("Caption", style = MaterialTheme.typography.caption)
                                Text("Overline", style = MaterialTheme.typography.overline)
                            }
                        }
                        ComponentPreview("Buttons") {
                            Button(onClick = {}) {
                                Text("Filled")
                            }
                            Button(onClick = {}, enabled = false) {
                                Text("Disabled")
                            }
                            OutlinedButton(onClick = {}) {
                                Text("Outlined")
                            }
                            TextButton(onClick = {}) {
                                Text("Text")
                            }
                        }
                        ComponentPreview("Icon Buttons") {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Map, contentDescription = "Icon Button")
                            }
                        }
                        ComponentPreview("Chip") {
                            Chip(onClick = {}){
                                Text("Chip")
                            }
                            Chip(onClick = {}, colors = ChipDefaults.outlinedChipColors(), border = ChipDefaults.outlinedBorder){
                                Text("Outlined")
                            }
                            FilterChip(selected = false, onClick = {}){
                                Text("Filter not Selected")
                            }
                            FilterChip(selected = true, onClick = {}){
                                Text("Filter Selected")
                            }
                        }
                        ComponentPreview("Progress Indicator") {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)){
                                CircularProgressIndicator()
                                LinearProgressIndicator()
                            }
                        }
                        ComponentPreview("Radio Button") {
                            var state by remember { mutableStateOf(true) }
                            RadioButton(!state, onClick = { state = false })
                            RadioButton(state, onClick = { state = true })

                        }
                        ComponentPreview("Checkbox") {
                            var state by remember { mutableStateOf(true) }
                            Checkbox(state, onCheckedChange = { state = it })
                            Checkbox(!state, onCheckedChange = { state = !it })
                            Checkbox(state, onCheckedChange = {}, enabled = false)
                            TriStateCheckbox(ToggleableState.Indeterminate, onClick = {})
                        }
                        ComponentPreview("Switch") {
                            var state by remember { mutableStateOf(true) }
                            Switch(state, onCheckedChange = { state = it })
                        }
                        ComponentPreview("Slider") {
                            var state by remember { mutableStateOf(0f) }
                            Slider(state, onValueChange = { state = it })

                        }
                        ComponentPreview("Badge") {
                            IconButton(onClick = {}) {
                                BadgedBox(badge = { Badge() }) {
                                    Icon(Icons.Default.Map, contentDescription = "Icon with Badge")
                                }
                            }
                        }
                        ComponentPreview("Number Field") {
                            var number by remember { mutableStateOf("123") }
                            TextField(number, onValueChange = {
                                if(it.all { char -> char.isDigit() }) {
                                    number = it
                                }
                            }, label = { Text("Number Field") })

                        }
                        ComponentPreview("Text Field") {
                            Row {
                                var text by remember { mutableStateOf("Text Field") }
                                TextField(text, onValueChange = { text = it })
                            }
                            var text by remember { mutableStateOf("Outlined Text Field") }
                            OutlinedTextField(text, onValueChange = { text = it})
                        }
                        ComponentPreview("Dropdown Menu") {
                            var show by remember { mutableStateOf(false) }
                            TextField("Dropdown", onValueChange = {}, readOnly = true, modifier = Modifier
                                .padding(8.dp)
                                .background(Color.Transparent)
                                .clickable { show = true }
                            )
                            DropdownMenu(
                                expanded = show,
                                onDismissRequest = {
                                    show = false
                                },
                            ) {
                                DropdownMenuItem(onClick = { show = false }) {
                                    Text("Menu Item 1", modifier = Modifier.padding(8.dp))
                                }
                                DropdownMenuItem(onClick = { show = false }) {
                                    Text("Menu Item 2", modifier = Modifier.padding(8.dp))
                                }
                                DropdownMenuItem(onClick = { show = false }) {
                                    Text("Menu Item 3", modifier = Modifier.padding(8.dp))
                                }
                            }


                        }

                        ComponentPreview("Scrollable View") {

                        }

                        ComponentPreview("Tabs") {

                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComponentPreview(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.h4)
        Divider()
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            content()
        }
        Divider()
    }
}