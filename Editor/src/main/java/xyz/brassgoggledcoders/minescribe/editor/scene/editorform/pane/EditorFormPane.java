package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.beans.property.*;
import javafx.geometry.Insets;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class EditorFormPane extends VBox {
    private static final Logger LOGGER = LoggerFactory.getLogger(EditorFormPane.class);
    private final ReadOnlyObjectProperty<FileForm> primaryForm;
    private final ObjectProperty<FileForm> serializerForm;
    private final ObjectProperty<JsonObject> persistedObject;

    private final BooleanProperty valid;
    private final BooleanProperty changed;
    private final BooleanProperty persistable;

    public EditorFormPane(FileForm primaryForm, List<EditorFieldPane<?>> fieldPanes, @Nullable JsonObject persistedObject) {
        this.primaryForm = new SimpleObjectProperty<>(primaryForm);
        this.serializerForm = new SimpleObjectProperty<>();
        this.persistedObject = new SimpleObjectProperty<>(persistedObject);
        this.valid = new SimpleBooleanProperty(true);
        this.changed = new SimpleBooleanProperty(false);
        this.persistable = new SimpleBooleanProperty(false);

        this.serializerForm.addListener((observable, oldValue, newValue) -> reloadSerializerForm(oldValue, newValue));
        fieldPanes.forEach(this::addFieldPanel);

        updateValidProperty();
        updateChangedProperty();

        this.getStyleClass().add("borders");
        this.setPadding(new Insets(10));
    }

    public void setPersistedObject(@NotNull JsonObject jsonObject) {
        this.persistedObject.set(jsonObject);
        List<SerializerEditorFieldPane> serializerEditorFieldPanes = this.getEditorFieldPanes()
                .filter(SerializerEditorFieldPane.class::isInstance)
                .map(SerializerEditorFieldPane.class::cast)
                .toList();

        //These can alter the list
        for (SerializerEditorFieldPane serializerEditorFieldPane : serializerEditorFieldPanes) {
            setField(serializerEditorFieldPane, jsonObject);
        }

        for (EditorFieldPane<?> editorFieldPane : this.getEditorFieldPanes().toList()) {
            setField(editorFieldPane, jsonObject);
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
        this.getEditorFieldPanes()
                .forEach(EditorFieldPane::reset);
    }

    public void persist() {
        if (this.valid.get()) {
            JsonObject newPersisted = new JsonObject();
            this.getEditorFieldPanes().forEach(editorFieldPane -> {
                editorFieldPane.persist();
                JsonElement jsonElement = editorFieldPane.getValue();
                if (jsonElement != null) {
                    newPersisted.add(editorFieldPane.getFieldName(), jsonElement);
                }
            });

            if (!newPersisted.isEmpty()) {
                this.persistedObject.set(newPersisted);
            }
        }
    }

    //Intellij complains otherwise?
    @SuppressWarnings("RedundantTypeArguments")
    private Stream<EditorFieldPane<?>> getEditorFieldPanes() {
        return this.getChildren()
                .stream()
                .filter(EditorFieldPane.class::isInstance)
                .<EditorFieldPane<?>>map(EditorFieldPane.class::cast);
    }

    private void addFieldPanel(EditorFieldPane<?> newField) {
        newField.setFormPane(this);
        newField.validProperty()
                .addListener((observable, oldValue, newValue) -> updateValidProperty());
        newField.changedProperty()
                .addListener((observable, oldValue, newValue) -> updateChangedProperty());

        if (this.persistedObjectProperty().get() != null) {
            JsonObject currentObject = this.persistedObjectProperty().get();
            setField(newField, currentObject);
        }

        List<Node> children = new ArrayList<>(this.getChildren());
        if (children.contains(newField)) {
            LOGGER.error("Editor Form already contains: " + newField);
        } else {
            children.add(newField);
            children.sort(this::sortChildren);
            this.getChildren().setAll(children);
        }

    }

    private int sortChildren(Node nodeA, Node nodeB) {
        int compared = 0;
        if (nodeA instanceof EditorFieldPane<?> editorFieldPaneOne &&
                nodeB instanceof EditorFieldPane<?> editorFieldPaneTwo) {
            boolean paneOneMain = editorFieldPaneOne.getFileForm() == this.primaryForm.get();
            boolean paneTwoMain = editorFieldPaneTwo.getFileForm() == this.primaryForm.get();

            compared = -Boolean.compare(paneOneMain, paneTwoMain);

            if (compared == 0) {
                compared = Integer.compare(editorFieldPaneOne.getSortOrder(), editorFieldPaneTwo.getSortOrder());
            }
        }
        return compared;
    }

    private void updateValidProperty() {
        valid.setValue(this.getEditorFieldPanes()
                .allMatch(EditorFieldPane::isValid)
        );
        updatePersistableProperty();
    }

    private void updateChangedProperty() {
        changed.setValue(this.getEditorFieldPanes()
                .anyMatch(EditorFieldPane::isChanged)
        );
        updatePersistableProperty();
    }

    private void updatePersistableProperty() {
        boolean canPersist = this.getEditorFieldPanes()
                .anyMatch(EditorFieldPane::isChanged);
        if (canPersist) {
            canPersist = this.getEditorFieldPanes()
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
                    this.addFieldPanel(EditorFileFieldPane.of(newValue, field));
                } catch (FormException e) {
                    LOGGER.error(e.getMessage(), e);
                    e.showErrorDialog();
                }
            }
        }
        updateValidProperty();
        updateChangedProperty();
    }

    private void setField(EditorFieldPane<?> editorFieldPane, JsonObject jsonObject) {
        if (jsonObject.has(editorFieldPane.getFieldName())) {
            editorFieldPane.setValue(jsonObject.get(editorFieldPane.getFieldName()));
            editorFieldPane.persist();
            editorFieldPane.reset();
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
