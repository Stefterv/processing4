import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ProcessingPluginTest{
    @Test
    fun testPluginAddsSketchTask(){
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.processing.gradle")

        assert(project.tasks.getByName("sketch") is Task)
    }
    @JvmField
    @Rule
    val folder: TemporaryFolder = TemporaryFolder()

    @Test
    fun testPluginOutcome() {

        val buildFile = folder.newFile("build.gradle.kts")
        buildFile.writeText("""
            plugins{
                id("org.processing.gradle")
            }
        """.trimIndent())

        val sketchFile = folder.newFile("sketch.pde")
        sketchFile.writeText("""
            void setup(){
                size(100, 100);
            }
            void draw(){
                background(0);
            }
        """.trimIndent())

        GradleRunner.create()
            .withProjectDir(folder.root)
            .withArguments("build")
            .withPluginClasspath()
            .build()

        assert(folder.root.resolve("build/generated/pde/main").exists())
    }
}
