package xyz.brassgoggledcoders.minescribe.editor.controller;

import atlantafx.base.theme.Theme;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;
import xyz.brassgoggledcoders.minescribe.editor.service.page.IPageService;
import xyz.brassgoggledcoders.minescribe.editor.service.project.IProjectService;
import xyz.brassgoggledcoders.minescribe.editor.theme.ThemeManager;

import java.io.File;
import java.nio.file.Path;

public class ApplicationController {
    private final IPageService pageService;
    private final IProjectService projectService;


    @FXML
    public AnchorPane content;
    public Menu themeMenu;

    @Inject
    public ApplicationController(IPageService pageService, IProjectService projectService) {
        this.pageService = pageService;
        this.projectService = projectService;
    }

    @FXML
    public void initialize() {
        this.projectService.loadLastProject();
        this.pageService.setPagePane(this.content);
        if (this.projectService.get() != null) {
            this.pageService.setPage("loading");
        } else {
            this.pageService.setPage("select_project");
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
            this.pageService.setPage("loading");
        }

    }
}