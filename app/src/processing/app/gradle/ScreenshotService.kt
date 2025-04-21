package processing.app.gradle

import com.sun.jdi.ObjectReference
import com.sun.jdi.VirtualMachine
import com.sun.jdi.event.BreakpointEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.Path
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

// TODO: Move to java mode
// TODO: Add more feedback when things go wrong
// TODO: Check if the sketch has a draw method
// TODO: Communicate the screen over the debugging connection (to support remote clients)
// TODO: Support recording videos
class ScreenshotService {
    companion object{
        fun takeScreenshot(vm: VirtualMachine, onComplete: (Path) -> Unit) {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                val manager = vm.eventRequestManager()
                val type = vm.classesByName("processing.core.PApplet").firstOrNull() ?: return@launch

                val methodDraw = type.methodsByName("handleDraw").firstOrNull() ?: return@launch
                val methodSave = type.methodsByName("save").firstOrNull() ?: return@launch

                val tempFile = Files.createTempFile( "sketch", ".png")

                val location = methodDraw.allLineLocations().last()

                val breakpoint = manager.createBreakpointRequest(location)
                breakpoint.enable()

                val queue = vm.eventQueue()
                val timeout = 5.seconds
                val startTime = TimeSource.Monotonic.markNow()

                while (startTime.elapsedNow() < timeout) {
                    try {
                        val events = queue.remove()
                        events.forEach { event ->
                            if (event !is BreakpointEvent) return@forEach

                            val thread = event.thread()
                            val frame = thread.frame(0)
                            val obj = frame.thisObject() ?: return@forEach

                            val arg = vm.mirrorOf(tempFile.toAbsolutePath().toString())

                            obj.invokeMethod(thread, methodSave, listOf(arg), ObjectReference.INVOKE_SINGLE_THREADED)

                            if (thread.isSuspended) {
                                thread.resume()
                            }
                            events.resume()
                            onComplete(tempFile)

                            return@launch
                        }
                        events.resume()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        breakpoint.disable()
                        manager.deleteEventRequest(breakpoint)
                    }
                }
            }
        }
    }
}