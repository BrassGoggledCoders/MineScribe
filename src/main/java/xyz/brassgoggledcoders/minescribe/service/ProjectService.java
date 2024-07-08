package xyz.brassgoggledcoders.minescribe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Alert;
import org.controlsfx.dialog.ExceptionDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import xyz.brassgoggledcoders.minescribe.event.ProjectOpenedEvent;
import xyz.brassgoggledcoders.minescribe.project.Project;
import xyz.brassgoggledcoders.minescribe.service.preferences.ApplicationPreferencesService;
import xyz.brassgoggledcoders.minescribe.util.SetupHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProjectService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);
    private static final ObjectMapper MAPPER = SetupHelper.setup(
            new ObjectMapper(),
            objectMapper -> objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
    );

    private final ApplicationContext applicationContext;
    private final ApplicationPreferencesService applicationPreferencesService;

    private ObjectProperty<Project> project;
    private ObjectProperty<Path> projectPath;

    @Autowired
    public ProjectService(ApplicationContext applicationContext, ApplicationPreferencesService applicationPreferencesService) {
        this.applicationContext = applicationContext;
        this.applicationPreferencesService = applicationPreferencesService;
    }

    public ObjectProperty<Path> projectPathProperty() {
        if (this.projectPath == null) {
            this.projectPath = new SimpleObjectProperty<>(
                    this,
                    "projectPath",
                    this.applicationPreferencesService.getApplicationPreferences()
                            .getLastProject()
            );
            this.projectPath.subscribe(newValue -> {
                if (newValue != null) {
                    this.projectProperty()
                            .setValue(this.tryLoadProject(newValue));
                } else {
                    this.projectProperty()
                            .setValue(null);
                }
            });
        }
        return this.projectPath;
    }

    public ObjectProperty<Project> projectProperty() {
        if (this.project == null) {
            this.project = new SimpleObjectProperty<>(this, "currentProject", null);
            this.project.subscribe(newValue -> {
                if (newValue != null) {
                    if (this.projectPath.getValue() == null) {
                        this.project.setValue(null);
                    } else {
                        this.applicationContext.publishEvent(new ProjectOpenedEvent(
                                newValue,
                                this.projectPath.getValue()
                        ));
                    }
                }
            });

            //Forces Initialization
            this.projectPathProperty();
        }

        return this.project;
    }

    public Project getProject() {
        return this.projectProperty()
                .getValue();
    }

    public void setProjectPath(Path path) {
        this.projectPathProperty()
                .setValue(path);
    }

    public Path getProjectPath() {
        return this.projectPathProperty()
                .getValue();
    }

    private Project tryLoadProject(Path path) {

        Project project = null;
        if (path != null) {
            project = Project.checkPath(path, false)
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

        Project newProject = new Project();
        try {
            MAPPER.writeValue(projectFilePath.toFile(), newProject);
        } catch (IOException e) {
            LOGGER.error("Failed to write new project", e);
        }
        return newProject;
    }

    public boolean tryOpenProject(Path projectPath) {
        Optional.of(Project.checkPath(projectPath, true))
                .map(checkedPath -> checkedPath.fold(
                        path -> {
                            Project newProject = this.createProject(path);
                            if (newProject != null) {
                                return path;
                            } else {
                                return null;
                            }
                        },
                        errorString -> {
                            new Alert(Alert.AlertType.ERROR, errorString)
                                    .showAndWait();
                            return null;
                        }
                ))
                .ifPresent(this::setProjectPath);

        return this.getProject() != null;
    }
}
