package processing.app.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import processing.app.Messages
import processing.app.ui.WelcomeToBeta.Companion.windowTitle
import processing.utils.download.Downloadable
import javax.swing.JFrame
import javax.swing.SwingUtilities

class DownloadsManager {

    companion object {

        @JvmStatic
        fun startup() {
            Messages.log("START UP :)")
            SwingUtilities.invokeLater {
                JFrame().apply {
                    ComposePanel().apply {
                        setContent {
                            DownloadsManager()
                        }
                        this@apply.add(this)
                    }
                    pack()
                    isVisible = true
                    requestFocus()
                }

            }
        }

        @Composable
        fun DownloadsManager() {
            Window(onCloseRequest = { /*TODO*/ }, title = windowTitle) {
                Text("Hello!")
            }
        }

        fun registerListener() {
            // Register the downloadable listener
            Downloadable.addRegistrationListener { downloadableClass ->
                Messages.log("New downloadable registered: ${downloadableClass.name}")
                downloadables.add(downloadableClass)
            }
        }

        val downloadables = mutableStateListOf<Class<out Downloadable>>()
    }

}