package xyz.brassgoggledcoders.minescribe.editor.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.editor.Application;
import xyz.brassgoggledcoders.minescribe.editor.event.NetworkEvent;
import xyz.brassgoggledcoders.minescribe.editor.event.NetworkEvent.ClientConnectionNetworkEvent;
import xyz.brassgoggledcoders.minescribe.editor.event.page.RequestPageEvent;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;

import java.io.IOException;
import java.net.URL;

public class ApplicationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);
    @FXML
    public AnchorPane content;

    @FXML
    public void initialize() {
        FileHandler.initialize();

        this.content.addEventHandler(
                ClientConnectionNetworkEvent.CLIENT_CONNECTED_EVENT_TYPE,
                event -> {
                    if (event.getStatus() == NetworkEvent.ConnectionStatus.DISCONNECTED) {
                        this.content.fireEvent(new RequestPageEvent("loading"));
                    }
                    if (event.getSource().equals(this.content)) {
                        this.content.getChildren()
                                .forEach(node -> node.fireEvent(event));
                    }
                }
        );

        this.content.addEventHandler(
                RequestPageEvent.REQUEST_PAGE_EVENT_TYPE,
                event -> trySetView(event.getPageName())
        );

        this.content.fireEvent(new RequestPageEvent("loading"));
    }

    public void trySetView(String name) {
        if (content.getChildren().stream().noneMatch(node -> node.getId().equals(name))) {
            URL url = Application.class.getResource(name + ".fxml");
            if (url == null) {
                LOGGER.error("Failed to find resource for: {}", name);
            } else {
                Platform.runLater(() -> {
                    try {
                        Node node = FXMLLoader.load(url);
                        content.getChildren().clear();
                        AnchorPane.setTopAnchor(node, 0D);
                        AnchorPane.setBottomAnchor(node, 0D);
                        AnchorPane.setLeftAnchor(node, 0D);
                        AnchorPane.setRightAnchor(node, 0D);
                        this.content.getChildren().add(node);
                    } catch (IOException e) {
                        LOGGER.error("Failed to load FXML for: %s".formatted(name), e);
                    }
                });
            }
        }
    }
}