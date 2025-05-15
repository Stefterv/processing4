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
import processing.app.Language
import processing.app.Messages
import processing.app.Platform
import processing.app.Preferences
import processing.app.gradle.helpers.ActionGradleJob
import processing.app.gradle.helpers.BackgroundGradleJob
import processing.app.ui.Editor
import java.io.*
import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeText

// TODO: Remove dependency on editor (editor is not mockable, or move editor away from JFrame)
// TODO: Improve progress tracking
// TODO: PoC new debugger/tweak mode
// TODO: Allow for plugins to skip gradle entirely / new modes
// TODO: Improve background building
// TODO: Track build speed (for analytics?)

// TODO: Support offline mode

// The gradle service runs the gradle tasks and manages the gradle connection
// It will create the necessary build files for gradle to run
// Then it will kick off a new GradleJob to run the tasks
// GradleJob manages the gradle build and connects the debugger
class GradleService(val editor: Editor) {
    val folder: File get() = editor.sketch.folder
    val active = mutableStateOf(Preferences.getBoolean("run.use_gradle"))

    val jobs = mutableStateListOf<GradleJob>()
    val workingDir = kotlin.io.path.createTempDirectory()
    val debugPort = (30_000..60_000).random()

    var connection: ProjectConnection? = null

    private val scope = CoroutineScope(Dispatchers.IO)

    // Hooks for java to check if the Gradle service is running since mutableStateOf is not accessible in java
    fun getEnabled(): Boolean {
        return active.value
    }
    fun setEnabled(active: Boolean) {
        this.active.value = active
    }

    fun startService(){
        Messages.log("Starting Gradle service at $folder")
        connection = GradleConnector.newConnector()
            .forProjectDirectory(folder)
            .connect()
        editor.sketch.onFolderChangeListeners.add {
            connection?.close()
            connection = GradleConnector.newConnector()
                .forProjectDirectory(folder)
                .connect()
        }

        startListening()
        startBuilding()
    }


    private fun startBuilding(){
        scope.launch {
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

            // TODO: Attach listener to new tab created, callback has to be added to the editor
        }
    }
    // TODO: Add support for present
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
        val unsaved = editor.sketch.code
            .map { code ->
                val file = workingDir.resolve("unsaved/${code.fileName}")
                file.parent.toFile().mkdirs()
                // If tab is marked modified save it to the working directory
                // Otherwise delete the file
                if(code.isModified){
                    file.writeText(code.documentText)
                }else{
                    file.deleteIfExists()
                }
                return@map code.fileName
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
            "fullscreen" to false, // TODO: Implement
            "display" to 1, // TODO: Implement
            "external" to true,
            "editor.location" to editor.location.let { "${it.x},${it.y}" },
            //"awt.disable" to false,
            //"window.color" to "0xFF000000", // TODO: Implement
            //"stop.color" to "0xFF000000", // TODO: Implement
            "stop.hide" to false, // TODO: Implement
            "sketch.folder" to folder.absolutePath,
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
        val generate = buildGradle.let {
            if(!it.exists()) return@let true

            val contents = it.readText()
            if(!contents.contains("@processing-auto-generated")) return@let false

            val version = contents.substringAfter("version=").substringBefore("\n")
            if(version != Base.getVersionName()) return@let true

            val mode = contents.substringAfter("mode=").substringBefore(" ")
            if(editor.mode.title != mode) return@let true

            return@let Base.DEBUG
        }
        if (generate) {
            Messages.log("build.gradle.kts outdated or not found in ${folder}, creating one")
            val header = """
                // @processing-auto-generated mode=${editor.mode.title} version=${Base.getVersionName()}
                //
                """.trimIndent()

            // TODO: add instructions keys
            val instructions = Language.text("gradle.instructions")
                .split("\n")
                .joinToString("\n") { "// $it" }

            // TODO: Move the current configuration to java mode
            // TODO: Allow for other plugins to be registered
            // TODO: Allow for the whole configuration to be overridden
            // TODO: Define new plugin / mode schema
            val configuration =  """
                plugins{
                    id("org.processing.gradle") version "${Base.getVersionName()}"
                }
            """.trimIndent()
            val content = "${header}\n${instructions}\n${configuration}"
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