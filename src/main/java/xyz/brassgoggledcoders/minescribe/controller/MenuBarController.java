package xyz.brassgoggledcoders.minescribe.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import xyz.brassgoggledcoders.minescribe.preferences.MineScribePreferences;

public class MenuBarController {

    public MenuBarController() {
    }

    @FXML
    private void openSettings() {
        MineScribePreferences.openSettings();
    }

    @FXML
    private void exit() {
        Platform.exit();
    }


}
