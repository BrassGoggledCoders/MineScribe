package xyz.brassgoggledcoders.minescribe.editor.event.field;

import javafx.event.EventType;

public class FieldAddedEvent extends FieldEvent {
    public static EventType<FieldAddedEvent> EVENT_TYPE = new EventType<>(FieldEvent.ROOT_TYPE, "added_field");

    public FieldAddedEvent(FieldInfo fieldInfo) {
        super(EVENT_TYPE, fieldInfo);
    }
}
