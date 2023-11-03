package xyz.brassgoggledcoders.minescribe.editor;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import xyz.brassgoggledcoders.minescribe.core.info.InfoRepository;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;

import java.io.IOException;
import java.util.Objects;
import java.util.prefs.Preferences;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("application.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        scene.getStylesheets()
                .add(Objects.requireNonNull(this.getClass().getResource("minescribe_style.css"))
                        .toExternalForm()
                );
        scene.getStylesheets().add("/com/dlsc/formsfx/view/renderer/style.css");
        stage.setTitle("MineScribe!");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        Project project = InfoRepository.getInstance().getValue(Project.KEY);
        if (project != null) {
            project.trySave(Preferences.userNodeForPackage(Application.class));
        }
    }

    public static void main(String[] args) {
        launch();
    }
}