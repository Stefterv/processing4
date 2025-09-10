package processing.mode.java.lsp.proxy

import processing.app.Base
import processing.app.Platform
import processing.app.Preferences
import processing.app.Sketch
import processing.app.contrib.ModeContribution
import processing.mode.java.JavaMode
import processing.mode.java.PreprocService
import processing.mode.java.PreprocSketch
import java.io.File
import java.net.URI

class PdeService {

    var mode: JavaMode? = null
    companion object{
        var instance: PdeService? = null
            get() {
                if (field == null) {
                    field = PdeService()
                }
                return field
            }
            private set
    }
    fun init(){
        Base.setCommandLine()
        Platform.init()
        Preferences.init()

        val location: File? = Platform.getContentFile("modes/java")
        mode = ModeContribution.load(null, location, JavaMode::class.java.getName()).mode as? JavaMode
    }

    val preprocServices = mutableMapOf<String, PreprocService>()

    fun addSketchFile(uri: String): PreprocSketch?{
        val uri = URI.create(uri)

        val sketch = File(uri)
        if(sketch.extension != "pde"){
            println("Not a PDE file: $uri")
            return null
        }
        val sketchFolder = sketch.absolutePath

        // TODO: Switch to the main file or same as the parent folder name
        if (!preprocServices.containsKey(sketchFolder)){
            val sketch = Sketch(sketchFolder, mode!!)
            val preprocService = PreprocService(mode!!, sketch)
            preprocServices[sketchFolder] = preprocService
        }
        val preprocService = preprocServices[sketchFolder]!!

        var result: PreprocSketch? = null

        preprocService.notifySketchChanged()
        preprocService.whenDoneBlocking{
            result = it
            println("PreprocService initialized for sketch at $sketchFolder")
            // After pre-processing is done, let the LSP server know about the files
        }
        return result
    }

}