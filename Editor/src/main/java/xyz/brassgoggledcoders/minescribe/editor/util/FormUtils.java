package xyz.brassgoggledcoders.minescribe.editor.util;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.structure.SingleSelectionField;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileFieldInfo;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.packinfo.SerializerType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.registries.EditorRegistries;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.IEditorFormField;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.CellFactoryComboBoxControl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FormUtils {
    public static List<Pair<FileFieldInfo, IEditorFormField<?>>> getFields(FileForm form) {
        List<Pair<FileFieldInfo, IEditorFormField<?>>> editorFormFieldList = new ArrayList<>();
        for (FileField<?> field : form.getFields()) {
            IEditorFormField<?> editorFormField = EditorRegistries.getEditorFormFieldRegistry()
                    .createEditorFieldFor(field.definition());

            if (editorFormField != null) {
                editorFormFieldList.add(Pair.of(field.info(), editorFormField));
            }
        }

        return editorFormFieldList;
    }

    public static FormSetup setupForm(FileForm fileForm, Supplier<List<SerializerType>> gatherTypes) {
        List<Pair<FileFieldInfo, IEditorFormField<?>>> editorFormFields = getFields(fileForm);
        List<Field<?>> fields = editorFormFields.stream()
                .map(editorFormField -> editorFormField.getSecond()
                        .asField()
                        .label(editorFormField.getFirst()
                                .label()
                        )
                        .id(editorFormField.getFirst()
                                .field()
                        )
                        .required(editorFormField.getFirst()
                                .required()
                        )
                )
                .collect(Collectors.toList());

        Optional<SingleSelectionField<SerializerType>> serializerSelection = fileForm.getSerializer()
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
                    return field;
                });

        serializerSelection.ifPresent(fields::add);

        Form form = Form.of(Group.of(fields.toArray(Field[]::new)));

        return new FormSetup(
                editorFormFields,
                form,
                serializerSelection
        );
    }

    public static void tryLoadForm(FormSetup formSetup, JsonObject jsonObject) {
        tryLoadForm(formSetup.form(), formSetup.editorFields(), jsonObject);

        formSetup.serializerFieldOpt.ifPresent(serializerField -> {
            JsonElement typeElement = jsonObject.get(serializerField.getID());
            if (typeElement != null && typeElement.isJsonPrimitive()) {
                ResourceId.fromString(typeElement.getAsString())
                        .result()
                        .map(Registries.getSerializerTypes()::getValue)
                        .ifPresent(serializerField.selectionProperty()::set);
            }

            if (serializerField.getSelection() == null && !serializerField.getItems().isEmpty()) {
                serializerField.selectionProperty()
                        .set(serializerField.itemsProperty()
                                .get(0)
                        );
            }
        });
    }

    public static void tryLoadForm(Form form, List<Pair<FileFieldInfo, IEditorFormField<?>>> editorFormFields, JsonObject jsonObject) {
        if (form != null && editorFormFields != null && jsonObject != null) {
            for (Pair<FileFieldInfo, IEditorFormField<?>> editorFormField : editorFormFields) {
                if (jsonObject.has(editorFormField.getFirst().field())) {
                    editorFormField.getSecond().loadFromJson(jsonObject.get(editorFormField.getFirst().field()));
                }
            }
            form.persist();
            form.reset();
        }
    }

    public static void trySaveForm(FormSetup formSetup, JsonObject jsonObject) {
        for (Pair<FileFieldInfo, IEditorFormField<?>> editorFormField : formSetup.editorFields()) {
            JsonElement savedJson = editorFormField.getSecond()
                    .saveAsJson();
            if (savedJson != null && !savedJson.isJsonNull()) {
                jsonObject.add(
                        editorFormField.getFirst()
                                .field(),
                        savedJson
                );
            }
        }

        formSetup.serializerFieldOpt.ifPresent(serializerField -> {
            SerializerType serializerType = serializerField.selectionProperty().get();
            if (serializerType != null && !serializerType.id().equals(ResourceId.NULL)) {
                jsonObject.addProperty(serializerField.getID(), serializerType.serializerId().toString());
            }
        });
    }

    public record FormSetup(
            List<Pair<FileFieldInfo, IEditorFormField<?>>> editorFields,
            Form form,
            Optional<SingleSelectionField<SerializerType>> serializerFieldOpt
    ) {
        public void reset() {
            this.form()
                    .reset();
        }

    }
}
