package xyz.brassgoggledcoders.minescribe.editor.event.tab;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.editor.Application;

import java.io.IOException;
import java.util.function.Consumer;

public class TabEvent extends Event {
    public static final EventType<TabEvent> TAB_EVENT_TYPE = new EventType<>("tab");

    public TabEvent(EventType<? extends TabEvent> eventType) {
        super(eventType);
    }
}
