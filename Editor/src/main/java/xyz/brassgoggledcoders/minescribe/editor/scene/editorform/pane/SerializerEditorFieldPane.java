package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane;

import com.dlsc.formsfx.model.structure.SingleSelectionField;
import com.dlsc.formsfx.view.controls.SimpleControl;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.SerializerInfo;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.packinfo.SerializerType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.SceneUtils;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.CellFactoryComboBoxControl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class SerializerEditorFieldPane extends EditorFieldPane<SingleSelectionField<SerializerType>> {
    private final SingleSelectionField<SerializerType> selectionField;
    private final SerializerInfo serializerInfo;

    private SerializerEditorFieldPane(FileForm fileForm, SerializerInfo serializerInfo, SingleSelectionField<SerializerType> selectionField) {
        super(fileForm);
        this.serializerInfo = serializerInfo;
        this.selectionField = selectionField;

        setup();
    }

    private void setup() {
        this.selectionField.selectionProperty()
                .addListener((observable, oldValue, newValue) -> reloadSerializerType(newValue));

        this.changedProperty().bind(this.selectionField.changedProperty());
        this.validProperty().bind(this.selectionField.validProperty());

        SimpleControl<SingleSelectionField<SerializerType>> simpleControl = this.selectionField.getRenderer();
        simpleControl.setField(this.selectionField);
        SceneUtils.setAnchors(simpleControl);
        this.getChildren().add(simpleControl);
    }

    private void reloadSerializerType(SerializerType newValue) {
        EditorFormPane formPane = this.getFormPane();
        if (formPane != null) {
            FileForm serializerForm = null;
            if (newValue != null) {
                serializerForm = newValue.fileForm();
            }

            formPane.serializerFormProperty()
                    .set(serializerForm);
        }
    }

    @Override
    public SingleSelectionField<SerializerType> getField() {
        return this.selectionField;
    }

    @Override
    public void setValue(JsonElement jsonElement) {
        if (jsonElement != null && jsonElement.isJsonPrimitive()) {
            ResourceId.fromString(jsonElement.getAsString())
                    .result()
                    .map(Registries.getSerializerTypes()::getValue)
                    .ifPresent(this.getField().selectionProperty()::set);
        }

        if (this.getField().getSelection() == null && !this.getField().getItems().isEmpty()) {
            this.getField()
                    .selectionProperty()
                    .set(this.getField()
                            .itemsProperty()
                            .get(0)
                    );
        }
    }

    @Override
    public JsonElement getValue() {
        SerializerType serializerType = this.getField()
                .getSelection();
        if (serializerType != null && !serializerType.id().equals(ResourceId.NULL)) {
            return new JsonPrimitive(serializerType.serializerId().toString());
        } else {
            return JsonNull.INSTANCE;
        }
    }

    @Override
    public String getFieldName() {
        return this.serializerInfo.fieldName();
    }

    @Override
    public int getSortOrder() {
        return 10000;
    }

    @Override
    public String toString() {
        return "SerializerEditorFieldPane{" +
                "serializerInfo=" + serializerInfo +
                '}';
    }

    public static Optional<SerializerEditorFieldPane> of(FileForm fileForm, Supplier<List<SerializerType>> gatherTypes) {
        return fileForm.getSerializer()
                .map(serializerInfo -> {
                    List<SerializerType> serializerTypes = new ArrayList<>(gatherTypes.get());
                    SerializerType defaultFieldsType = null;
                    if (!serializerInfo.defaultFields().isEmpty()) {
                        defaultFieldsType = new SerializerType(
                                ResourceId.NULL,
                                ResourceId.NULL,
                                ResourceId.NULL,
                                "Default",
                                FileForm.of(serializerInfo.defaultFields()
                                        .toArray(FileField[]::new)
                                )
                        );
                        serializerTypes.add(0, defaultFieldsType);
                    }

                    SingleSelectionField<SerializerType> field = SingleSelectionField.ofSingleSelectionType(serializerTypes)
                            .id(serializerInfo.fieldName())
                            .label(serializerInfo.label())
                            .render(() -> new CellFactoryComboBoxControl<>(SerializerType::label))
                            .required(true);

                    serializerInfo.defaultType()
                            .map(Registries.getSerializerTypes()::getValue)
                            .ifPresent(field.selectionProperty()::set);

                    if (field.getSelection() == null && defaultFieldsType != null) {
                        field.selectionProperty().set(defaultFieldsType);
                    }
                    return new SerializerEditorFieldPane(fileForm, serializerInfo, field);
                });
    }
}
