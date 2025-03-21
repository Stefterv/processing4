package processing.app.tools

import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import processing.app.Mode
import processing.app.Sketch
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText
import kotlin.test.Test

class ShareTest{

    // Test how the sketch to url class deals with plain processing sketches with only test files
    @Test
    fun testPlainSketchToURL(){
        val mode = mock<Mode>(){
            on { extensions } doReturn arrayOf("pde")
        }

        val tempDir = createTempDirectory().resolve("main")
        tempDir.toFile().mkdirs()
        val mainFile = tempDir.resolve("main.pde")
        mainFile.writeText("""
            void setup(){
                size(400, 400);
            }
            void draw(){
                background(255);
                callme();
            }
        """.trimIndent())

        val secondaryFile = tempDir.resolve("secondary.pde")
        secondaryFile.writeText("""
            void callme(){
                println("Hello World");
            }
        """.trimIndent())

        val sketch = Sketch(mainFile.toString(), mode)
        val url = Share.convertSketchToBase64URL(sketch)
        assertEquals("pde://sketch/base64/dm9pZCBzZXR1cCgpewogICAgc2l6ZSg0MDAsIDQwMCk7Cn0Kdm9pZCBkcmF3KCl7CiAgICBiYWNrZ3JvdW5kKDI1NSk7CiAgICBjYWxsbWUoKTsKfQ==?pde=secondary.pde:dm9pZCBjYWxsbWUoKXsKICAgIHByaW50bG4oIkhlbGxvIFdvcmxkIik7Cn0=", url)
    }
}