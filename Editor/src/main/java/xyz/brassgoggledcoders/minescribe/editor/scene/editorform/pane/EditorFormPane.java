package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.packinfo.SerializerType;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class EditorFormPane extends VBox {
    private static final Logger LOGGER = LoggerFactory.getLogger(EditorFormPane.class);
    private final ReadOnlyObjectProperty<FileForm> primaryForm;
    private final ObjectProperty<FileForm> serializerForm;
    private final ObjectProperty<JsonObject> persistedObject;
    private final ListProperty<EditorFieldPane<?>> editorFieldPanes;

    private final BooleanProperty valid;
    private final BooleanProperty changed;
    private final BooleanProperty persistable;

    public EditorFormPane(FileForm primaryForm, List<EditorFieldPane<?>> fieldPanes, @Nullable JsonObject persistedObject) {
        this.primaryForm = new SimpleObjectProperty<>(primaryForm);
        this.serializerForm = new SimpleObjectProperty<>();
        this.persistedObject = new SimpleObjectProperty<>(persistedObject);
        this.editorFieldPanes = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.valid = new SimpleBooleanProperty(true);
        this.changed = new SimpleBooleanProperty(false);
        this.persistable = new SimpleBooleanProperty(false);

        this.serializerForm.addListener((observable, oldValue, newValue) -> reloadSerializerForm(oldValue, newValue));
        this.editorFieldPanes.addListener(this::editorFieldChanged);

        this.editorFieldPanes.get()
                .addAll(fieldPanes);

        updateValidProperty();
        updateChangedProperty();
    }

    public void setPersistedObject(@NotNull JsonObject jsonObject) {
        this.persistedObject.set(jsonObject);
        List<SerializerEditorFieldPane> serializerEditorFieldPanes = this.editorFieldPanes.stream()
                .filter(SerializerEditorFieldPane.class::isInstance)
                .map(SerializerEditorFieldPane.class::cast)
                .toList();

        //These can alter the list
        for (SerializerEditorFieldPane serializerEditorFieldPane : serializerEditorFieldPanes) {
            if (jsonObject.has(serializerEditorFieldPane.getFieldName())) {
                serializerEditorFieldPane.setValue(jsonObject.get(serializerEditorFieldPane.getFieldName()));
            }
        }

        for (EditorFieldPane<?> editorFieldPane : this.editorFieldPanes) {
            if (jsonObject.has(editorFieldPane.getFieldName())) {
                editorFieldPane.setValue(jsonObject.get(editorFieldPane.getFieldName()));
            }
        }
    }

    @SuppressWarnings("unused")
    public ReadOnlyObjectProperty<FileForm> primaryFormProperty() {
        return this.primaryForm;
    }

    public ObjectProperty<FileForm> serializerFormProperty() {
        return this.serializerForm;
    }

    public ReadOnlyObjectProperty<JsonObject> persistedObjectProperty() {
        return this.persistedObject;
    }

    public ReadOnlyBooleanProperty changedProperty() {
        return this.changed;
    }

    public ReadOnlyBooleanProperty validProperty() {
        return this.valid;
    }

    public void reset() {
        this.editorFieldPanes.forEach(EditorFieldPane::reset);
    }

    public void persist() {
        if (this.valid.get() && this.persistable.get()) {
            JsonObject newPersisted = new JsonObject();
            for (EditorFieldPane<?> editorFieldPane : this.editorFieldPanes) {
                editorFieldPane.persist();
                JsonElement jsonElement = editorFieldPane.getValue();
                if (jsonElement != null) {
                    newPersisted.add(editorFieldPane.getFieldName(), jsonElement);
                }
            }
            if (!newPersisted.isEmpty()) {
                this.persistedObject.set(newPersisted);
            }
        }
    }

    private void editorFieldChanged(Change<? extends EditorFieldPane<?>> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                for (EditorFieldPane<?> removed : change.getRemoved()) {
                    this.getChildren().remove(removed);
                }
            } else if (change.wasAdded()) {
                for (EditorFieldPane<?> added : change.getAddedSubList()) {
                    added.setFormPane(this);
                    added.validProperty()
                            .addListener((observable, oldValue, newValue) -> updateValidProperty());
                    added.changedProperty()
                            .addListener((observable, oldValue, newValue) -> updateChangedProperty());

                    if (this.persistedObjectProperty().get() != null) {
                        JsonObject currentObject = this.persistedObjectProperty().get();
                        if (currentObject.has(added.getFieldName())) {
                            added.setValue(currentObject.get(added.getFieldName()));
                        }
                    }

                    List<Node> children = this.getChildren();
                    if (children.contains(added)) {
                        LOGGER.error("Re-added Element: " + added);
                    } else {
                        children.add(added);
                    }
                }
            }
        }
    }

    private void updateValidProperty() {
        valid.setValue(this.editorFieldPanes.stream().allMatch(EditorFieldPane::isValid));
        updatePersistableProperty();
    }

    private void updateChangedProperty() {
        changed.setValue(this.editorFieldPanes.stream().anyMatch(EditorFieldPane::isChanged));
        updatePersistableProperty();
    }

    private void updatePersistableProperty() {
        boolean canPersist = this.editorFieldPanes.stream()
                .anyMatch(EditorFieldPane::isChanged);
        if (canPersist) {
            canPersist = this.editorFieldPanes.stream()
                    .allMatch(EditorFieldPane::isValid);
        }
        persistable.set(canPersist);
    }

    private void reloadSerializerForm(FileForm oldValue, FileForm newValue) {
        this.getChildren().removeIf(child -> {
            if (child instanceof EditorFieldPane<?> editorFieldPane) {
                return editorFieldPane.getFileForm() == oldValue;
            }

            return false;
        });

        if (newValue != null) {
            for (FileField<?> field : newValue.getFields()) {
                try {
                    this.editorFieldPanes.add(EditorFileFieldPane.of(newValue, field));
                } catch (FormException e) {
                    LOGGER.error(e.getMessage(), e);
                    e.showErrorDialog();
                }
            }
        }
    }

    public static EditorFormPane of(FileForm form, Supplier<List<SerializerType>> gatherTypes,
                                    @Nullable JsonObject persistedObject) throws FormException {
        List<EditorFieldPane<?>> editorFieldPanes = new LinkedList<>();

        for (FileField<?> field : form.getFields()) {
            editorFieldPanes.add(EditorFileFieldPane.of(form, field));
        }

        SerializerEditorFieldPane.of(form, gatherTypes)
                .ifPresent(editorFieldPanes::add);

        return new EditorFormPane(form, editorFieldPanes, persistedObject);
    }
}
