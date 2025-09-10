package processing.mode.java.lsp.proxy

import org.eclipse.lsp4j.CallHierarchyIncomingCall
import org.eclipse.lsp4j.CallHierarchyIncomingCallsParams
import org.eclipse.lsp4j.CallHierarchyItem
import org.eclipse.lsp4j.CallHierarchyOutgoingCall
import org.eclipse.lsp4j.CallHierarchyOutgoingCallsParams
import org.eclipse.lsp4j.CallHierarchyPrepareParams
import org.eclipse.lsp4j.CodeAction
import org.eclipse.lsp4j.CodeActionParams
import org.eclipse.lsp4j.CodeLens
import org.eclipse.lsp4j.CodeLensParams
import org.eclipse.lsp4j.ColorInformation
import org.eclipse.lsp4j.ColorPresentation
import org.eclipse.lsp4j.ColorPresentationParams
import org.eclipse.lsp4j.Command
import org.eclipse.lsp4j.CompletionItem
import org.eclipse.lsp4j.CompletionList
import org.eclipse.lsp4j.CompletionParams
import org.eclipse.lsp4j.DeclarationParams
import org.eclipse.lsp4j.DefinitionParams
import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.DidSaveTextDocumentParams
import org.eclipse.lsp4j.DocumentColorParams
import org.eclipse.lsp4j.DocumentDiagnosticParams
import org.eclipse.lsp4j.DocumentDiagnosticReport
import org.eclipse.lsp4j.DocumentFormattingParams
import org.eclipse.lsp4j.DocumentHighlight
import org.eclipse.lsp4j.DocumentHighlightParams
import org.eclipse.lsp4j.DocumentLink
import org.eclipse.lsp4j.DocumentLinkParams
import org.eclipse.lsp4j.DocumentOnTypeFormattingParams
import org.eclipse.lsp4j.DocumentRangeFormattingParams
import org.eclipse.lsp4j.DocumentSymbol
import org.eclipse.lsp4j.DocumentSymbolParams
import org.eclipse.lsp4j.FoldingRange
import org.eclipse.lsp4j.FoldingRangeRequestParams
import org.eclipse.lsp4j.Hover
import org.eclipse.lsp4j.HoverParams
import org.eclipse.lsp4j.ImplementationParams
import org.eclipse.lsp4j.InlayHint
import org.eclipse.lsp4j.InlayHintParams
import org.eclipse.lsp4j.InlineValue
import org.eclipse.lsp4j.InlineValueParams
import org.eclipse.lsp4j.LinkedEditingRangeParams
import org.eclipse.lsp4j.LinkedEditingRanges
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.LocationLink
import org.eclipse.lsp4j.Moniker
import org.eclipse.lsp4j.MonikerParams
import org.eclipse.lsp4j.PrepareRenameDefaultBehavior
import org.eclipse.lsp4j.PrepareRenameParams
import org.eclipse.lsp4j.PrepareRenameResult
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.RenameParams
import org.eclipse.lsp4j.SelectionRange
import org.eclipse.lsp4j.SelectionRangeParams
import org.eclipse.lsp4j.SemanticTokens
import org.eclipse.lsp4j.SemanticTokensDelta
import org.eclipse.lsp4j.SemanticTokensDeltaParams
import org.eclipse.lsp4j.SemanticTokensParams
import org.eclipse.lsp4j.SemanticTokensRangeParams
import org.eclipse.lsp4j.SignatureHelp
import org.eclipse.lsp4j.SignatureHelpParams
import org.eclipse.lsp4j.SymbolInformation
import org.eclipse.lsp4j.TextEdit
import org.eclipse.lsp4j.TypeDefinitionParams
import org.eclipse.lsp4j.TypeHierarchyItem
import org.eclipse.lsp4j.TypeHierarchyPrepareParams
import org.eclipse.lsp4j.TypeHierarchySubtypesParams
import org.eclipse.lsp4j.TypeHierarchySupertypesParams
import org.eclipse.lsp4j.WillSaveTextDocumentParams
import org.eclipse.lsp4j.WorkspaceEdit
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.jsonrpc.messages.Either3
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.TextDocumentService
import java.util.concurrent.CompletableFuture

class EditorToBaseTextDocumentService(val baseLanguageServer: LanguageServer): TextDocumentService {
    val downstream = baseLanguageServer.textDocumentService
    override fun completion(position: CompletionParams?): CompletableFuture<Either<List<CompletionItem?>?, CompletionList?>?>? {
        return downstream.completion(position)
    }

    override fun resolveCompletionItem(unresolved: CompletionItem?): CompletableFuture<CompletionItem?>? {
        return downstream.resolveCompletionItem(unresolved)
    }

    override fun hover(params: HoverParams?): CompletableFuture<Hover?>? {
        return downstream.hover(params)
    }

    override fun signatureHelp(params: SignatureHelpParams?): CompletableFuture<SignatureHelp?>? {
        return downstream.signatureHelp(params)
    }

    override fun declaration(params: DeclarationParams?): CompletableFuture<Either<List<Location?>?, List<LocationLink?>?>?>? {
        return downstream.declaration(params)
    }

    override fun definition(params: DefinitionParams?): CompletableFuture<Either<List<Location?>?, List<LocationLink?>?>?>? {
        return downstream.definition(params)
    }

    override fun typeDefinition(params: TypeDefinitionParams?): CompletableFuture<Either<List<Location?>?, List<LocationLink?>?>?>? {
        return downstream.typeDefinition(params)
    }

    override fun implementation(params: ImplementationParams?): CompletableFuture<Either<List<Location?>?, List<LocationLink?>?>?>? {
        return downstream.implementation(params)
    }

    override fun references(params: ReferenceParams?): CompletableFuture<List<Location?>?>? {
        return downstream.references(params)
    }

    override fun documentHighlight(params: DocumentHighlightParams?): CompletableFuture<List<DocumentHighlight?>?>? {
        return downstream.documentHighlight(params)
    }

    override fun documentSymbol(params: DocumentSymbolParams?): CompletableFuture<List<Either<SymbolInformation?, DocumentSymbol?>?>?>? {
        return downstream.documentSymbol(params)
    }

    override fun codeAction(params: CodeActionParams?): CompletableFuture<List<Either<Command?, CodeAction?>?>?>? {
        return downstream.codeAction(params)
    }

    override fun resolveCodeAction(unresolved: CodeAction?): CompletableFuture<CodeAction?>? {
        return downstream.resolveCodeAction(unresolved)
    }

    override fun codeLens(params: CodeLensParams?): CompletableFuture<List<CodeLens?>?>? {
        return downstream.codeLens(params)
    }

    override fun resolveCodeLens(unresolved: CodeLens?): CompletableFuture<CodeLens?>? {
        return downstream.resolveCodeLens(unresolved)
    }

    override fun formatting(params: DocumentFormattingParams?): CompletableFuture<List<TextEdit?>?>? {
        return downstream.formatting(params)
    }

    override fun rangeFormatting(params: DocumentRangeFormattingParams?): CompletableFuture<List<TextEdit?>?>? {
        return downstream.rangeFormatting(params)
    }

    override fun onTypeFormatting(params: DocumentOnTypeFormattingParams?): CompletableFuture<List<TextEdit?>?>? {
        return downstream.onTypeFormatting(params)
    }

    override fun rename(params: RenameParams?): CompletableFuture<WorkspaceEdit?>? {
        return downstream.rename(params)
    }

    override fun linkedEditingRange(params: LinkedEditingRangeParams?): CompletableFuture<LinkedEditingRanges?>? {
        return downstream.linkedEditingRange(params)
    }

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

    override fun willSave(params: WillSaveTextDocumentParams?) {
        downstream.willSave(params)
    }

    override fun willSaveWaitUntil(params: WillSaveTextDocumentParams?): CompletableFuture<List<TextEdit?>?>? {
        return downstream.willSaveWaitUntil(params)
    }

    override fun documentLink(params: DocumentLinkParams?): CompletableFuture<List<DocumentLink?>?>? {
        return downstream.documentLink(params)
    }

    override fun documentLinkResolve(params: DocumentLink?): CompletableFuture<DocumentLink?>? {
        return downstream.documentLinkResolve(params)
    }

    override fun documentColor(params: DocumentColorParams?): CompletableFuture<List<ColorInformation?>?>? {
        return downstream.documentColor(params)
    }

    override fun colorPresentation(params: ColorPresentationParams?): CompletableFuture<List<ColorPresentation?>?>? {
        return downstream.colorPresentation(params)
    }

    override fun foldingRange(params: FoldingRangeRequestParams?): CompletableFuture<List<FoldingRange?>?>? {
        return downstream.foldingRange(params)
    }

    override fun prepareRename(params: PrepareRenameParams?): CompletableFuture<Either3<Range?, PrepareRenameResult?, PrepareRenameDefaultBehavior?>?>? {
        return downstream.prepareRename(params)
    }

    override fun prepareTypeHierarchy(params: TypeHierarchyPrepareParams?): CompletableFuture<List<TypeHierarchyItem?>?>? {
        return downstream.prepareTypeHierarchy(params)
    }

    override fun typeHierarchySupertypes(params: TypeHierarchySupertypesParams?): CompletableFuture<List<TypeHierarchyItem?>?>? {
        return downstream.typeHierarchySupertypes(params)
    }

    override fun typeHierarchySubtypes(params: TypeHierarchySubtypesParams?): CompletableFuture<List<TypeHierarchyItem?>?>? {
        return downstream.typeHierarchySubtypes(params)
    }

    override fun prepareCallHierarchy(params: CallHierarchyPrepareParams?): CompletableFuture<List<CallHierarchyItem?>?>? {
        return downstream.prepareCallHierarchy(params)
    }

    override fun callHierarchyIncomingCalls(params: CallHierarchyIncomingCallsParams?): CompletableFuture<List<CallHierarchyIncomingCall?>?>? {
        return downstream.callHierarchyIncomingCalls(params)
    }

    override fun callHierarchyOutgoingCalls(params: CallHierarchyOutgoingCallsParams?): CompletableFuture<List<CallHierarchyOutgoingCall?>?>? {
        return downstream.callHierarchyOutgoingCalls(params)
    }

    override fun selectionRange(params: SelectionRangeParams?): CompletableFuture<List<SelectionRange?>?>? {
        return downstream.selectionRange(params)
    }

    override fun semanticTokensFull(params: SemanticTokensParams?): CompletableFuture<SemanticTokens?>? {
        return downstream.semanticTokensFull(params)
    }

    override fun semanticTokensFullDelta(params: SemanticTokensDeltaParams?): CompletableFuture<Either<SemanticTokens?, SemanticTokensDelta?>?>? {
        return downstream.semanticTokensFullDelta(params)
    }

    override fun semanticTokensRange(params: SemanticTokensRangeParams?): CompletableFuture<SemanticTokens?>? {
        return downstream.semanticTokensRange(params)
    }

    override fun moniker(params: MonikerParams?): CompletableFuture<List<Moniker?>?>? {
        return downstream.moniker(params)
    }

    override fun inlayHint(params: InlayHintParams?): CompletableFuture<List<InlayHint?>?>? {
        return downstream.inlayHint(params)
    }

    override fun resolveInlayHint(unresolved: InlayHint?): CompletableFuture<InlayHint?>? {
        return downstream.resolveInlayHint(unresolved)
    }

    override fun inlineValue(params: InlineValueParams?): CompletableFuture<List<InlineValue?>?>? {
        return downstream.inlineValue(params)
    }

    override fun diagnostic(params: DocumentDiagnosticParams?): CompletableFuture<DocumentDiagnosticReport?>? {
        return downstream.diagnostic(params)
    }

}