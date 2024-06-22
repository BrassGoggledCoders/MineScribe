package xyz.brassgoggledcoders.minescribe;

import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import xyz.brassgoggledcoders.minescribe.controller.OpenProjectController;
import xyz.brassgoggledcoders.minescribe.project.Project;

import java.io.IOException;
import java.util.prefs.Preferences;

public class MineScribe extends Application {
    private final ObjectProperty<Project> project = new SimpleObjectProperty<>();

    @Override
    public void start(Stage stage) throws IOException {

        Preferences minescribeUserNode = Preferences.userNodeForPackage(MineScribe.class);
        this.project.setValue(Project.loadProject(minescribeUserNode));
        this.project.addListener((observableValue, project, newProject) ->
                Project.saveProject(minescribeUserNode, newProject)
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
            String javaVersion = System.getProperty("java.version");
            String javafxVersion = System.getProperty("javafx.version");
            Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
            Scene scene = new Scene(new StackPane(l), 640, 480);
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
