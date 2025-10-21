package processing.app.ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import processing.app.LocalPreferences
import processing.app.SketchName
import processing.app.ui.LocalPreferenceGroups
import processing.app.ui.PDEPreference
import processing.app.ui.PDEPreferenceGroup
import processing.app.ui.Preferences
import processing.app.ui.theme.LocalLocale


class General {
    companion object{
        val general = PDEPreferenceGroup(
            name = "General",
            icon = {
                Icon(Icons.Default.Settings, contentDescription = "A settings icon")
            }
        )
        val other = PDEPreferenceGroup(
            name = "Other",
            icon = {
                Icon(Icons.Default.Map, contentDescription = "A map icon")
            },
            after = general
        )
        fun register() {
            Preferences.register(
                PDEPreference(
                    key = "sketchbook.path.four",
                    descriptionKey = "preferences.sketchbook_location",
                    group = general,
                    control = { preference, updatePreference ->
                        Row (
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            TextField(
                                value = preference ?: "",
                                onValueChange = {
                                    updatePreference(it)
                                }
                            )
                            Button(
                                onClick = {

                                }
                            ) {
                                Text("Browse")
                            }
                        }
                    }
                )
            )
            Preferences.register(
                PDEPreference(
                    key = "sketch.name.approach",
                    descriptionKey = "preferences.sketch_naming",
                    group = general,
                    control = { preference, updatePreference ->
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp)
                        ) {
                            for (option in if (false) SketchName.getOptions() else arrayOf(
                                "timestamp",
                                "untitled",
                                "custom"
                            )) {
                                FilterChip(
                                    selected = preference == option,
                                    onClick = {
                                        updatePreference(option)
                                    },
                                    label = {
                                        Text(option)
                                    },
                                    modifier = Modifier.padding(4.dp),
                                )
                            }
                        }
                    }
                )
            )
            Preferences.register(
                PDEPreference(
                    key = "update.check",
                    descriptionKey = "preferences.check_for_updates_on_startup",
                    group = general,
                    control = { preference, updatePreference ->
                        Switch(
                            checked = preference.toBoolean(),
                            modifier = Modifier.padding(horizontal = 20.dp),
                            onCheckedChange = {
                                updatePreference(it.toString())
                            }
                        )
                    }
                )
            )
            // show welcome screen
            Preferences.register(
                PDEPreference(
                    key = "welcome.show",
                    descriptionKey = "preferences.show_welcome_screen_on_startup",
                    group = general,
                    control = { preference, updatePreference ->
                        Switch(
                            checked = preference.toBoolean(),
                            modifier = Modifier.padding(horizontal = 20.dp),
                            onCheckedChange = {
                                updatePreference(it.toString())
                            }
                        )
                    }
                )
            )

            Preferences.register(
                PDEPreference(
                    key = "other",
                    descriptionKey = "preferences.other",
                    group = other,
                    control = { _, _ ->
                        val prefs = LocalPreferences.current
                        val groups = LocalPreferenceGroups.current
                        val restPrefs = remember {
                            val keys = prefs.keys.mapNotNull { it as? String }
                            val existing = groups.values.flatten().map { it.key }
                            keys.filter { it !in existing }.sorted()
                        }
                        val locale = LocalLocale.current

                        for(prefKey in restPrefs){
                            val value = prefs[prefKey]
                            Row (
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ){
                                Text(
                                    text = locale[prefKey],
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                                TextField(value ?: "", onValueChange = {
                                    prefs[prefKey] = it
                                })
                            }
                        }

                    }
                )
            )
        }
    }
}