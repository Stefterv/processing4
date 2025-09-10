package processing.mode.java.lsp.proxy

import processing.app.Base
import processing.app.Platform
import processing.app.Preferences
import processing.app.contrib.ModeContribution
import processing.mode.java.JavaMode
import java.io.File

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

}