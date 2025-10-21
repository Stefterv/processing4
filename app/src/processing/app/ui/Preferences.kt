package processing.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import processing.app.LocalPreferences
import processing.app.ui.preferences.General
import processing.app.ui.theme.LocalLocale
import processing.app.ui.theme.PDETheme

val LocalPreferenceGroups = compositionLocalOf<MutableMap<PDEPreferenceGroup, List<PDEPreference>>> {
    error("No Preference Groups Set")
}

class Preferences {
    companion object{
        val groups = mutableStateMapOf<PDEPreferenceGroup, List<PDEPreference>>()
        fun register(preference: PDEPreference) {
            val list = groups[preference.group]?.toMutableList() ?: mutableListOf()
            list.add(preference)
            groups[preference.group] = list
        }
        init{
            General.register()
        }


        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun preferences(){
            var visible by remember { mutableStateOf(groups) }
            val sortedGroups = remember {
                val keys = visible.keys
                keys.toSortedSet {
                        a, b ->
                    when {
                        a.after == b -> 1
                        b.after == a -> -1
                        else -> a.name.compareTo(b.name)
                    }
                }
            }
            var selected by remember { mutableStateOf(sortedGroups.first()) }
            CompositionLocalProvider(
                LocalPreferenceGroups provides visible
            ) {
                Row {
                    NavigationRail(
                        header = {
                            Text(
                                "Settings",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(top = 42.dp)
                            )

                        },
                        modifier = Modifier.defaultMinSize(minWidth = 200.dp)
                    ) {

                        for (group in sortedGroups) {
                            NavigationRailItem(
                                selected = selected == group,
                                enabled = visible.keys.contains(group),
                                onClick = {
                                    selected = group
                                },
                                icon = {
                                    group.icon()
                                },
                                label = {
                                    Text(group.name)
                                }
                            )
                        }
                    }
                    Box(modifier = Modifier.padding(top = 42.dp)) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            var query by remember { mutableStateOf("") }
                            val locale = LocalLocale.current
                            LaunchedEffect(query){

                                snapshotFlow { query }
                                    .debounce(100)
                                    .collect{
                                        if(it.isBlank()){
                                            visible = groups
                                            return@collect
                                        }
                                        val filtered = mutableStateMapOf<PDEPreferenceGroup, List<PDEPreference>>()
                                        for((group, preferences) in groups){
                                            val matching = preferences.filter { preference ->
                                                if(preference.key == "other"){
                                                    return@filter true
                                                }
                                                if(preference.key.contains(it, ignoreCase = true)){
                                                    return@filter true
                                                }
                                                val description = locale[preference.descriptionKey]
                                                description.contains(it, ignoreCase = true)
                                            }
                                            if(matching.isNotEmpty()){
                                                filtered[group] = matching
                                            }
                                        }
                                        visible = filtered
                                    }

                            }
                            SearchBar(
                                inputField = {
                                    SearchBarDefaults.InputField(
                                        query = query,
                                        onQueryChange = {
                                            query = it
                                        },
                                        onSearch = {

                                        },
                                        expanded = false,
                                        onExpandedChange = {  },
                                        placeholder = { Text("Search") }
                                    )
                                },
                                expanded = false,
                                onExpandedChange = {},
                                modifier = Modifier.align(Alignment.End).padding(16.dp)
                            ) {

                            }
                            val prefs = LocalPreferences.current
                            val preferences = visible[selected] ?: emptyList()
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                                items(preferences){ preference ->
                                    Text(
                                        text = locale[preference.descriptionKey],
                                        modifier = Modifier.padding(horizontal = 20.dp),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    preference.control(prefs[preference.key]) { newValue ->
                                        prefs[preference.key] = newValue
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        @JvmStatic
        fun main(args: Array<String>) {
            application {
                Window(onCloseRequest = ::exitApplication){
                    remember{
                        window.rootPane.putClientProperty("apple.awt.fullWindowContent", true)
                        window.rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
                    }
                    PDETheme(darkTheme = true) {
                        preferences()
                    }
                }
                Window(onCloseRequest = ::exitApplication){
                    remember{
                        window.rootPane.putClientProperty("apple.awt.fullWindowContent", true)
                        window.rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
                    }
                    PDETheme(darkTheme = false) {
                        preferences()
                    }
                }
            }
        }
    }
}

data class PDEPreference(
    /**
     * The key in the preferences file used to store this preference.
     */
    val key: String,
    val descriptionKey: String,
    /**
     * The group this preference belongs to.
     */
    val group: PDEPreferenceGroup,
    val control: @Composable (preference: String?, updatePreference: (newValue: String) -> Unit) -> Unit = { preference, updatePreference ->  },
)

data class PDEPreferenceGroup(
    /**
     * The name of this group.
     */
    val name: String,
    /**
     * The icon representing this group.
     */
    val icon: @Composable () -> Unit,
    /**
     * The group that comes before this one in the list.
     */
    val after: PDEPreferenceGroup? = null,
)