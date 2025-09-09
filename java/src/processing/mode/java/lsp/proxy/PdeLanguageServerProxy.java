package processing.mode.java.lsp.proxy;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.*;

import java.util.concurrent.CompletableFuture;

public class PdeLanguageServerProxy implements LanguageServer, LanguageClientAware {
    static public void main(String[] args) {
        var input = System.in;
        var output = System.out;
        System.setOut(System.err);

        var server = new PdeLanguageServerProxy();
        var launcher =
                LSPLauncher.createServerLauncher(
                        server,
                        input,
                        output
                );
        var client = launcher.getRemoteProxy();
        server.connect(client);
        launcher.startListening();

        //        Approach 1: Start the PdeProxy and pipe everything else through to the Eclipse JDT LS and modify paths and such at runtime
        //        Approach 2: Manually write the actions that will send the correct data the Eclipse JDT LS
    }

    @Override
    public void connect(LanguageClient client) {

    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        return null;
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        return null;
    }

    @Override
    public void exit() {

    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return null;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return null;
    }
}
