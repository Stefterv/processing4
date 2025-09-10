package processing.mode.java.lsp.proxy

import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.DidSaveTextDocumentParams
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.TextDocumentService

class EditorToBaseTextDocumentService(val baseLanguageServer: LanguageServer): TextDocumentService {
    val downstream = baseLanguageServer.textDocumentService
    override fun didOpen(params: DidOpenTextDocumentParams?) {
        downstream.didOpen(params)
    }

    override fun didChange(params: DidChangeTextDocumentParams?) {
        downstream.didChange(params)
    }

    override fun didClose(params: DidCloseTextDocumentParams?) {
        downstream.didClose(params)
    }

    override fun didSave(params: DidSaveTextDocumentParams?) {
        downstream.didSave(params)
    }
}