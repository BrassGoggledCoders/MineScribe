package xyz.brassgoggledcoders.minescribe.editor;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

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

    public static void main(String[] args) {
        launch();
    }
}