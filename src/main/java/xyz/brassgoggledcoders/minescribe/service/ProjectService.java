package xyz.brassgoggledcoders.minescribe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Alert;
import org.controlsfx.dialog.ExceptionDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import xyz.brassgoggledcoders.minescribe.project.Project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProjectService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final ApplicationPreferencesService applicationPreferencesService;

    private ObjectProperty<Project> project;

    @Autowired
    public ProjectService(ApplicationPreferencesService applicationPreferencesService) {
        this.applicationPreferencesService = applicationPreferencesService;
    }

    public ObjectProperty<Project> projectProperty() {
        if (this.project == null) {
            this.project = new SimpleObjectProperty<>(this, "currentProject", tryLoadProject());
        }

        return this.project;
    }

    public Project getProject() {
        return this.projectProperty()
                .getValue();
    }

    private Project tryLoadProject() {
        Path lastProjectPath = this.applicationPreferencesService.getApplicationPreferences()
                .getLastProject();

        Project project = null;
        if (lastProjectPath != null) {
            project = Project.checkPath(lastProjectPath, false)
                    .fold(
                            this::createProject,
                            errorString -> {
                                LOGGER.error(errorString);
                                return null;
                            }
                    );
        }

        return project;
    }

    public Project createProject(Path path) {
        Path projectFilePath = path.resolve("minescribe_project.json");
        if (Files.exists(projectFilePath)) {
            try {
                return MAPPER.readValue(projectFilePath.toFile(), Project.class);
            } catch (IOException e) {
                LOGGER.error("Failed to load existing project", e);
                ExceptionDialog exceptionDialog = new ExceptionDialog(e);
                exceptionDialog.setTitle("Failed to load existing project");
                exceptionDialog.showAndWait();
                return null;
            }
        }

        Project newProject = new Project(path);
        try {
            MAPPER.writeValue(projectFilePath.toFile(), newProject);
        } catch (IOException e) {
            LOGGER.error("Failed to write new project", e);
        }
        return newProject;
    }

    public boolean tryOpenProject(Path projectPath) {
        Optional.of(Project.checkPath(projectPath, true))
                .map(path -> path.fold(
                        this::createProject,
                        errorString -> {
                            new Alert(Alert.AlertType.ERROR, errorString)
                                    .showAndWait();
                            return null;
                        }
                ))
                .ifPresent(this::setProject);

        return this.projectProperty().getValue() != null;
    }

    private void setProject(Project project) {
        this.projectProperty()
                .setValue(project);

    }
}
