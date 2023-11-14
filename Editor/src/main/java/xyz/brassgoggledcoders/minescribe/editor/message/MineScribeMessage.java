package xyz.brassgoggledcoders.minescribe.editor.message;

import javafx.beans.property.*;

import java.nio.file.Path;

public class MineScribeMessage {
    private final ObjectProperty<MessageType> type;
    private final ObjectProperty<Path> filePath;
    private final StringProperty field;
    private final StringProperty message;
    private final BooleanProperty valid;

    public MineScribeMessage(MessageType type, Path filePath, String field, String message) {
        this.type = new SimpleObjectProperty<>(type);
        this.filePath = new SimpleObjectProperty<>(filePath);
        this.field = new SimpleStringProperty(field);
        this.message = new SimpleStringProperty(message);
        this.valid = new SimpleBooleanProperty(true);
    }

    public BooleanProperty validProperty() {
        return this.valid;
    }

    public ObjectProperty<Path> filePathProperty() {
        return this.filePath;
    }

    public ObjectProperty<MessageType> messageTypeProperty() {
        return this.type;
    }

    public StringProperty fieldProperty() {
        return this.field;
    }

    public StringProperty messageProperty() {
        return this.message;
    }
}
