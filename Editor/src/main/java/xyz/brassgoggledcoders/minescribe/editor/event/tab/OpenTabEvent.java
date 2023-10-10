package xyz.brassgoggledcoders.minescribe.editor.event.tab;

import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.editor.Application;

import java.io.IOException;
import java.util.UUID;
import java.util.function.BiConsumer;

public class OpenTabEvent<T> extends TabEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenTabEvent.class);

    public static final EventType<OpenTabEvent<?>> OPEN_TAB_EVENT_TYPE = new EventType<>(TAB_EVENT_TYPE, "open");

    //Note: This is from the xyz.brassgoggledcoders.minescribe.editor package
    private final String fxmlFile;
    private final String tabName;
    private final BiConsumer<T, String> setControllerData;

    public OpenTabEvent(String tabName, String fxmlFile, BiConsumer<T, String> setControllerData) {
        super(OPEN_TAB_EVENT_TYPE);
        this.tabName = tabName;
        this.fxmlFile = fxmlFile;
        this.setControllerData = setControllerData;
    }

    public String getTabName() {
        return this.tabName;
    }

    public AnchorPane createTabContent(UUID tabId) {
        try {
            FXMLLoader loader = new FXMLLoader(Application.class.getResource(fxmlFile + ".fxml"));
            AnchorPane anchorPane = loader.load();
            T controller = loader.getController();
            if (controller != null) {
                setControllerData.accept(controller, tabId.toString());
            }
            return anchorPane;
        } catch (IOException e) {
            LOGGER.error("Failed to Create Tab: {}", this.tabName, e);
            return null;
        }
    }
}