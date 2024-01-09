package xyz.brassgoggledcoders.minescribe.editor;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.bridge.SLF4JBridgeHandler;
import xyz.brassgoggledcoders.minescribe.core.MineScribeRuntime;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.javascript.ScriptHandler;
import xyz.brassgoggledcoders.minescribe.editor.service.GuiceModule;
import xyz.brassgoggledcoders.minescribe.editor.service.project.IProjectService;
import xyz.brassgoggledcoders.minescribe.editor.theme.ThemeManager;

import java.io.IOException;
import java.util.Objects;

public class Application extends javafx.application.Application {
    private final Injector injector = Guice.createInjector(new GuiceModule());

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("application.fxml"));
        fxmlLoader.setControllerFactory(injector::getInstance);
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        scene.getStylesheets()
                .add(Objects.requireNonNull(this.getClass().getResource("minescribe_style.css"))
                        .toExternalForm()
                );
        ThemeManager.getInstance()
                .setScene(scene);
        if (ThemeManager.getInstance().getTheme() == null) {
            ThemeManager.getInstance()
                    .setTheme(ThemeManager.getInstance()
                            .getDefaultTheme()
                    );
        }
        scene.getStylesheets().add("/com/dlsc/formsfx/view/renderer/style.css");
        stage.setTitle("MineScribe!");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        this.injector.getInstance(IProjectService.class)
                        .saveProject();
        FileHandler.dispose();
        ScriptHandler.getInstance()
                .close();
    }

    public static void main(String[] args) {
        SLF4JBridgeHandler.install();
        MineScribeRuntime.setRuntime(MineScribeRuntime.APPLICATION);
        launch();
    }
}