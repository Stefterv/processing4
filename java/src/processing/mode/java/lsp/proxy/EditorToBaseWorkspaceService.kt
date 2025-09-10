package processing.mode.java.lsp.proxy

import org.eclipse.lsp4j.CreateFilesParams
import org.eclipse.lsp4j.DeleteFilesParams
import org.eclipse.lsp4j.DidChangeConfigurationParams
import org.eclipse.lsp4j.DidChangeWatchedFilesParams
import org.eclipse.lsp4j.DidChangeWorkspaceFoldersParams
import org.eclipse.lsp4j.ExecuteCommandParams
import org.eclipse.lsp4j.RenameFilesParams
import org.eclipse.lsp4j.SymbolInformation
import org.eclipse.lsp4j.WorkspaceDiagnosticParams
import org.eclipse.lsp4j.WorkspaceDiagnosticReport
import org.eclipse.lsp4j.WorkspaceEdit
import org.eclipse.lsp4j.WorkspaceSymbol
import org.eclipse.lsp4j.WorkspaceSymbolParams
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.WorkspaceService
import java.util.concurrent.CompletableFuture

class EditorToBaseWorkspaceService(baseLanguageServer: LanguageServer) : WorkspaceService {
    val downstream: WorkspaceService = baseLanguageServer.workspaceService
    override fun executeCommand(params: ExecuteCommandParams?): CompletableFuture<in Any>? {
        return downstream.executeCommand(params)
    }

    override fun symbol(params: WorkspaceSymbolParams?): CompletableFuture<Either<List<SymbolInformation?>?, List<WorkspaceSymbol?>?>?>? {
        return downstream.symbol(params)
    }

    override fun resolveWorkspaceSymbol(workspaceSymbol: WorkspaceSymbol?): CompletableFuture<WorkspaceSymbol?>? {
        return downstream.resolveWorkspaceSymbol(workspaceSymbol)
    }

    override fun didChangeConfiguration(params: DidChangeConfigurationParams?) {
        downstream.didChangeConfiguration(params)
    }

    override fun didChangeWatchedFiles(params: DidChangeWatchedFilesParams?) {
        downstream.didChangeWatchedFiles(params)
    }

    override fun didChangeWorkspaceFolders(params: DidChangeWorkspaceFoldersParams?) {
        downstream.didChangeWorkspaceFolders(params)
    }

    override fun willCreateFiles(params: CreateFilesParams?): CompletableFuture<WorkspaceEdit?>? {
        return downstream.willCreateFiles(params)
    }

    override fun didCreateFiles(params: CreateFilesParams?) {
        downstream.didCreateFiles(params)
    }

    override fun willRenameFiles(params: RenameFilesParams?): CompletableFuture<WorkspaceEdit?>? {
        return downstream.willRenameFiles(params)
    }

    override fun didRenameFiles(params: RenameFilesParams?) {
        downstream.didRenameFiles(params)
    }

    override fun willDeleteFiles(params: DeleteFilesParams?): CompletableFuture<WorkspaceEdit?>? {
        return downstream.willDeleteFiles(params)
    }

    override fun didDeleteFiles(params: DeleteFilesParams?) {
        downstream.didDeleteFiles(params)
    }

    override fun diagnostic(params: WorkspaceDiagnosticParams?): CompletableFuture<WorkspaceDiagnosticReport?>? {
        return downstream.diagnostic(params)
    }
}