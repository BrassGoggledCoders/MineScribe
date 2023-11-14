package xyz.brassgoggledcoders.minescribe.editor.message;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class MessageHandler {
    private static final MessageHandler INSTANCE = new MessageHandler();

    private final ObservableList<MineScribeMessage> messages;
    private final FilteredList<MineScribeMessage> validMessages;

    public MessageHandler() {
        this.messages = FXCollections.observableArrayList(message -> new Observable[]{message.validProperty()});
        this.validMessages = this.messages.filtered(mineScribeMessage -> mineScribeMessage.validProperty()
                .getValue()
        );
    }

    public void addMessage(MineScribeMessage newMessage) {
        this.messages.add(newMessage);
        this.messages.removeIf(message -> !message.validProperty().get());
    }

    public ObservableList<MineScribeMessage> getMessages() {
        return this.validMessages;
    }

    public static MessageHandler getInstance() {
        return INSTANCE;
    }
}
