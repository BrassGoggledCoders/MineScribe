package xyz.brassgoggledcoders.minescribe.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.scene.control.toolwindow.ToolWindowPane;
import xyz.brassgoggledcoders.minescribe.service.ToolWindowPreferencesService;

@Component
@FxmlView("/xyz/brassgoggledcoders/minescribe/application.fxml")
public class ApplicationController {
    private final ToolWindowPreferencesService toolWindowPreferencesService;

    @FXML
    private ToolWindowPane toolWindowPane;

    @FXML
    private AnchorPane application;

    public ApplicationController(ToolWindowPreferencesService toolWindowPreferencesService) {
        this.toolWindowPreferencesService = toolWindowPreferencesService;
    }

    @FXML
    public void initialize() {
        this.toolWindowPane.setToolWindowInfoHandler(toolWindowPreferencesService);
    }
}
