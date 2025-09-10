package processing.mode.java.lsp.proxy;


import org.eclipse.lsp4j.launch.LSPLauncher;

import java.io.IOException;

public class PdeLanguageServerProxy {
    static public void main(String[] args) throws IOException {
        // Start the base LSP server process
        var processBuilder = new ProcessBuilder()
                .command("bash", "-c", "/Applications/Processing.app/Contents/MacOS/Processing lsp");
        var baseLSPProcess = processBuilder.start();

        var baseIn = baseLSPProcess.getInputStream();
        var baseOut = baseLSPProcess.getOutputStream();

        // This proxy creates two LSP pipes,
        //      one from the connecting editor to the base server piping requests like initialize, shutdown etc to the base LSP
        //      another on from the base lsp to the connecting editor, piping notifications like diagnostics, log messages etc to the editor

        // Create the LSP client and connect it to the base server
        // This will transport messages emitted from the base server to the editor
        var baseToEditorLanguageClient = new BaseToEditorLanguageClient();
        var baseToEditorLauncher = LSPLauncher.createClientLauncher(baseToEditorLanguageClient, baseIn, baseOut);
        var baseServer = baseToEditorLauncher.getRemoteProxy();

        // Create the LSP server this will listen to requests from the editor and transport them to the base server
        var editorToBaseLanguageServer = new EditorToBaseLanguageServer();
        // Connect the down stream, needs to happen before starting the server
        editorToBaseLanguageServer.setDownstream(baseServer);
        var editorToBaseLauncher = LSPLauncher.createServerLauncher(editorToBaseLanguageServer, System.in, System.out);
        var editorClient = editorToBaseLauncher.getRemoteProxy();

        // Connect the up stream
        baseToEditorLanguageClient.setUpstream(editorClient);

        // Why? unused for now
        editorToBaseLanguageServer.setUpstream(editorClient);
        baseToEditorLanguageClient.setDownstream(baseServer);

        // Start everything
        baseToEditorLauncher.startListening();
        editorToBaseLauncher.startListening();
    }
}
