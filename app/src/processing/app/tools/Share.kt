package processing.app.tools

import androidx.compose.material.Text
import processing.app.Sketch
import processing.app.ui.theme.LocalLocale
import processing.app.ui.theme.PDEButton
import processing.app.ui.theme.PDEWindow
import java.net.URL
import javax.swing.SwingUtilities

class Share{
    companion object {
        @JvmStatic
        fun sketch(sketch: Sketch) {
            SwingUtilities.invokeLater {
                PDEWindow("share.window.title"){
                    PDEButton(onClick = {

                    }){
                        val locale = LocalLocale.current
                        Text(locale["share.window.text"])
                    }
                }
            }
        }
        fun convertSketchToBase64URL(sketch: Sketch): URL{


            return URL("https://processing.org");
        }

    }
}
