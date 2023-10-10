package xyz.brassgoggledcoders.minescribe.editor.event.page;

import javafx.event.Event;
import javafx.event.EventType;

public class PageEvent extends Event {
    public static final EventType<PageEvent> EVENT_TYPE = new EventType<>("page");

    public PageEvent(EventType<? extends PageEvent> eventType) {
        super(eventType);
    }
}
