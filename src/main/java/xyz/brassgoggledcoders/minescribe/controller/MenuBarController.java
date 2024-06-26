package xyz.brassgoggledcoders.minescribe.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.service.UserPreferencesService;

@Component
@FxmlView("/xyz/brassgoggledcoders/minescribe/menu_bar.fxml")
public class MenuBarController {
    private final UserPreferencesService userPreferencesService;

    @Autowired
    public MenuBarController(UserPreferencesService userPreferencesService) {
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


}
