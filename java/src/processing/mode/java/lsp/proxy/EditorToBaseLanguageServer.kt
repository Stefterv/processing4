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

    // This method needs to modify the following of the input params
    // the root path
    // the root URI
    // the workspace folders
    // this method needs to modify the following output results:
    // the server capabilities
    // the server info
    override fun initialize(params: InitializeParams?): CompletableFuture<InitializeResult?>? {
        PdeService.instance?.init()
        // Modify params here
        return downstream?.initialize(params)
            ?.thenApply {
                // Modify result here
                return@thenApply it
            }
    }

    override fun shutdown(): CompletableFuture<in Any>? {
        return downstream?.shutdown()
    }

    override fun exit() {
        downstream?.exit()
    }

    override fun getTextDocumentService(): TextDocumentService? {
        return downstream?.let { EditorToBaseTextDocumentService(it) }
    }

    override fun getWorkspaceService(): WorkspaceService? {
        return downstream?.let { EditorToBaseWorkspaceService(it) }
    }
}