package xyz.brassgoggledcoders.minescribe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import xyz.brassgoggledcoders.minescribe.controller.ApplicationController;
import xyz.brassgoggledcoders.minescribe.controller.OpenProjectController;
import xyz.brassgoggledcoders.minescribe.project.Project;
import xyz.brassgoggledcoders.minescribe.theme.ThemeManager;
import xyz.brassgoggledcoders.minescribe.util.PreferenceHelper;

import java.io.IOException;

public class MineScribe extends Application {
    private final ObjectProperty<Project> project = new SimpleObjectProperty<>();

    @Override
    public void start(Stage stage) throws IOException {
        this.project.setValue(PreferenceHelper.loadPreference(Project.class, "project"));
        this.project.addListener((observableValue, project, newProject) ->
                PreferenceHelper.savePreferences(project, "project")
        );

        if (this.project.getValue() == null) {
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Open New MineScribe Project");

            FXMLLoader loader = new FXMLLoader(MineScribe.class.getResource("open_project.fxml"));
            dialogStage.setScene(new Scene(loader.load()));
            this.project.bindBidirectional(loader.<OpenProjectController>getController()
                    .projectProperty()
            );
            this.project.addListener((observableValue, project, newProject) -> {
                if (newProject != null) {
                    dialogStage.hide();
                }
            });
            dialogStage.showAndWait();
        }

        if (project.getValue() != null) {
            FXMLLoader loader = new FXMLLoader(MineScribe.class.getResource("application.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 1000);
            ThemeManager.getInstance()
                    .setup(scene);
            loader.<ApplicationController>getController()
                    .getProjectProperty()
                    .bindBidirectional(this.project);
            stage.setScene(scene);
            stage.show();
        } else {
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
