package xyz.brassgoggledcoders.minescribe.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.controller.dialog.ProjectSelectionController;
import xyz.brassgoggledcoders.minescribe.service.preferences.UserPreferencesService;
import xyz.brassgoggledcoders.minescribe.service.ui.DialogService;

@Component
@FxmlView("/xyz/brassgoggledcoders/minescribe/menu_bar.fxml")
public class MenuBarController {
    private final DialogService dialogService;
    private final UserPreferencesService userPreferencesService;

    @FXML
    private MenuBar menuBar;

    @Autowired
    public MenuBarController(DialogService dialogService, UserPreferencesService userPreferencesService) {
        this.dialogService = dialogService;
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
        this.dialogService.showDialogAndWait(
                ProjectSelectionController.class,
                this.menuBar.getScene()
                        .getWindow()
        );
    }
}
