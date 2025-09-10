package processing.mode.java.lsp.proxy;


import org.eclipse.lsp4j.launch.LSPLauncher;

import java.io.IOException;

public class PdeLanguageServerProxy {
    static public void main(String[] args) throws IOException {
        var processBuilder = new ProcessBuilder()
                .command("bash", "-c", "/Applications/Processing.app/Contents/MacOS/Processing lsp");
        var baseLSP = processBuilder.start();

        var baseIn = baseLSP.getInputStream();
        var baseOut = baseLSP.getOutputStream();
        var baseErr = baseLSP.getErrorStream();

        var baseClient = new BaseLanguageClient();
        var baseServerLauncher = LSPLauncher.createClientLauncher(baseClient, baseIn, baseOut);
        var downstreamServer = baseServerLauncher.getRemoteProxy();
        baseClient.setDownstream(downstreamServer);
        baseServerLauncher.startListening();

        var baseServer = new BaseLanguageServer(downstreamServer);

        var baseClientLauncher = LSPLauncher.createServerLauncher(baseServer, System.in, System.out);
        var upstreamClient = baseClientLauncher.getRemoteProxy();
        baseServer.setUpstream(upstreamClient);

        baseClientLauncher.startListening();
    }
}
