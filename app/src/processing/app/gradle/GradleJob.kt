package processing.app.gradle

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.sun.jdi.Bootstrap
import com.sun.jdi.VirtualMachine
import com.sun.jdi.connect.AttachingConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.events.ProgressListener
import org.gradle.tooling.events.problems.ProblemEvent
import org.gradle.tooling.events.task.TaskFinishEvent
import org.gradle.tooling.events.task.TaskStartEvent
import processing.app.Messages
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

// TODO: Capture and filter gradle output
open abstract class GradleJob{
    enum class State{
        NONE,
        BUILDING,
        RUNNING,
        DONE
    }

    var service: GradleService? = null
    var configure: BuildLauncher.() -> Unit = {}

    val state = mutableStateOf(State.NONE)
    val vm = mutableStateOf<VirtualMachine?>(null)
    val problems = mutableStateListOf<ProblemEvent>()

    private val scope = CoroutineScope(Dispatchers.IO)
    private val cancel = GradleConnector.newCancellationTokenSource()

    fun start() {
        service?.jobs?.add(this)
        val connection = service?.connection ?: return
        scope.launch {
            try {
                state.value = State.BUILDING

                connection.newBuild()
                    .apply {
                        configure()
                    }
                    .withCancellationToken(cancel.token())
                    .addStateListener()
                    .addDebugging()
                    .run()

            }catch (e: Exception){
                if(state.value == State.RUNNING){
                    Messages.log("Error while running: ${e.message}")
                }else{
                    throw e
                }
            }finally {
                state.value = State.DONE
                vm.value = null
            }

        }
    }

    fun cancel(){
        cancel.cancel()
    }

    private fun BuildLauncher.addStateListener(): BuildLauncher{
        this.addProgressListener(ProgressListener { event ->
            if(event is TaskStartEvent) {
                when(event.descriptor.name) {
                    ":run" -> {
                        state.value = State.RUNNING
                        Messages.log("Start run")
                    }
                }

            }
            if(event is TaskFinishEvent) {
                when(event.descriptor.name){
                    ":jar"->{
                        state.value = State.NONE
                        Messages.log("Jar finished")
                    }
                    ":run"->{
                        state.value = State.NONE
                    }
                }
            }
            if(event is ProblemEvent) {
                problems.add(event)
            }
        })
        return this
    }

    private fun BuildLauncher.addDebugging(): BuildLauncher{
        this.addProgressListener(ProgressListener { event ->
            if (event !is TaskStartEvent) return@ProgressListener
            if (event.descriptor.name != ":run") return@ProgressListener

            try {
                val port = service?.debugPort.toString()
                Messages.log("Attaching to VM $port")
                val connector = Bootstrap.virtualMachineManager().allConnectors()
                    .firstOrNull { it.name() == "com.sun.jdi.SocketAttach" }
                        as AttachingConnector?
                    ?: throw IllegalStateException("No socket attach connector found")
                val args = connector.defaultArguments()
                args["port"]?.setValue(port)

                // Try to attach the debugger, retrying if it fails
                scope.launch {
                    val start = TimeSource.Monotonic.markNow()
                    while (start.elapsedNow() < 10.seconds) {
                        try {
                            val sketch = connector.attach(args)
                            vm.value = sketch
                            break
                            Messages.log("Attached to VM: ${sketch.name()}")
                        } catch (e: Exception) {
                            Messages.log("Error while attaching to VM: ${e.message}... Retrying")
                        }
                        delay(250)
                    }
                    if(vm.value == null) throw IllegalStateException("Failed to attach to VM after 10 seconds")
                }
            } catch (e: Exception) {
                Messages.log("Error while attaching to VM: ${e.message}")
            }
        })
        return this
    }
}