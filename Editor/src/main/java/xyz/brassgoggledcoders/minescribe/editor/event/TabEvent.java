package xyz.brassgoggledcoders.minescribe.editor.event;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.editor.Application;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

public class TabEvent extends Event {
    public static final EventType<TabEvent> TAB_EVENT_TYPE = new EventType<>("tab");

    public TabEvent(EventType<? extends TabEvent> eventType) {
        super(eventType);
    }

    public static class OpenTabEvent<T> extends TabEvent {
        private static final Logger LOGGER = LoggerFactory.getLogger(OpenTabEvent.class);

        public static final EventType<OpenTabEvent<?>> OPEN_TAB_EVENT_TYPE = new EventType<>(TAB_EVENT_TYPE, "open");

        //Note: This is from the xyz.brassgoggledcoders.minescribe.editor package
        private final String fxmlFile;
        private final String tabName;
        private final Consumer<T> setControllerData;

        public OpenTabEvent(String tabName, String fxmlFile, Consumer<T> setControllerData) {
            super(OPEN_TAB_EVENT_TYPE);
            this.tabName = tabName;
            this.fxmlFile = fxmlFile;
            this.setControllerData = setControllerData;
        }

        public String getTabName() {
            return this.tabName;
        }

        public AnchorPane createTab() {
            try {
                FXMLLoader loader = new FXMLLoader(Application.class.getResource(fxmlFile + ".fxml"));
                AnchorPane anchorPane = loader.load();
                T controller = loader.getController();
                if (controller != null) {
                    setControllerData.accept(controller);
                }
                return anchorPane;
            } catch (IOException e) {
                LOGGER.error("Failed to Create Tab: {}", this.tabName, e);
                return null;
            }
        }
    }
}
