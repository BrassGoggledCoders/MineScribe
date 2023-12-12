package xyz.brassgoggledcoders.minescribe.editor.event.field;

import javafx.event.Event;
import javafx.event.EventType;

public class FieldEvent extends Event {
    public static final EventType<FieldEvent> ROOT_TYPE = new EventType<>("field");
    private final FieldInfo fieldInfo;

    public FieldEvent(EventType<? extends Event> eventType, FieldInfo fieldInfo) {
        super(eventType);
        this.fieldInfo = fieldInfo;
    }

    public FieldInfo getFieldInfo() {
        return fieldInfo;
    }
}
