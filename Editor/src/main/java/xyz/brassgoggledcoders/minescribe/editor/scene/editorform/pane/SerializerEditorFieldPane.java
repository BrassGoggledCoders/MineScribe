package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Label;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.SerializerInfo;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.packinfo.SerializerType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.scene.SceneUtils;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control.SingleSelectionFieldControl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class SerializerEditorFieldPane extends EditorFieldPane<SingleSelectionFieldControl<SerializerType>> {
    private final SingleSelectionFieldControl<SerializerType> fieldControl;
    private final SerializerInfo serializerInfo;

    private SerializerEditorFieldPane(FileForm fileForm, SerializerInfo serializerInfo,
                                      SingleSelectionFieldControl<SerializerType> fieldControl) {
        super(fileForm);
        this.serializerInfo = serializerInfo;
        this.fieldControl = fieldControl;
        this.labelProperty().set(new Label(serializerInfo.label()));

        setup();
    }

    private void setup() {
        this.fieldControl.valueProperty()
                .addListener((observable, oldValue, newValue) -> reloadSerializerType(newValue));

        this.changedProperty().bind(this.fieldControl.changedProperty());
        this.validProperty().bind(this.fieldControl.validProperty());

        SceneUtils.setAnchors(this.fieldControl.getNode());
        this.getChildren().add(this.fieldControl.getNode());
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
    public SingleSelectionFieldControl<SerializerType> getContent() {
        return this.fieldControl;
    }

    @Override
    public void setValue(JsonElement jsonElement) {
        ObjectProperty<SerializerType> selected = this.getContent()
                .valueProperty();
        if (jsonElement != null && jsonElement.isJsonPrimitive()) {
            ResourceId.fromString(jsonElement.getAsString())
                    .result()
                    .map(Registries.getSerializerTypes()::getValue)
                    .ifPresent(selected::set);
        }

        if (selected.get() == null) {
            serializerInfo.defaultType()
                    .map(Registries.getSerializerTypes()::getValue)
                    .ifPresent(selected::set);
        }

        List<SerializerType> items = this.getContent()
                .itemsProperty()
                .get();

        if (selected.get() == null) {
            items.stream()
                    .filter(serializerType -> serializerType.id().equals(ResourceId.NULL))
                    .findFirst()
                    .ifPresent(selected::set);
        }

        if (selected.get() == null) {
            selected.set(items.get(0));
        }
    }

    @Override
    public JsonElement getValue() {
        SerializerType serializerType = this.getContent()
                .valueProperty()
                .get();
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
                    if (serializerInfo.defaultForm().isPresent()) {
                        SerializerType defaultFieldsType = new SerializerType(
                                ResourceId.NULL,
                                ResourceId.NULL,
                                ResourceId.NULL,
                                "Default",
                                serializerInfo.defaultForm()
                                        .get()
                        );
                        serializerTypes.add(0, defaultFieldsType);
                    }

                    SingleSelectionFieldControl<SerializerType> field = SingleSelectionFieldControl.of(
                                    serializerTypes,
                                    serializerType -> serializerType.id().toString(),
                                    SerializerType.class
                            )
                            .withId(serializerInfo.fieldName())
                            .withLabel(serializerInfo.label())
                            .withRequired(true);


                    field.setLabelMaker(SerializerType::label);

                    return new SerializerEditorFieldPane(fileForm, serializerInfo, field);
                });
    }
}
