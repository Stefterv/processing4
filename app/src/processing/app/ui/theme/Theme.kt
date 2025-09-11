package processing.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import processing.app.LocalPreferences
import processing.app.PreferencesProvider
import java.io.InputStream
import java.util.Properties


// TODO: Other monospace font
// TODO: Look at flatlaf components
// TODO: Look at Jetpack Compose default components
// TODO: Message Boxes
// TODO: Tabs
// TODO: https://web.mit.edu/6.005/www/sp14/psets/ps4/java-6-tutorial/components.html

class Theme(themeFile: String? = "") : Properties() {
    init {
        load(ClassLoader.getSystemResourceAsStream("theme.txt"))
        load(ClassLoader.getSystemResourceAsStream(themeFile ?: "") ?: InputStream.nullInputStream())
    }
    fun getColor(key: String): Color {
        return Color(getProperty(key).toColorInt())
    }
}

val LocalTheme = compositionLocalOf<Theme> { error("No theme provided") }

@Composable
fun ProcessingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    PreferencesProvider {
        val preferences = LocalPreferences.current
        val theme = Theme(preferences.getProperty("theme"))
//        val colors = Colors(
//            primary = theme.getColor("editor.gradient.top"),
//            primaryVariant = theme.getColor("toolbar.button.pressed.field"),
//            secondary = theme.getColor("editor.gradient.bottom"),
//            secondaryVariant = theme.getColor("editor.scrollbar.thumb.pressed.color"),
//            background = theme.getColor("editor.bgcolor"),
//            surface = theme.getColor("editor.bgcolor"),
//            error = theme.getColor("status.error.bgcolor"),
//            onPrimary = theme.getColor("toolbar.button.enabled.field"),
//            onSecondary = theme.getColor("toolbar.button.enabled.field"),
//            onBackground = theme.getColor("editor.fgcolor"),
//            onSurface = theme.getColor("editor.fgcolor"),
//            onError = theme.getColor("status.error.fgcolor"),
//            isLight = theme.getProperty("laf.mode").equals("light")
//        )


        CompositionLocalProvider(LocalTheme provides theme) {
            LocaleProvider {
                MaterialTheme(
                    colors = if(darkTheme) PDELightColors else PDELightColors,
                    typography = Typography,
                    content = content
                )
            }
        }
    }
}