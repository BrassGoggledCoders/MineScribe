package xyz.brassgoggledcoders.minescribe.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import xyz.brassgoggledcoders.minescribe.preferences.ApplicationPreferences;
import xyz.brassgoggledcoders.minescribe.preferences.ProjectPreferences;
import xyz.brassgoggledcoders.minescribe.project.Project;
import xyz.brassgoggledcoders.minescribe.scene.control.toolwindow.ToolWindowLocation;

public class ApplicationController {
    private final ObjectProperty<ApplicationPreferences> applicationPreferences;
    private final ObjectProperty<Project> projectProperty;
    private final ObjectProperty<ProjectPreferences> projectPreferencesProperty;

    @FXML
    private AnchorPane application;

    public ApplicationController() {
        this.applicationPreferences = new SimpleObjectProperty<>();
        this.projectProperty = new SimpleObjectProperty<>();
        this.projectPreferencesProperty = new SimpleObjectProperty<>();
    }

    public ObjectProperty<Project> getProjectProperty() {
        return projectProperty;
    }

    public ObjectProperty<ApplicationPreferences> getApplicationPreferences() {
        return applicationPreferences;
    }

    public ObjectProperty<ProjectPreferences> getProjectPreferencesProperty() {
        return projectPreferencesProperty;
    }

    @FXML
    public ToolWindowLocation getLocation(String name) {
        return ToolWindowLocation.LEFT_TOP;
    }
}
