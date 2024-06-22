package xyz.brassgoggledcoders.minescribe.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import xyz.brassgoggledcoders.minescribe.project.Project;

import java.io.File;

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
        if (directory != null && directory.exists() && directory.isDirectory()) {
            if (directory.getName().endsWith(".minescribe")) {
                directory = directory.getParentFile();
            }

            File minescribeChild = new File(directory, ".minescribe");
            if (minescribeChild.exists() && minescribeChild.isDirectory()) {
                File loadComplete = new File(minescribeChild, ".load-complete");
                if (loadComplete.exists()) {
                    this.project.setValue(new Project(directory.toPath()));
                } else {
                    new Alert(Alert.AlertType.ERROR, ".minescribe folder does not contain required resources, run 'minescribe generate' command in Minecraft")
                            .showAndWait();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Folder does not container a valid .minescribe folder")
                        .showAndWait();
            }
        }
    }

    public ObjectProperty<Project> projectProperty() {
        return this.project;
    }
}
