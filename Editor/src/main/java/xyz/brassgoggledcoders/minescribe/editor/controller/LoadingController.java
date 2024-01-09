package xyz.brassgoggledcoders.minescribe.editor.controller;

import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;
import xyz.brassgoggledcoders.minescribe.editor.registry.EditorRegistries;
import xyz.brassgoggledcoders.minescribe.editor.service.page.IPageService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;

public class LoadingController {
    private final IPageService pageService;
    private final Provider<Project> projectProvider;

    @FXML
    public StackPane loading;

    @FXML
    public Text loadingStatus;

    private Project project;

    @Inject
    public LoadingController(IPageService pageService, Provider<Project> projectProvider) {
        this.pageService = pageService;
        this.projectProvider = projectProvider;
    }

    @FXML
    public void initialize() {
        this.project = this.projectProvider.get();
        if (project != null) {
            Path mineScribePath = project.getMineScribeFolder();
            Path loadComplete = mineScribePath.resolve(".load_complete");
            if (Files.exists(loadComplete)) {
                this.startProjectLoad();
            } else {
                Timer time = new Timer();
                time.schedule(new CheckLoadComplete(loadComplete, time), 3000L);
            }
        } else {
            this.loadingStatus.setText("Failed to find active project, try reopening project.");
        }
    }

    private void startProjectLoad() {
        this.loadingStatus.setText("Found Project. Loading Files from ./minescribe");
        EditorRegistries.load(this.project.getMineScribeFolder());
        this.loadingStatus.setText("Project Loaded. Opening Editor");

        this.pageService.setPage("editor");
    }

    private class CheckLoadComplete extends TimerTask {
        private final Path loadPath;
        private final Timer timer;

        private CheckLoadComplete(Path loadPath, Timer timer) {
            this.loadPath = loadPath;
            this.timer = timer;
        }

        @Override
        public void run() {
            if (Files.exists(this.loadPath)) {
                Platform.runLater(LoadingController.this::startProjectLoad);
            } else {
                timer.schedule(new CheckLoadComplete(loadPath, timer), 3000L);
            }
        }
    }
}
