package xyz.brassgoggledcoders.minescribe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Alert;
import org.controlsfx.dialog.ExceptionDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import xyz.brassgoggledcoders.minescribe.event.ProjectOpenedEvent;
import xyz.brassgoggledcoders.minescribe.event.SavePreferencesEvent;
import xyz.brassgoggledcoders.minescribe.project.Project;
import xyz.brassgoggledcoders.minescribe.service.preferences.ApplicationPreferencesService;
import xyz.brassgoggledcoders.minescribe.util.SetupHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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

    private final ObjectProperty<Project> project;
    private final ObjectProperty<Path> projectPath;

    private final BooleanProperty projectDirty;

    @Autowired
    public ProjectService(ApplicationContext applicationContext, ApplicationPreferencesService applicationPreferencesService) {
        this.applicationContext = applicationContext;
        this.applicationPreferencesService = applicationPreferencesService;

        this.projectPath = new SimpleObjectProperty<>(
                this,
                "projectPath",
                this.applicationPreferencesService.getApplicationPreferences()
                        .getLastProject()
        );

        this.project = new SimpleObjectProperty<>(
                this,
                "project"
        );

        this.projectPath.addListener((observable, oldValue, newValue) -> this.projectPathUpdate(newValue));
        this.project.addListener((observable, oldValue, newValue) -> this.projectListeners(newValue));

        this.projectDirty = new SimpleBooleanProperty(this, "projectDirty", false);
    }

    public void fireProjectOpened() {
        if (this.project.getValue() != null) {
            this.applicationContext.publishEvent(new ProjectOpenedEvent(
                    this.project.getValue(),
                    this.projectPath.getValue()
            ));
        }
    }

    public void importedPacks(List<Path> packPaths) {
        Project currentProject = this.project.get();
        if (currentProject != null) {
            currentProject.importedPacksProperty()
                    .addAll(packPaths.stream()
                            .map(packPath -> {
                                if (packPath.startsWith(this.projectPath.getValue())) {
                                    packPath = this.projectPath.getValue()
                                            .relativize(packPath);
                                }
                                return packPath.toString();
                            })
                            .toList()
                    );
        }
    }

    private void projectListeners(Project newProject) {
        if (newProject != null) {
            newProject.importedPacksProperty()
                    .addListener((observable, oldValue, newValue) -> this.projectDirty.set(true));
            newProject.importedPacksProperty()
                    .addListener((ListChangeListener<? super String>) change -> this.projectDirty.set(true));
        }
    }

    public ObjectProperty<Path> projectPathProperty() {
        return this.projectPath;
    }

    public ReadOnlyObjectProperty<Project> projectProperty() {
        return this.project;
    }

    private void projectPathUpdate(Path newPath) {
        this.project.setValue(this.tryLoadProject(newPath));
        this.fireProjectOpened();
    }

    public Project getProject() {
        Project theProject = this.projectProperty()
                .getValue();
        if (theProject == null) {
            Path theProjectPath = this.getProjectPath();
            if (theProjectPath != null) {
                this.projectPathUpdate(theProjectPath);
            }
        }
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

    @EventListener(SavePreferencesEvent.class)
    private void handleSave(SavePreferencesEvent ignoredEvent) {
        if (this.projectDirty.getValue()) {
            if (this.project.getValue() != null) {
                Path filePath = this.projectPath.getValue()
                        .resolve("minescribe_project.json");
                try {
                    MAPPER.writeValue(filePath.toFile(), this.project.getValue());
                    this.projectDirty.setValue(false);
                } catch (IOException e) {
                    LOGGER.error("Failed to write project updates", e);
                }
            }
        }
    }
}
