package processing.app.gradle

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.*
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.events.ProgressListener
import org.gradle.tooling.events.task.TaskFinishEvent
import org.gradle.tooling.events.task.TaskStartEvent
import processing.app.Base
import processing.app.Messages
import processing.app.ui.Editor
import java.io.File
import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

// TODO: Embed the core, gradle plugin, and preprocessor in a custom .m2 repository
// Right now the gradle service only works if you publish those to the local maven repository
class GradleService(val editor: Editor) {
    val availableTasks = mutableStateListOf<String>()
    val finishedTasks = mutableStateListOf<String>()
    val running = mutableStateOf(false)

    private var connection: ProjectConnection? = null
    private var preparation: Job? = null
    private var preparing = false

    private var run: Job? = null
    private var cancel = GradleConnector.newCancellationTokenSource()

    val folder: File get() = editor.sketch.folder

    fun prepare(){
        if(preparing) return
        preparation?.cancel()
        preparation = CoroutineScope(Dispatchers.IO).launch {
            val connection = connection ?: return@launch
            delay(1000)
            preparing = true

            connection.newSketchBuild()
                .forTasks("build")
                .run()

            preparing = false
        }
    }

    fun run(){
        val connection = connection ?: return
        if(!preparing) preparation?.cancel()

        run?.cancel()
        run = CoroutineScope(Dispatchers.IO).launch {
            running.value = true
            preparation?.join()
            cancel.cancel()
            cancel = GradleConnector.newCancellationTokenSource()
            try {
                connection.newSketchBuild()
                    .forTasks("run")
                    .withCancellationToken(cancel.token())
                    .run()
            }catch (e: Exception){
                Messages.log(e.toString())
            }
        }
        run?.invokeOnCompletion { running.value = run?.isActive ?: false }
    }

    fun stop(){
        cancel.cancel()
    }

    fun startService(){
        Messages.log("Starting Gradle service at ${folder}")

        connection = GradleConnector.newConnector()
            .forProjectDirectory(folder)
            .connect()

        // TODO: recreate connection if sketch folder changes

        // TODO: Run the sketch with the latest changes

        SwingUtilities.invokeLater {
            editor.sketch.code.forEach {
                it.document.addDocumentListener(object : DocumentListener {
                    override fun insertUpdate(e: DocumentEvent) {
                        prepare()
                    }

                    override fun removeUpdate(e: DocumentEvent) {
                        prepare()
                    }

                    override fun changedUpdate(e: DocumentEvent) {
                        prepare()
                    }
                })
            }

            // TODO: Attach listener if new tab is created
        }
    }




    fun ProjectConnection.newSketchBuild(): BuildLauncher{
        finishedTasks.clear()
        // TODO: Research if we can generate these programmatically
        // Ideally they would not be placed into the sketch folder

        val buildGradle = folder.resolve("build.gradle.kts")
        if(!buildGradle.exists()){
            Messages.log("build.gradle.kts not found in ${folder}, creating one")
            // TODO: Allow for other plugins to be registered
            val content = """
                    plugins{
                        id("processing.java.gradle") version "${Base.getVersionName()}"
                    }
                """.trimIndent()
            buildGradle.writeText(content)
        }

        val settingsGradle = folder.resolve("settings.gradle.kts")
        if(!settingsGradle.exists()){
            Messages.log("settings.gradle.kts not found in ${folder}, creating one")
            val content = """
                    pluginManagement {
                        repositories {
                            mavenLocal()
                            mavenCentral()
                        }
                    }
                """.trimIndent()
            settingsGradle.writeText(content)
        }

        return this.newBuild()
            .addProgressListener(ProgressListener { event ->
                val name = event.descriptor.name
                if(event is TaskStartEvent) {
                    if(!availableTasks.contains(name)) availableTasks.add(name)
                }
                if(event is TaskFinishEvent){
                    finishedTasks.add(name)
                }
            })
//            .setStandardOutput(System.out)
//            .setJavaHome(Platform.getJavaHome())
    }
}