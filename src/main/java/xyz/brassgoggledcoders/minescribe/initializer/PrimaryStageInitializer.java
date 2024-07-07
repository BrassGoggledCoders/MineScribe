package xyz.brassgoggledcoders.minescribe.initializer;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.controller.ApplicationController;
import xyz.brassgoggledcoders.minescribe.controller.dialog.ProjectSelectionController;
import xyz.brassgoggledcoders.minescribe.event.ApplicationReadyEvent;
import xyz.brassgoggledcoders.minescribe.event.SceneReadyEvent;
import xyz.brassgoggledcoders.minescribe.project.Project;
import xyz.brassgoggledcoders.minescribe.service.LocalizationService;
import xyz.brassgoggledcoders.minescribe.service.ProjectService;
import xyz.brassgoggledcoders.minescribe.service.preferences.UserPreferencesService;
import xyz.brassgoggledcoders.minescribe.service.ui.DialogService;

@Component
public class PrimaryStageInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final ApplicationContext applicationContext;
    private final FxWeaver fxWeaver;
    private final ProjectService projectService;
    private final UserPreferencesService userPreferencesService;
    private final LocalizationService localizationService;
    private final DialogService dialogService;

    @Autowired
    public PrimaryStageInitializer(ApplicationContext applicationContext, FxWeaver fxWeaver,
                                   ProjectService projectService, UserPreferencesService userPreferencesService,
                                   LocalizationService localizationService, DialogService dialogService) {
        this.applicationContext = applicationContext;
        this.fxWeaver = fxWeaver;
        this.projectService = projectService;
        this.userPreferencesService = userPreferencesService;
        this.localizationService = localizationService;
        this.dialogService = dialogService;
    }

    @Override
    @Order(100)
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        this.userPreferencesService.loadPreferences();
        Project project = this.projectService.getProject();

        if (project == null) {
            this.dialogService.showDialogAndWait(
                    ProjectSelectionController.class,
                    event.getStage()
                            .getOwner()
            );
        }

        project = this.projectService.getProject();

        if (project != null) {
            FxControllerAndView<ApplicationController, AnchorPane> applicationView = fxWeaver.load(
                    ApplicationController.class,
                    localizationService.getResourceBundle()
            );
            Scene scene = new Scene(applicationView.getView()
                    .orElseThrow()
            );
            event.getStage()
                    .setScene(scene);

            event.getStage()
                    .setTitle(this.localizationService.getString("application.minescribe.title"));

            applicationContext.publishEvent(new SceneReadyEvent(scene));

            event.getStage()
                    .show();
        } else {
            Platform.exit();
        }
    }
}
