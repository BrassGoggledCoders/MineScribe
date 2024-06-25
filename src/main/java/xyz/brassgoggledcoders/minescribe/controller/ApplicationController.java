package xyz.brassgoggledcoders.minescribe.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import xyz.brassgoggledcoders.minescribe.preferences.ApplicationPreferences;
import xyz.brassgoggledcoders.minescribe.project.Project;
import xyz.brassgoggledcoders.minescribe.scene.control.toolwindow.ToolWindowLocation;

public class ApplicationController {
    private final ObjectProperty<ApplicationPreferences> applicationPreferences = new SimpleObjectProperty<>();
    private final ObjectProperty<Project> projectProperty;

    @FXML
    private AnchorPane application;

    public ApplicationController() {
        this.projectProperty = new SimpleObjectProperty<>();
    }

    public ObjectProperty<Project> getProjectProperty() {
        return projectProperty;
    }

    public ObjectProperty<ApplicationPreferences> getApplicationPreferences() {
        return applicationPreferences;
    }

    @FXML
    public ToolWindowLocation getLocation(String name) {
        return ToolWindowLocation.LEFT_TOP;
    }
}
