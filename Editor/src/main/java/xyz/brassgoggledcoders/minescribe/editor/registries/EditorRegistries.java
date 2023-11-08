package xyz.brassgoggledcoders.minescribe.editor.registries;

import com.google.common.base.Suppliers;
import javafx.scene.control.SpinnerValueFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.*;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.DoubleFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.IntegerFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object.ReferencedObjectFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.util.Range;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control.*;

import java.util.function.Supplier;

public class EditorRegistries {
    private static final Supplier<EditorFormFieldRegistry> EDITOR_FORM_FIELD_TRANSFORMS =
            Suppliers.memoize(() -> {
                EditorFormFieldRegistry registry = new EditorFormFieldRegistry();
                registry.register("checkbox", new EditorFormFieldTransform<>(
                        CheckBoxFileFieldDefinition.class,
                        CheckBoxFieldControl::of
                ));
                registry.register("string", new EditorFormFieldTransform<>(
                        StringFileFieldDefinition.class,
                        StringFieldControl::of
                ));
                registry.register("list_selection", new EditorFormFieldTransform<>(
                        ListSelectionFileFieldDefinition.class,
                        ListSelectionFieldContent::of
                ));

                registry.register("list_of_fields", new EditorFormFieldTransform<>(
                        ListOfFileFieldDefinition.class,
                        ListOfFieldFieldControl::of
                ));
                registry.register("single_selection", new EditorFormFieldTransform<>(
                        SingleSelectionFileFieldDefinition.class,
                        SingleSelectionFieldControl::of
                ));
                registry.register("integer", new EditorFormFieldTransform<>(
                        IntegerFileFieldDefinition.class,
                        integerFileField -> {
                            Range<Integer> range = integerFileField.getRange();
                            return new NumberFieldControl<>(
                                    integerFileField,
                                    new SpinnerValueFactory.IntegerSpinnerValueFactory(
                                            range.min(),
                                            range.max(),
                                            range.start()
                                    )
                            );
                        }
                ));
                registry.register("double", new EditorFormFieldTransform<>(
                        DoubleFileFieldDefinition.class,
                        doubleFileField -> {
                            Range<Double> doubleRange = doubleFileField.getRange();
                            return new NumberFieldControl<>(
                                    doubleFileField,
                                    new SpinnerValueFactory.DoubleSpinnerValueFactory(
                                            doubleRange.min(),
                                            doubleRange.max(),
                                            doubleRange.start()
                                    )
                            );
                        }
                ));
                registry.register("object_ref", new EditorFormFieldTransform<>(
                        ReferencedObjectFileFieldDefinition.class,
                        ObjectFieldControl::of
                ));
                registry.validate();
                return registry;
            });

    public static EditorFormFieldRegistry getEditorFormFieldRegistry() {
        return EDITOR_FORM_FIELD_TRANSFORMS.get();
    }
}
