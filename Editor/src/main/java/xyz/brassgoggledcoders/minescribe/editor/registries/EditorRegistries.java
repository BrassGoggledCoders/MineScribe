package xyz.brassgoggledcoders.minescribe.editor.registries;

import com.dlsc.formsfx.model.structure.DoubleField;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.IntegerField;
import com.google.common.base.Suppliers;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.*;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.DoubleFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.IntegerFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object.ReferencedObjectFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.*;

import java.util.function.Supplier;

public class EditorRegistries {
    private static final Supplier<EditorFormFieldRegistry> EDITOR_FORM_FIELD_TRANSFORMS =
            Suppliers.memoize(() -> {
                EditorFormFieldRegistry registry = new EditorFormFieldRegistry();
                registry.register("checkbox", new EditorFormFieldTransform<>(
                        CheckBoxFileFieldDefinition.class,
                        CheckBoxEditorFormField::new
                ));
                registry.register("list_selection", new EditorFormFieldTransform<>(
                        ListSelectionFileFieldDefinition.class,
                        ListSelectionEditorFormField::new
                ));
                registry.register("string", new EditorFormFieldTransform<>(
                        StringFileFieldDefinition.class,
                        StringEditorFormField::new
                ));
                registry.register("list_of_fields", new EditorFormFieldTransform<>(
                        ListOfFileFieldDefinition.class,
                        ListOfEditorFormField::new
                ));
                registry.register("single_selection", new EditorFormFieldTransform<>(
                        SingleSelectionFileFieldDefinition.class,
                        SingleSelectionEditorFormField::new
                ));
                registry.register("integer", new EditorFormFieldTransform<IntegerFileFieldDefinition, NumberEditorFormField<IntegerFileFieldDefinition, Integer, IntegerProperty, IntegerField>, IntegerField>(
                        IntegerFileFieldDefinition.class,
                        integerFileField -> new NumberEditorFormField<>(
                                integerFileField,
                                fileField -> Field.ofIntegerType(fileField.getRange()
                                        .start()
                                )
                        )
                ));
                registry.register("double", new EditorFormFieldTransform<DoubleFileFieldDefinition, NumberEditorFormField<DoubleFileFieldDefinition, Double, DoubleProperty, DoubleField>, DoubleField>(
                        DoubleFileFieldDefinition.class,
                        doubleFileField -> new NumberEditorFormField<>(
                                doubleFileField,
                                fileField -> Field.ofDoubleType(fileField.getRange()
                                        .start()
                                )
                        )
                ));
                registry.register("object_ref", new EditorFormFieldTransform<>(
                        ReferencedObjectFileFieldDefinition.class,
                        ObjectEditorFormField::new
                ));
                registry.validate();
                return registry;
            });

    public static EditorFormFieldRegistry getEditorFormFieldRegistry() {
        return EDITOR_FORM_FIELD_TRANSFORMS.get();
    }
}
