package xyz.brassgoggledcoders.minescribe.editor.util;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.structure.SingleSelectionField;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;
import xyz.brassgoggledcoders.minescribe.core.packinfo.SerializerType;
import xyz.brassgoggledcoders.minescribe.editor.registries.EditorRegistries;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.IEditorFormField;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.CellFactoryComboBoxControl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FormUtils {
    public static List<IEditorFormField<?>> getFields(FileForm form) {
        List<IEditorFormField<?>> editorFormFieldList = new ArrayList<>();
        for (IFileField field : form.getFields()) {
            IEditorFormField<?> editorFormField = EditorRegistries.getEditorFormFieldRegistry()
                    .createEditorFieldFor(field);

            if (editorFormField != null) {
                editorFormFieldList.add(editorFormField);
            }
        }

        return editorFormFieldList;
    }

    public static FormSetup setupForm(FileForm fileForm, boolean addSerializerToForm) {
        List<IEditorFormField<?>> editorFormFields = getFields(fileForm);
        List<Field<?>> fields = editorFormFields.stream()
                .map(editorFormField -> editorFormField.asField()
                        .label(editorFormField.getFileField().getLabel())
                        .id(editorFormField.getFileField().getField())
                )
                .collect(Collectors.toList());

        Optional<SingleSelectionField<SerializerType>> serializerSelection = fileForm.getSerializer()
                .map(serializerInfo -> SingleSelectionField.ofSingleSelectionType(new ArrayList<SerializerType>())
                        .id(serializerInfo.fieldName())
                        .label(serializerInfo.label())
                        .render(() -> new CellFactoryComboBoxControl<>(SerializerType::label))
                        .required(true)
                );

        if (addSerializerToForm) {
            serializerSelection.ifPresent(fields::add);
        }

        Form form = Form.of(Group.of(fields.toArray(Field[]::new)));

        return new FormSetup(
                editorFormFields,
                form,
                serializerSelection
        );
    }

    public static void tryLoadForm(Form form, List<IEditorFormField<?>> editorFormFields, JsonObject jsonObject) {
        if (form != null && editorFormFields != null && jsonObject != null) {
            for (IEditorFormField<?> editorFormField : editorFormFields) {
                if (jsonObject.has(editorFormField.getFileField().getField())) {
                    editorFormField.loadFromJson(jsonObject.get(editorFormField.getFileField().getField()));
                }
            }
            form.persist();
            form.reset();
        }
    }

    public record FormSetup(
            List<IEditorFormField<?>> editorFields,
            Form form,
            Optional<SingleSelectionField<SerializerType>> serializerFieldOpt
    ) {

    }
}
