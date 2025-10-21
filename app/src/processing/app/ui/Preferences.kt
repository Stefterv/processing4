package processing.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import processing.app.ui.theme.PDETheme

class Preferences {
    companion object{

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun preferences(){
            Row {
                NavigationRail(
                    header = {
                        Text("Settings", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 42.dp))

                    },
                    modifier = Modifier.defaultMinSize(minWidth = 200.dp)
                ) {
                    NavigationRailItem(
                        selected = true,
                        onClick = { /*TODO*/ },
                        icon = {
                            Icon(Icons.Default.Settings, contentDescription = null)
                        },
                        label = {
                            Text("General")
                        }
                    )
                    NavigationRailItem(
                        selected = false,
                        onClick = { /*TODO*/ },
                        icon = {
                            Icon(Icons.Default.Settings, contentDescription = null)
                        },
                        label = {
                            Text("Interface & fonts")
                        }
                    )
                }
                Box(modifier = Modifier.padding(top = 42.dp)){
                    Column(modifier = Modifier.fillMaxSize()) {
                        SearchBar(inputField = {
                            SearchBarDefaults.InputField(
                                query = "",
                                onQueryChange = { },
                                onSearch = {

                                },
                                expanded = false,
                                onExpandedChange = { },
                                placeholder = { Text("Search") }
                            )
                        }, expanded = false, onExpandedChange = {}, modifier = Modifier.align(Alignment.End).padding(16.dp)) {

                        }
                        Text("Preferences go here")
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