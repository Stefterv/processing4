package processing.mode.java.lsp.proxy

import org.eclipse.lsp4j.DidChangeConfigurationParams
import org.eclipse.lsp4j.DidChangeWatchedFilesParams
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.WorkspaceService

class EditorToBaseWorkspaceService(val baseLanguageServer: LanguageServer) : WorkspaceService {
    val downstream = baseLanguageServer.workspaceService
    override fun didChangeConfiguration(params: DidChangeConfigurationParams?) {
        downstream.didChangeConfiguration(params)
    }

    override fun didChangeWatchedFiles(params: DidChangeWatchedFilesParams?) {
        downstream.didChangeWatchedFiles(params)
    }
}