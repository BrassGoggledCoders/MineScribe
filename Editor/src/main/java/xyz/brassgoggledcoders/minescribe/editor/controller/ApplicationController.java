package xyz.brassgoggledcoders.minescribe.editor.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.MineScribeInfo;
import xyz.brassgoggledcoders.minescribe.core.info.InfoKeys;
import xyz.brassgoggledcoders.minescribe.core.info.InfoRepository;
import xyz.brassgoggledcoders.minescribe.core.netty.PacketRegistry;
import xyz.brassgoggledcoders.minescribe.core.netty.packet.InstanceDataResponse;
import xyz.brassgoggledcoders.minescribe.editor.Application;
import xyz.brassgoggledcoders.minescribe.editor.event.NetworkEvent;
import xyz.brassgoggledcoders.minescribe.editor.event.NetworkEvent.ClientConnectionNetworkEvent;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.server.MineScribeNettyServer;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;

public class ApplicationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);
    @FXML
    public AnchorPane content;

    @FXML
    public void initialize() {
        MineScribeNettyServer.initialize(this.content::fireEvent, MineScribeInfo.DEFAULT_PORT);
        FileHandler.initialize();

        PacketRegistry.INSTANCE.addPacketHandler(
                InstanceDataResponse.class,
                instanceDataResponse -> {
                    InfoRepository.getInstance().setValue(InfoKeys.RESOURCE_PACK_VERSION, instanceDataResponse.resourcePackVersion());
                    InfoRepository.getInstance().setValue(InfoKeys.DATA_PACK_VERSION, instanceDataResponse.dataPackVersion());
                    for (Map.Entry<String, Path> packEntries: instanceDataResponse.packFolders().entrySet()) {
                        FileHandler.getInstance()
                                .addPackDirectory(packEntries.getKey(), packEntries.getValue());
                    }
                }
        );

        this.content.addEventHandler(
                ClientConnectionNetworkEvent.CLIENT_CONNECTED_EVENT_TYPE,
                event -> {
                    if (event.getStatus() == NetworkEvent.ConnectionStatus.CONNECTED) {
                        trySetView("editor");
                    } else {
                        trySetView("loading");
                    }
                }
        );
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