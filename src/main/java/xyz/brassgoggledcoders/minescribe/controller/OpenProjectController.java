package xyz.brassgoggledcoders.minescribe.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.service.ProjectService;

import java.io.File;

@Component
@FxmlView("/xyz/brassgoggledcoders/minescribe/open_project.fxml")
public class OpenProjectController {
    private final ProjectService projectService;
    private final BooleanProperty openedProject;

    @FXML
    private Node anchorPane;


    @Autowired
    public OpenProjectController(ProjectService projectService) {
        this.projectService = projectService;
        this.openedProject = new SimpleBooleanProperty(this, "openedProject", false);
    }

    public BooleanProperty openedProjectProperty() {
        return openedProject;
    }

    @FXML
    public void findProject() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a Project Directory");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File directory = directoryChooser.showDialog(anchorPane.getScene()
                .getWindow()
        );
        if (directory != null) {
            this.openedProject.setValue(this.projectService.tryOpenProject(directory.toPath()));
        }
    }
}
