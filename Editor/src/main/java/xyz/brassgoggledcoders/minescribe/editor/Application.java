package xyz.brassgoggledcoders.minescribe.editor;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import xyz.brassgoggledcoders.minescribe.editor.server.MineScribeNettyServer;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("application.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("MineScribe!");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        MineScribeNettyServer.getInstance().close();
    }

    public static void main(String[] args) {
        launch();
    }
}