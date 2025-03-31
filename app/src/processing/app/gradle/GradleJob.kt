package processing.app.gradle

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.sun.jdi.*
import com.sun.jdi.connect.AttachingConnector
import com.sun.jdi.event.ExceptionEvent
import com.sun.jdi.request.EventRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.events.ProgressListener
import org.gradle.tooling.events.problems.ProblemEvent
import org.gradle.tooling.events.problems.Severity
import org.gradle.tooling.events.problems.internal.DefaultFileLocation
import org.gradle.tooling.events.problems.internal.DefaultSingleProblemEvent
import org.gradle.tooling.events.task.TaskFinishEvent
import org.gradle.tooling.events.task.TaskStartEvent
import processing.app.Base
import processing.app.Messages
import java.io.InputStreamReader
import java.io.PipedInputStream
import java.io.PipedOutputStream
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource


// TODO: Refactor, reduce its scope
// TODO: Move the error reporting to its own file
// TODO: Move the output filtering to its own file
abstract class GradleJob{
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

    private val outputStream = PipedOutputStream()
    private val errorStream = PipedOutputStream()

    fun start() {
        service?.jobs?.add(this)
        val connection = service?.connection ?: return
        scope.launch {
            try {
                state.value = State.BUILDING

                connection.newBuild()
                    .apply {
                        configure()
                        withCancellationToken(cancel.token())
                        addStateListener()
                        addDebugging()
                        if(Base.DEBUG) {
                            setStandardOutput(System.out)
                            setStandardError(System.err)
                        } else {
                            setStandardOutput(outputStream)
                            setStandardError(errorStream)
                        }
                        run()
                    }


            }catch (e: Exception){
                Messages.log("Error while running: ${e.message}")
            }finally {
                state.value = State.DONE
                vm.value = null
            }
        }
        scope.launch {
            try {
                InputStreamReader(PipedInputStream(outputStream)).buffered().use { reader ->
                    reader.lineSequence()
                        .forEach { line ->
                            if (cancel.token().isCancellationRequested) {
                                return@launch
                            }
                            if (state.value != State.RUNNING) {
                                return@forEach
                            }
                            service?.editor?.console?.out?.println(line)
                        }
                }
            }catch (e: Exception){
                Messages.log("Error while reading output: ${e.message}")
            }
        }
        scope.launch {
            try {
                InputStreamReader(PipedInputStream(errorStream)).buffered().use { reader ->
                    reader.lineSequence()
                        .forEach { line ->
                            if (cancel.token().isCancellationRequested) {
                                return@launch
                            }
                            if (state.value != State.RUNNING) {
                                return@forEach
                            }
                            when{
                                line.contains("+[IMKClient subclass]: chose IMKClient_Modern") -> return@forEach
                                line.contains("+[IMKInputSession subclass]: chose IMKInputSession_Modern") -> return@forEach
                                line.startsWith("__MOVE__") -> return@forEach
                                else -> service?.editor?.console?.err?.println(line)
                            }
                        }
                }
            }catch (e: Exception){
                Messages.log("Error while reading error: ${e.message}")
            }
        }

    }

    fun cancel(){
        cancel.cancel()
    }

    private fun BuildLauncher.addStateListener(){
        addProgressListener(ProgressListener { event ->
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
            if(event is DefaultSingleProblemEvent) {
                if(event.definition.severity == Severity.ADVICE) return@ProgressListener
                problems.add(event)

                val path = (event.locations.firstOrNull() as DefaultFileLocation?)?.path
// Not Trimming indent as the content might contain newlines
                service?.editor?.console?.err?.println(
"""
${event.definition.id.displayName}: 
    ${event.contextualLabel.contextualLabel}
    
    ${event.details.details?.replace(path ?: "", "")}
    ${event.solutions.joinToString("\n") { it.solution }}
"""
                )
            }
        })
    }

    private fun listenForExceptions(virtualMachine: VirtualMachine){
        scope.launch {
            try {
                val manager = virtualMachine.eventRequestManager()

                val request = manager.createExceptionRequest(null, false, true)
                request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD)
                request.enable()

                val queue = virtualMachine.eventQueue()
                while (true) {
                    val eventSet = queue.remove()
                    for(event in eventSet) {
                        if (event is ExceptionEvent) {
                            printExceptionDetails(event)
                            event.thread().resume()
                        }
                    }
                    eventSet.resume()
                }
            } catch (e: Exception) {
                Messages.log("Error while listening for exceptions: ${e.message}")
            }

        }
    }
    fun printExceptionDetails(event: ExceptionEvent) {
        val exception = event.exception()
        val thread = event.thread()
        val location = event.location()
        val stackFrames = thread.frames()

        println("\n🚨 Exception Caught 🚨")
        println("Type       : ${exception.referenceType().name()}")
//        println("Message    : ${getExceptionMessage(exception)}")
        println("Thread     : ${thread.name()}")
        println("Location   : ${location.sourcePath()}:${location.lineNumber()}\n")

        // Separate stack frames
        val userFrames = mutableListOf<StackFrame>()
        val processingFrames = mutableListOf<StackFrame>()

        stackFrames.forEach { frame ->
            val className = frame.location().declaringType().name()
            if (className.startsWith("processing.")) {
                processingFrames.add(frame)
            } else {
                userFrames.add(frame)
            }
        }

        // Print user frames first
        println("🔍 Stacktrace (Your Code First):")
        userFrames.forEachIndexed { index, frame -> printStackFrame(index, frame) }

        // Print Processing frames second
        if (processingFrames.isNotEmpty()) {
            println("\n🔧 Processing Stacktrace (Hidden Initially):")
            processingFrames.forEachIndexed { index, frame -> printStackFrame(index, frame) }
        }

        println("──────────────────────────────────\n")
    }

    fun printStackFrame(index: Int, frame: StackFrame) {
        val location = frame.location()
        val method = location.method()
        println("   #$index ${location.sourcePath()}:${location.lineNumber()} -> ${method.declaringType().name()}.${method.name()}()")
    }

    // Extracts the exception's message
    fun getExceptionMessage(exception: ObjectReference): String {
        val messageMethod = exception.referenceType().methodsByName("getMessage").firstOrNull() ?: return "Unknown"
        val messageValue = exception.invokeMethod(null, messageMethod, emptyList(), ObjectReference.INVOKE_SINGLE_THREADED)
        return (messageValue as? StringReference)?.value() ?: "Unknown"
    }


    private fun BuildLauncher.addDebugging(){
        addProgressListener(ProgressListener { event ->
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
                            sketch.resume()
                            Messages.log("Attached to VM: ${sketch.name()}")
                            listenForExceptions(sketch)
                            break
                        } catch (e: Exception) {
                            Messages.log("Error while attaching to VM: ${e.message}... Retrying")
                        }
                        delay(250)
                    }
                }
            } catch (e: Exception) {
                Messages.log("Error while attaching to VM: ${e.message}")
            }
        })
    }
}