package xyz.brassgoggledcoders.minescribe.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.controller.dialog.ProjectSelectionController;
import xyz.brassgoggledcoders.minescribe.fxweaver.FxStageWeaver;
import xyz.brassgoggledcoders.minescribe.service.UserPreferencesService;

@Component
@FxmlView("/xyz/brassgoggledcoders/minescribe/menu_bar.fxml")
public class MenuBarController {
    private final FxStageWeaver fxWeaver;
    private final UserPreferencesService userPreferencesService;

    @Autowired
    public MenuBarController(FxStageWeaver fxWeaver, UserPreferencesService userPreferencesService) {
        this.fxWeaver = fxWeaver;
        this.userPreferencesService = userPreferencesService;
    }

    @FXML
    private void openSettings() {
        this.userPreferencesService.openSettings();
    }

    @FXML
    private void exit() {
        Platform.exit();
    }

    @FXML
    private void openProject() {
        this.fxWeaver.loadStage(ProjectSelectionController.class)
                .stage()
                .ifPresent(Stage::showAndWait);
    }
}
