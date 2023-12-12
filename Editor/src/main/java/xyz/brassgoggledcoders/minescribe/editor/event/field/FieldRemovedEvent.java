package xyz.brassgoggledcoders.minescribe.editor.event.field;

import javafx.event.EventType;

public class FieldRemovedEvent extends FieldEvent {
    public static EventType<FieldRemovedEvent> EVENT_TYPE = new EventType<>(FieldEvent.ROOT_TYPE, "removed_field");

    public FieldRemovedEvent(FieldInfo fieldInfo) {
        super(EVENT_TYPE, fieldInfo);
    }
}
