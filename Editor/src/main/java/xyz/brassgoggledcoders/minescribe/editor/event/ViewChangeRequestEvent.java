package xyz.brassgoggledcoders.minescribe.editor.event;

import javafx.event.Event;
import javafx.event.EventType;

public class ViewChangeRequestEvent extends Event {
    public static final EventType<ViewChangeRequestEvent> EVENT_TYPE = new EventType<>("view_change_request");

    private final String newView;
    public ViewChangeRequestEvent(String newView) {
        super(EVENT_TYPE);
        this.newView = newView;
    }

    public String getNewView() {
        return newView;
    }
}
