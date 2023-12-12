package xyz.brassgoggledcoders.minescribe.editor.message;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.brassgoggledcoders.minescribe.core.info.InfoRepository;
import xyz.brassgoggledcoders.minescribe.editor.project.Project;

import java.nio.file.Path;
import java.util.Objects;

public class MineScribeMessage {
    private final ObjectProperty<MessageType> type;
    private final ObjectProperty<Path> filePath;
    private final ObservableValue<Path> relativePath;
    private final StringProperty field;
    private final StringProperty message;
    private final BooleanProperty valid;
    private final Object context;

    public MineScribeMessage(@NotNull MessageType type, @Nullable Path filePath, @Nullable String field, @NotNull String message, Object context) {
        this.type = new SimpleObjectProperty<>(type);
        this.filePath = new SimpleObjectProperty<>(filePath);
        this.context = context;
        this.relativePath = this.filePath.map(fullPath -> InfoRepository.getInstance()
                .getValue(Project.KEY)
                .getRootPath()
                .relativize(fullPath)
        );
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

    public ObservableValue<Path> relativeFilePath() {
        return this.relativePath;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MineScribeMessage message1 = (MineScribeMessage) o;
        return Objects.equals(type, message1.type) && Objects.equals(filePath, message1.filePath) &&
                Objects.equals(relativePath, message1.relativePath) && Objects.equals(field, message1.field) &&
                Objects.equals(message, message1.message) && Objects.equals(valid, message1.valid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, filePath, relativePath, field, message, valid);
    }

    public Object getContext() {
        return context;
    }
}
