package xyz.brassgoggledcoders.minescribe.editor.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.info.InfoKey;
import xyz.brassgoggledcoders.minescribe.core.info.InfoRepository;
import xyz.brassgoggledcoders.minescribe.editor.Application;
import xyz.brassgoggledcoders.minescribe.editor.event.page.RequestPageEvent;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

public class ApplicationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);
    public static final InfoKey<Consumer<String>> PAGE_REQUEST_KEY = new InfoKey<>() {
    };

    @FXML
    public AnchorPane content;

    @FXML
    public void initialize() {
        InfoRepository.getInstance().setValue(PAGE_REQUEST_KEY, this::trySetView);

        this.content.addEventHandler(
                RequestPageEvent.REQUEST_PAGE_EVENT_TYPE,
                event -> trySetView(event.getPageName())
        );

        Preferences preferences = Preferences.userNodeForPackage(Application.class);

        Project project = Project.tryLoad(preferences);
        if (project != null) {
            InfoRepository.getInstance().setValue(Project.KEY, project);
            this.content.fireEvent(new RequestPageEvent("loading"));
        }

        if (InfoRepository.getInstance().getValue(Project.KEY) == null) {
            this.content.fireEvent(new RequestPageEvent("select_project"));
        }
    }

    public void trySetView(String name) {
        if (content.getChildren().stream().noneMatch(node -> node.getId().equals(name))) {
            URL url = Application.class.getResource(name + ".fxml");
            if (url == null) {
                LOGGER.error("Failed to find resource for: {}", name);
            } else {
                Platform.runLater(() -> {
                    try {
                        Node node = FXMLLoader.load(url);
                        content.getChildren().clear();
                        AnchorPane.setTopAnchor(node, 0D);
                        AnchorPane.setBottomAnchor(node, 0D);
                        AnchorPane.setLeftAnchor(node, 0D);
                        AnchorPane.setRightAnchor(node, 0D);
                        this.content.getChildren().add(node);
                    } catch (IOException e) {
                        LOGGER.error("Failed to load FXML for: %s".formatted(name), e);
                    }
                });
            }
        }
    }

    public void openProject(ActionEvent ignoredActionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Project");
        File projectFile = directoryChooser.showDialog(this.content.getScene().getWindow());
        if (projectFile != null) {
            Path projectPath = projectFile.toPath();
            if (projectPath.endsWith(".minescribe")) {
                projectPath = projectPath.getParent();
            }
            Project project = new Project(projectPath);
            InfoRepository.getInstance().setValue(Project.KEY, project);
            project.trySave(Preferences.userNodeForPackage(Application.class));
            this.content.fireEvent(new RequestPageEvent("loading"));
        }

    }
}