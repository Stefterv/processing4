package processing.app.tools

import androidx.compose.material.Text
import processing.app.Sketch
import processing.app.SketchCode
import processing.app.ui.theme.LocalLocale
import processing.app.ui.theme.PDEButton
import processing.app.ui.theme.PDEWindow
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import javax.swing.SwingUtilities
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


class Share{
    companion object {
        @JvmStatic
        fun sketch(sketch: Sketch) {
            SwingUtilities.invokeLater {
                PDEWindow("share.window.title"){
                    PDEButton(onClick = {
                        val clipboard: Clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
                        clipboard.setContents(StringSelection(sketch.toBase64URL()), null)
                    }){
                        val locale = LocalLocale.current
                        Text(locale["share.window.text"])
                    }
                }
            }
        }
        fun Sketch.toBase64URL(): String{
            return convertSketchToBase64URL(this)
        }
        fun convertSketchToBase64URL(sketch: Sketch): String{
            val main = sketch.code.first()

            var url = "pde://sketch/base64/${main.toBase64()}"

            val pdes = sketch.code
                .drop(1)
                .map{
                    "${it.fileName}:${it.toBase64()}"
                }
                .joinToString(",")
            if(pdes.isNotEmpty()){
                url += "?pde=$pdes"
            }
            return url
        }

        @OptIn(ExperimentalEncodingApi::class)
        fun SketchCode.toBase64(): String{
            return Base64.encode(program.toByteArray())

        }
    }
}
