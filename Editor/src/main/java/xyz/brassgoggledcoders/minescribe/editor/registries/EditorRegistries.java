package xyz.brassgoggledcoders.minescribe.editor.registries;

import com.google.common.base.Suppliers;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.*;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.*;

import java.util.function.Supplier;

public class EditorRegistries {
    private static final Supplier<EditorFormFieldRegistry> EDITOR_FORM_FIELD_TRANSFORMS =
            Suppliers.memoize(() -> {
                EditorFormFieldRegistry registry = new EditorFormFieldRegistry();
                registry.register("checkbox", new EditorFormFieldTransform<>(
                        CheckBoxFileField.class,
                        CheckBoxEditorFormField::new
                ));
                registry.register("list_selection", new EditorFormFieldTransform<>(
                        ListSelectionFileField.class,
                        ListSelectionEditorFormField::new
                ));
                registry.register("string", new EditorFormFieldTransform<>(
                        StringFileField.class,
                        StringEditorFormField::new
                ));
                registry.register("list_of_fields", new EditorFormFieldTransform<>(
                        ListOfFileField.class,
                        ListOfEditorFormField::new
                ));
                registry.register("single_selection", new EditorFormFieldTransform<>(
                        SingleSelectionFileField.class,
                        SingleSelectionEditorFormField::new
                ));
                registry.validate();
                return registry;
            });

    public static EditorFormFieldRegistry getEditorFormFieldRegistry() {
        return EDITOR_FORM_FIELD_TRANSFORMS.get();
    }
}