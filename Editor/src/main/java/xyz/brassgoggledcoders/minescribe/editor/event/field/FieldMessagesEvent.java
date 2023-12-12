package xyz.brassgoggledcoders.minescribe.editor.event.field;

import javafx.event.EventType;
import xyz.brassgoggledcoders.minescribe.editor.message.FieldMessage;

import java.util.Collection;
import java.util.Set;

public class FieldMessagesEvent extends FieldEvent {
    public static final EventType<FieldMessagesEvent> EVENT_TYPE = new EventType<>(ROOT_TYPE, "messages_field");

    private final Set<FieldMessage> addedMessages;
    private final Set<FieldMessage> removedMessages;
    private final Set<FieldMessage> allMessages;

    public FieldMessagesEvent(FieldInfo fieldInfo, Set<FieldMessage> addedMessages, Set<FieldMessage> removedMessages, Set<FieldMessage> allMessages) {
        super(EVENT_TYPE, fieldInfo);
        this.addedMessages = addedMessages;
        this.removedMessages = removedMessages;
        this.allMessages = allMessages;
    }

    public Collection<FieldMessage> getAddedMessages() {
        return addedMessages;
    }

    public Collection<FieldMessage> getRemovedMessages() {
        return removedMessages;
    }

    public Set<FieldMessage> getAllMessages() {
        return allMessages;
    }
}
