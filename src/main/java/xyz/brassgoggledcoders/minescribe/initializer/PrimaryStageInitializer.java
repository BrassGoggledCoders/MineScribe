package xyz.brassgoggledcoders.minescribe.initializer;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.controller.ApplicationController;
import xyz.brassgoggledcoders.minescribe.controller.OpenProjectController;
import xyz.brassgoggledcoders.minescribe.event.SceneReadyEvent;
import xyz.brassgoggledcoders.minescribe.event.StageReadyEvent;
import xyz.brassgoggledcoders.minescribe.project.Project;
import xyz.brassgoggledcoders.minescribe.service.ProjectService;
import xyz.brassgoggledcoders.minescribe.service.UserPreferencesService;

@Component
public class PrimaryStageInitializer implements ApplicationListener<StageReadyEvent> {
    private final ApplicationContext applicationContext;
    private final FxWeaver fxWeaver;
    private final ProjectService projectService;
    private final UserPreferencesService userPreferencesService;

    @Autowired
    public PrimaryStageInitializer(ApplicationContext applicationContext, FxWeaver fxWeaver,
                                   ProjectService projectService, UserPreferencesService userPreferencesService) {
        this.applicationContext = applicationContext;
        this.fxWeaver = fxWeaver;
        this.projectService = projectService;
        this.userPreferencesService = userPreferencesService;
    }

    @Override
    @Order(100)
    public void onApplicationEvent(@NotNull StageReadyEvent event) {
        this.userPreferencesService.loadPreferences();
        Project project = this.projectService.getProject();

        if (project == null) {
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Open New MineScribe Project");

            FxControllerAndView<OpenProjectController, AnchorPane> openProjectView = fxWeaver.load(
                    OpenProjectController.class
            );
            Scene scene = new Scene(openProjectView.getView()
                    .orElseThrow()
            );
            dialogStage.setScene(scene);

            this.applicationContext.publishEvent(new SceneReadyEvent(scene));

            openProjectView.getController()
                    .openedProjectProperty()
                    .addListener((observableValue, oldValue, newValue) -> {
                        if (newValue) {
                            dialogStage.hide();
                        }
                    });
            dialogStage.showAndWait();
        }

        project = this.projectService.getProject();

        if (project != null) {
            FxControllerAndView<ApplicationController, AnchorPane> applicationView = fxWeaver.load(ApplicationController.class);
            Scene scene = new Scene(applicationView.getView()
                    .orElseThrow()
            );
            event.getStage()
                    .setScene(scene);

            applicationContext.publishEvent(new SceneReadyEvent(scene));

            event.getStage()
                    .show();
        } else {
            Platform.exit();
        }
    }
}
