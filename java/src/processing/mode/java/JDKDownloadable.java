package processing.mode.java;

import org.jetbrains.annotations.NotNull;
import processing.utils.download.Downloadable;

public class JDKDownloadable implements Downloadable {
    @Override
    public boolean getRequired() {
        return false;
    }

    @Override
    @NotNull
    public String getName() {
        return "";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return "";
    }

    @Override
    public boolean isDownloaded() {
        return false;
    }
}
