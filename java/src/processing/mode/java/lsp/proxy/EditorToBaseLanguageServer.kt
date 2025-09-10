package processing.mode.java.lsp.proxy

import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.InitializeResult
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.services.WorkspaceService
import java.util.concurrent.CompletableFuture

class EditorToBaseLanguageServer() : LanguageServer {
    var downstream: LanguageServer? = null
    var upstream: LanguageClient? = null

    override fun initialize(params: InitializeParams?): CompletableFuture<InitializeResult?>? {
        return downstream?.initialize(params)
    }

    override fun shutdown(): CompletableFuture<in Any>? {
        return downstream?.shutdown()
    }

    override fun exit() {
        downstream?.exit()
    }

    override fun getTextDocumentService(): TextDocumentService? {
        return downstream?.textDocumentService
    }

    override fun getWorkspaceService(): WorkspaceService? {
        return downstream?.workspaceService
    }
}