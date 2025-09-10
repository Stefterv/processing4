package processing.mode.java.lsp.proxy

import org.eclipse.lsp4j.MessageActionItem
import org.eclipse.lsp4j.MessageParams
import org.eclipse.lsp4j.PublishDiagnosticsParams
import org.eclipse.lsp4j.ShowMessageRequestParams
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageServer
import java.util.concurrent.CompletableFuture

class BaseLanguageClient: LanguageClient {
    var downstream: LanguageServer? = null

    override fun telemetryEvent(`object`: Any?) {
        println("Telemetry: $`object`")
    }

    override fun publishDiagnostics(diagnostics: PublishDiagnosticsParams?) {
        println("Diagnostics: $diagnostics")
    }

    override fun showMessage(messageParams: MessageParams?) {
        println("Message: $messageParams")
    }

    override fun showMessageRequest(requestParams: ShowMessageRequestParams?): CompletableFuture<MessageActionItem?>? {
        println("Message request: $requestParams")
        return CompletableFuture.completedFuture(null)
    }

    override fun logMessage(message: MessageParams?) {
        TODO("Not yet implemented")
    }

}