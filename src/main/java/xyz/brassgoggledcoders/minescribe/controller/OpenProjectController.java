package xyz.brassgoggledcoders.minescribe.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import xyz.brassgoggledcoders.minescribe.project.Project;

import java.io.File;
import java.util.Optional;

public class OpenProjectController {

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
        Project.checkPath(directory.toPath())
                .fold(
                        path -> Optional.of(new Project(path)),
                        errorString -> {
                            new Alert(Alert.AlertType.ERROR, errorString)
                                    .showAndWait();
                            return Optional.<Project>empty();
                        }
                ).ifPresent(this.project::setValue);
    }

    public ObjectProperty<Project> projectProperty() {
        return this.project;
    }
}
