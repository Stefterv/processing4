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
    var upstream: LanguageClient? = null

    override fun telemetryEvent(`object`: Any?) {
        upstream?.telemetryEvent(`object`)
    }

    override fun publishDiagnostics(diagnostics: PublishDiagnosticsParams?) {
        upstream?.publishDiagnostics(diagnostics)
    }

    override fun showMessage(messageParams: MessageParams?) {
        upstream?.showMessage(messageParams)
    }

    override fun showMessageRequest(requestParams: ShowMessageRequestParams?): CompletableFuture<MessageActionItem?>? {
        return upstream?.showMessageRequest(requestParams)
    }

    override fun logMessage(message: MessageParams?) {
        upstream?.logMessage(message)
    }

}