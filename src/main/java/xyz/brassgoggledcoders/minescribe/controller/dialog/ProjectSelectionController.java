package xyz.brassgoggledcoders.minescribe.controller.dialog;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.service.ProjectService;

import java.io.File;

@Component
@FxmlView("/xyz/brassgoggledcoders/minescribe/dialog/project_selection.fxml")
public class ProjectSelectionController extends DialogController {
    private final ProjectService projectService;

    @FXML
    private Node anchorPane;

    @FXML
    private Stage stage;

    @Autowired
    public ProjectSelectionController(ApplicationContext applicationContext, ProjectService projectService) {
        super(applicationContext);
        this.projectService = projectService;
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
            if (this.projectService.tryOpenProject(directory.toPath())) {
                this.stage.hide();
            }
        }
    }

    @Override
    protected Stage getStage() {
        return this.stage;
    }
}
