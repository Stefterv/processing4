package processing.app.gradle

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import processing.app.Base
import processing.app.Messages
import processing.app.Platform
import processing.app.gradle.helpers.ActionGradleJob
import processing.app.gradle.helpers.BackgroundGradleJob
import processing.app.ui.Editor
import java.io.*
import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import kotlin.io.path.writeText

// TODO: Remove dependency on editor (editor is not mockable, or move editor away from JFrame)
// TODO: Improve progress tracking
// TODO: PoC new debugger/tweak mode
// TODO: Allow for plugins to skip gradle entirely
// TODO: Improve background building
// The gradle service runs the gradle tasks and manages the gradle connection
// It will create the necessary build files for gradle to run
class GradleService(val editor: Editor) {
    val folder: File get() = editor.sketch.folder
    val active = mutableStateOf(true)

    val jobs = mutableStateListOf<GradleJob>()
    val workingDir = kotlin.io.path.createTempDirectory()
    val debugPort = (30_000..60_000).random()

    var connection: ProjectConnection? = null

    private val scope = CoroutineScope(Dispatchers.IO)

    // Hooks for java to check if the Gradle service is running
    fun getEnabled(): Boolean {
        return active.value
    }
    fun setEnabled(active: Boolean) {
        this.active.value = active
    }

    fun startService(){
        Messages.log("Starting Gradle service at $folder")
        // TODO: recreate connection if sketch folder changes
        connection = GradleConnector.newConnector()
            .forProjectDirectory(folder)
            .connect()

        startListening()
        startBuilding()
    }


    fun startBuilding(){
        scope.launch {
            // TODO: Improve the experience with unsaved
            val job = BackgroundGradleJob()
            job.service = this@GradleService
            job.configure = {
                setup()
                forTasks("jar")
                addArguments("--continuous")
            }
            job.start()
        }
    }

    private fun startListening(){
        SwingUtilities.invokeLater {
            editor.sketch.code.forEach {
                it.document.addDocumentListener(object : DocumentListener {
                    override fun insertUpdate(e: DocumentEvent) {
                        setupGradle()
                    }

                    override fun removeUpdate(e: DocumentEvent) {
                        setupGradle()
                    }

                    override fun changedUpdate(e: DocumentEvent) {
                        setupGradle()
                    }
                })
            }

            // TODO: Attach listener to new tab created
        }
        // TODO: Stop all jobs on dispose
    }
    fun run(){
        stopActions()
        editor.console.clear()

        val job = ActionGradleJob()
        job.service = this
        job.configure = {
            setup()
            forTasks("run")
        }
        job.start()
    }

    fun export(){
        stopActions()
        editor.console.clear()

        val job = ActionGradleJob()
        job.service = this
        job.configure = {
            setup()
            forTasks("runDistributable")
        }
        job.start()
    }

    fun stop(){
        stopActions()
        startBuilding()
    }

    fun stopActions(){
        jobs
            .filterIsInstance<ActionGradleJob>()
            .forEach(GradleJob::cancel)
    }

    private fun setupGradle(): MutableList<String> {
        // TODO: is this the best way to handle unsaved data?
        // Certainly not...
        // Gradle is not recognizing the unsaved files as changed
        // Tricky as when we save the file the actual one will be the latest
        val unsaved = editor.sketch.code
            .filter { it.isModified }
            .map { code ->
                val file = workingDir.resolve("unsaved/${code.fileName}")
                file.parent.toFile().mkdirs()
                file.writeText(code.documentText)
                code.fileName
            }

        val group = System.getProperty("processing.group", "org.processing")

        val variables = mapOf(
            "group" to group,
            "version" to Base.getVersionName(),
            "sketchFolder" to folder.absolutePath,
            "workingDir" to workingDir.toAbsolutePath().toString(),
            "settings" to Platform.getSettingsFolder().absolutePath.toString(),
            "unsaved" to unsaved.joinToString(","),
            "debugPort" to debugPort.toString(),
            "present" to false, // TODO: Implement
            "fullscreen" to false, // TODO: Implement
            "display" to 1, // TODO: Implement
            "external" to true,
            "editor.location" to editor.location.let { "${it.x},${it.y}" },
            //"awt.disable" to false,
            //"window.color" to "0xFF000000", // TODO: Implement
            //"stop.color" to "0xFF000000", // TODO: Implement
            "stop.hide" to false, // TODO: Implement
            "sketch.folder" to folder.absolutePath,
            //"location" to "0,0",
            //"ui.scale" to "1.0",
        )
        val repository = Platform.getContentFile("repository").absolutePath.replace("""\""", """\\""")

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
        if (!buildGradle.exists()) {
            Messages.log("build.gradle.kts not found in ${folder}, creating one")
            // TODO: Allow for other plugins to be registered
            // TODO: Allow for the whole configuration to be overridden
            // TODO: Move this to java mode
            // TODO: Define new plugin / mode schema
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
        if (!settingsGradle.exists()) {
            settingsGradle.createNewFile()
        }

        val arguments = mutableListOf("--init-script", initGradle.toAbsolutePath().toString())
        if (!Base.DEBUG) arguments.add("--quiet")
        arguments.addAll(variables.entries.map { "-Pprocessing.${it.key}=${it.value}" })

        return arguments
    }


    private fun BuildLauncher.setup(extraArguments: List<String> = listOf()) {
        setJavaHome(Platform.getJavaHome())

        val arguments = setupGradle()
        arguments.addAll(extraArguments)
        withArguments(*arguments.toTypedArray())
    }
}