package xyz.brassgoggledcoders.minescribe.editor.controller.tab;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.editor.scene.tab.FileTab;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileViewController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileViewController.class);
    @FXML
    public TextArea fileContents;
    @FXML
    public FileTab tab;

    @FXML
    public void initialize() {
        this.tab.pathProperty()
                .addListener(this::invalidated);
    }

    public void invalidated(Observable observable) {
        Path path = this.tab.pathProperty()
                .getValue();

        if (path != null && Files.isRegularFile(path)) {
            try {
                String fileString = Files.readString(path);
                this.fileContents.setText(fileString);
            } catch (IOException e) {
                LOGGER.error("Failed to read file to text area", e);
                this.fileContents.clear();
            }
        } else {
            this.fileContents.clear();
        }
    }
}
