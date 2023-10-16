package xyz.brassgoggledcoders.minescribe.editor.controller.tab;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NoFormController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoFormController.class);
    @FXML
    public TextArea fileContents;

    public void setPathToFile(Path path) {
        if (Files.isRegularFile(path)) {
            try {
                String fileString = Files.readString(path);
                this.fileContents.setText(fileString);
            } catch (IOException e) {
                LOGGER.error("Failed to read file to text area", e);
            }
        }
    }
}
