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
import xyz.brassgoggledcoders.minescribe.preferences.ApplicationPreferences;
import xyz.brassgoggledcoders.minescribe.project.Project;
import xyz.brassgoggledcoders.minescribe.theme.ThemeManager;

import java.io.IOException;
import java.nio.file.Path;

public class MineScribe extends Application {
    private final ObjectProperty<ApplicationPreferences> applicationPreferences = new SimpleObjectProperty<>();
    private final ObjectProperty<Project> project = new SimpleObjectProperty<>();

    @Override
    public void start(Stage stage) throws IOException {
        this.applicationPreferences.set(ApplicationPreferences.load());

        Path lastProjectPath = this.applicationPreferences.getValue()
                .getLastProject();

        if (lastProjectPath != null) {
            this.project.setValue(Project.checkPath(lastProjectPath)
                    .fold(
                            Project::new,
                            errorString -> null
                    )
            );
        }

        this.project.addListener((observableValue, project, newProject) ->
                this.applicationPreferences.getValue()
                        .setLastProject(newProject.projectPath())
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
            Scene scene = new Scene(
                    loader.load(),
                    this.applicationPreferences.getValue().getHeight(),
                    this.applicationPreferences.getValue().getWidth()
            );

            ThemeManager.getInstance()
                    .setup(scene);
            ApplicationController applicationController = loader.getController();

            applicationController.getProjectProperty()
                    .bindBidirectional(this.project);
            applicationController.getApplicationPreferences()
                    .bindBidirectional(this.applicationPreferences);

            if (this.applicationPreferences.getValue().getXPos() != Double.MIN_NORMAL) {
                stage.setX(this.applicationPreferences.getValue().getXPos());
            }
            if (this.applicationPreferences.getValue().getYPos() != Double.MIN_NORMAL) {
                stage.setY(this.applicationPreferences.getValue().getYPos());
            }
            this.applicationPreferences.getValue()
                            .subscribeTo(stage);
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
