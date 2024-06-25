package xyz.brassgoggledcoders.minescribe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import org.controlsfx.dialog.ExceptionDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.project.Project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class OpenProjectController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenProjectController.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final ObjectProperty<Project> project = new SimpleObjectProperty<>();

    @FXML
    private Node anchorPane;

    @FXML
    public void findProject() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a Project Directory");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File directory = directoryChooser.showDialog(anchorPane.getScene()
                .getWindow()
        );
        Project.checkPath(directory.toPath(), true)
                .fold(
                        this::createProject,
                        errorString -> {
                            new Alert(Alert.AlertType.ERROR, errorString)
                                    .showAndWait();
                            return Optional.<Project>empty();
                        }
                ).ifPresent(this.project::setValue);
    }

    public Optional<Project> createProject(Path path) {
        Path projectFilePath = path.resolve("minescribe_project.json");
        if (Files.exists(projectFilePath)) {
            try {
                return Optional.of(MAPPER.readValue(projectFilePath.toFile(), Project.class));
            } catch (IOException e) {
                LOGGER.error("Failed to load existing project", e);
                ExceptionDialog exceptionDialog = new ExceptionDialog(e);
                exceptionDialog.setTitle("Failed to load existing project");
                exceptionDialog.showAndWait();
                return Optional.empty();
            }
        }

        Project newProject = new Project(path);
        try {
            MAPPER.writeValue(projectFilePath.toFile(), newProject);
        } catch (IOException e) {
            LOGGER.error("Failed to write new project", e);
        }
        return Optional.of(newProject);
    }

    public ObjectProperty<Project> projectProperty() {
        return this.project;
    }
}
