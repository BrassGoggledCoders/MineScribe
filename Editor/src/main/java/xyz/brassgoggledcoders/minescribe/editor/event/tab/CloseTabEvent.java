package xyz.brassgoggledcoders.minescribe.editor.event.tab;

import javafx.event.EventType;

public class CloseTabEvent extends TabEvent {
    public static final EventType<CloseTabEvent> EVENT_TYPE = new EventType<>(TAB_EVENT_TYPE, "close");

    private final String id;
    public CloseTabEvent(String id) {
        super(EVENT_TYPE);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
