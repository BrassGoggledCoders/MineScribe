package xyz.brassgoggledcoders.minescribe.controller.dialog;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.service.ProjectService;

import java.io.File;

@Component
@FxmlView("/xyz/brassgoggledcoders/minescribe/dialog/project_selection.fxml")
public class ProjectSelectionController implements IDialogController<Boolean> {
    private final ProjectService projectService;
    private final SimpleBooleanProperty projectFoundProperty;
    private final SimpleStringProperty titleProperty;

    @FXML
    private Node anchorPane;

    @Autowired
    public ProjectSelectionController(ProjectService projectService) {
        this.projectService = projectService;
        this.projectFoundProperty = new SimpleBooleanProperty(this, "projectFound", false);
        this.titleProperty = new SimpleStringProperty(this, "titleProperty");
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
            this.projectFoundProperty.setValue(this.projectService.tryOpenProject(directory.toPath()));
        }
    }

    @Override
    public ReadOnlyStringProperty titleProperty() {
        return this.titleProperty;
    }

    @Override
    public ReadOnlyBooleanProperty closingProperty() {
        return this.projectFoundProperty;
    }
}
