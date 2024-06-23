package xyz.brassgoggledcoders.minescribe.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import xyz.brassgoggledcoders.minescribe.project.Project;

public class ApplicationController {
    private final ObjectProperty<Project> projectProperty;

    @FXML
    private AnchorPane application;

    public ApplicationController() {
        this.projectProperty = new SimpleObjectProperty<>();
    }

    public ObjectProperty<Project> getProjectProperty() {
        return projectProperty;
    }

    public void setProject(Project value) {
        this.projectProperty.setValue(value);
    }

    public Project getProject() {
        return this.projectProperty.getValue();
    }
}
