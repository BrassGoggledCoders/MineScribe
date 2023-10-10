package xyz.brassgoggledcoders.minescribe.editor.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import xyz.brassgoggledcoders.minescribe.editor.event.NetworkEvent;
import xyz.brassgoggledcoders.minescribe.editor.event.page.RequestPageEvent;
import xyz.brassgoggledcoders.minescribe.editor.registry.PackContentTypeRegistry;

import java.util.concurrent.CompletableFuture;

public class LoadingController {
    @FXML
    public StackPane loading;

    @FXML
    public Text loadingStatus;

    @FXML
    public void initialize() {
        this.loading.addEventHandler(
                NetworkEvent.ClientConnectionNetworkEvent.CLIENT_CONNECTED_EVENT_TYPE,
                event -> {
                    event.consume();
                    if (event.getStatus() == NetworkEvent.ConnectionStatus.CONNECTED) {
                        this.loadingStatus.setText("Awaiting Loading of Resources");
                        CompletableFuture.allOf(
                                PackContentTypeRegistry.getInstance().getPackContentTypesLoaded(),
                                PackContentTypeRegistry.getInstance().getPackContentSubTypesLoaded()
                        ).thenAccept(unused -> Platform.runLater(() -> {
                            this.loadingStatus.setText("Loading Complete. Opening Editor");
                            this.loadingStatus.fireEvent(new RequestPageEvent("editor"));
                        }));
                    }
                }
        );
    }
}
