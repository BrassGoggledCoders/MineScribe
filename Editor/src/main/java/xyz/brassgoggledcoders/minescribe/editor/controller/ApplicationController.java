package xyz.brassgoggledcoders.minescribe.editor.controller;

import atlantafx.base.theme.Theme;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.info.InfoKey;
import xyz.brassgoggledcoders.minescribe.core.info.InfoRepository;
import xyz.brassgoggledcoders.minescribe.editor.Application;
import xyz.brassgoggledcoders.minescribe.editor.event.page.RequestPageEvent;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;
import xyz.brassgoggledcoders.minescribe.editor.service.project.IProjectService;
import xyz.brassgoggledcoders.minescribe.editor.theme.ThemeManager;

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

    private final IProjectService projectService;


    @FXML
    public AnchorPane content;
    public Menu themeMenu;

    @Inject
    public ApplicationController(IProjectService projectService) {
        this.projectService = projectService;
    }

    @FXML
    public void initialize() {
        InfoRepository.getInstance().setValue(PAGE_REQUEST_KEY, this::trySetView);

        this.content.addEventHandler(
                RequestPageEvent.REQUEST_PAGE_EVENT_TYPE,
                event -> trySetView(event.getPageName())
        );

        this.projectService.loadLastProject();
        if (this.projectService.get() != null) {
            this.content.fireEvent(new RequestPageEvent("loading"));
        } else {
            this.content.fireEvent(new RequestPageEvent("select_project"));
        }

        for (Theme theme : ThemeManager.getInstance().getThemes()) {
            MenuItem themeItem = new MenuItem(theme.getName());
            themeItem.setOnAction(event -> ThemeManager.getInstance()
                    .setTheme(theme)
            );
            this.themeMenu.getItems()
                    .add(themeItem);
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
            this.projectService.setCurrentProject(project);
            this.content.fireEvent(new RequestPageEvent("loading"));
        }

    }
}