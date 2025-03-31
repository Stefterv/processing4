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
}
