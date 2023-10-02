package xyz.brassgoggledcoders.minescribe.editor.file;

import javafx.scene.control.TreeItem;
import org.fxmisc.livedirs.DirectoryModel;
import org.fxmisc.livedirs.LiveDirs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class FileHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(FileHandler.class);
    private static FileHandler INSTANCE;


    private final LiveDirs<ChangeSource> liveDirs;

    public FileHandler(LiveDirs<ChangeSource> liveDirs) {
        this.liveDirs = liveDirs;
    }

    public void addDirectory(Path path) {
        this.liveDirs.addTopLevelDirectory(path);
    }

    public DirectoryModel<ChangeSource> getModel() {
        return liveDirs.model();
    }

    public void reload(Path path) {
        this.liveDirs.refresh(path);
    }

    public static void initialize() {
        try {
            LiveDirs<ChangeSource> initialLive = new LiveDirs<>(ChangeSource.EXTERNAL);
            INSTANCE = new FileHandler(initialLive);
        } catch (IOException e) {
            LOGGER.error("Failed to Initialize FileHandler", e);
        }
    }

    public static FileHandler getInstance() {
        return Objects.requireNonNull(INSTANCE, "initialize has not been called");
    }


    public enum ChangeSource {
        INTERNAL,
        EXTERNAL
    }
}
