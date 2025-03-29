package processing.app.gradle

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.sun.jdi.Bootstrap
import com.sun.jdi.VirtualMachine
import com.sun.jdi.connect.AttachingConnector
import kotlinx.coroutines.*
import org.gradle.internal.logging.events.OutputEvent
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.events.ProgressListener
import org.gradle.tooling.events.problems.ProblemEvent
import org.gradle.tooling.events.problems.internal.DefaultSingleProblemEvent
import org.gradle.tooling.events.task.TaskFinishEvent
import org.gradle.tooling.events.task.TaskStartEvent
import processing.app.Base
import processing.app.Messages
import processing.app.Platform
import processing.app.ui.Editor
import java.io.*
import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import kotlin.io.path.writeText

// TODO: Remove dependency on editor
class GradleService(val editor: Editor) {
    val availableTasks = mutableStateListOf<String>()
    val finishedTasks = mutableStateListOf<String>()
    val running = mutableStateOf(false)
    var vm: VirtualMachine? = null
    val debugPort = (30000..60000).random()
    val problems = mutableStateListOf<ProblemEvent>()

    private var connection: ProjectConnection? = null
    private var preparation: Job? = null
    private var preparing = false

    private var run: Job? = null
    private var cancel = GradleConnector.newCancellationTokenSource()

    val folder: File get() = editor.sketch.folder

    // TODO: Capture output & enable at start of running

    fun prepare(){
        Messages.log("Preparing sketch")
        if(preparing) return
        preparation?.cancel()
        preparation = CoroutineScope(Dispatchers.IO).launch {
            val connection = connection ?: return@launch
            delay(1000)
            preparing = true

            connection.newSketchBuild()
                .forTasks("jar")
                .run()

            preparing = false
            Messages.log("Preparation finished")
        }
    }

    fun run(){
        val connection = connection ?: return
        Messages.log("Running sketch")
        startRun {
            connection.newSketchBuild()
                .addDebugging()
                .addProgressListener(listOf(":run"))
                .forTasks("run")
                .withCancellationToken(cancel.token())
                .run()
            Messages.log("Running finished")
        }

    }

    fun export(){
        val connection = connection ?: return
        Messages.log("Exporting sketch")
        startRun {
            connection.newSketchBuild()
                .addProgressListener(listOf(":runDistributable"))
                .forTasks("runDistributable")
                .withCancellationToken(cancel.token())
                .run()
            Messages.log("Exporting finished")
        }
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
        // TODO: Stop on dispose
    }

    private fun startRun(action: () -> Unit){
        running.value = true
        run?.cancel()
        run = CoroutineScope(Dispatchers.IO).launch {
            preparation?.join()

            cancel.cancel()
            cancel = GradleConnector.newCancellationTokenSource()

            editor.console.clear()

            try{
                action()
            }catch (e: Exception){
                Messages.log("Error while running sketch: ${e.message}")
                return@launch
            }

        }
        run?.invokeOnCompletion { running.value = run?.isActive ?: false }
    }

    private fun BuildLauncher.addDebugging(): BuildLauncher{
        this.addProgressListener(ProgressListener { event ->
            if(event !is TaskStartEvent) return@ProgressListener
            if(event.descriptor.name != ":run") return@ProgressListener

            Messages.log("Attaching to VM")
            val connector = Bootstrap.virtualMachineManager().allConnectors()
                .firstOrNull { it.name() == "com.sun.jdi.SocketAttach" }
                    as AttachingConnector?
                ?: return@ProgressListener
            val args = connector.defaultArguments()
            args["port"]?.setValue(debugPort.toString())
            val vm = connector.attach(args)
            this@GradleService.vm = vm
            Messages.log("Attached to VM: ${vm.name()}")
        })
        return this
    }

    private fun BuildLauncher.addProgressListener(skipping: List<String> ): BuildLauncher{
        this.addProgressListener(ProgressListener { event ->
            val name = event.descriptor.name
            if(skipping.contains(name)) return@ProgressListener
            if(event is TaskStartEvent) {
                if(!availableTasks.contains(name)) availableTasks.add(name)
            }
            if(event is TaskFinishEvent){
                finishedTasks.add(name)
            }
            if(event is DefaultSingleProblemEvent){
                Messages.log("")
            }
        })
        return this
    }

    private fun ProjectConnection.newSketchBuild(): BuildLauncher{
        finishedTasks.clear()

        val workingDir = kotlin.io.path.createTempDirectory()
        val group = System.getProperty("processing.group", "org.processing")


        // TODO: is this the best way to handle unsaved data?
        val unsaved = mutableListOf<String>()
        editor.sketch.code.forEach { code ->
            if(!code.isModified) return@forEach

            val file = workingDir.resolve("unsaved/${code.fileName}")
            file.parent.toFile().mkdirs()
            file.writeText(code.documentText)
            unsaved.add(code.fileName)
        }

        val variables = mapOf(
            "group" to group,
            "version" to Base.getVersionName(),
            "sketchFolder" to folder.absolutePath,
            "workingDir" to workingDir.toAbsolutePath().toString(),
            "settings" to Platform.getSettingsFolder().absolutePath.toString(),
            "unsaved" to unsaved.joinToString(","),
            "debugPort" to debugPort.toString()
        )
        val repository = Platform.getContentFile("repository").absolutePath

        val initGradle = workingDir.resolve("init.gradle.kts").apply {
            val content = """
                beforeSettings{
                    pluginManagement {
                        repositories {
                            maven { url = uri("$repository") }
                            gradlePluginPortal()
                        }
                    }
                }
                allprojects{
                    repositories {
                        maven { url = uri("$repository") }
                        mavenCentral()
                    }
                }
            """.trimIndent()

            writeText(content)
        }



        val buildGradle = folder.resolve("build.gradle.kts")
        // TODO: Manage script if the comment exists
        if(!buildGradle.exists()){
            Messages.log("build.gradle.kts not found in ${folder}, creating one")
            // TODO: Allow for other plugins to be registered
            // TODO: Allow for the whole configuration to be overridden
            // TODO: Move this to java mode
            val content = """
                    // Managed by: Processing ${Base.getVersionName()} ${editor.mode.title}
                    // If you delete this comment Processing will no longer update the build scripts

                    plugins{
                        id("org.processing.gradle") version "${Base.getVersionName()}"
                    }
                """.trimIndent()
            buildGradle.writeText(content)
        }
        val settingsGradle = folder.resolve("settings.gradle.kts")
        if(!settingsGradle.exists()) {
            settingsGradle.createNewFile()
        }

        return this.newBuild()
//            .addJvmArguments("-Xmx2g")
            .setJavaHome(Platform.getJavaHome())
            .withArguments(
                "--init-script", initGradle.toAbsolutePath().toString(),
                *variables.entries.map { "-Pprocessing.${it.key}=${it.value}" }.toTypedArray()
            )
            .setStandardError(System.err)
            .setStandardOutput(System.out)
    }
}