package xyz.brassgoggledcoders.minescribe.editor.registry;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import javafx.scene.control.SpinnerValueFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.*;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.DoubleFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.IntegerFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object.ReferencedObjectFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.util.Range;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control.*;

import java.nio.file.Path;
import java.util.function.Supplier;

public class EditorRegistries {
    @SuppressWarnings("Convert2Diamond")
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
                registry.register("list_selection", new EditorFormFieldTransform<ListSelectionFileFieldDefinition, ListSelectionFieldContent<String>>(
                        ListSelectionFileFieldDefinition.class,
                        ListSelectionFieldContent::of
                ));

                registry.register("list_of_fields", new EditorFormFieldTransform<>(
                        ListOfFileFieldDefinition.class,
                        ListOfFieldFieldControl::of
                ));
                registry.register("single_selection", new EditorFormFieldTransform<SingleSelectionFileFieldDefinition, SingleSelectionFieldControl<String>>(
                        SingleSelectionFileFieldDefinition.class,
                        SingleSelectionFieldControl::of
                ));
                registry.register("integer", new EditorFormFieldTransform<IntegerFileFieldDefinition, NumberFieldControl<Integer>>(
                        IntegerFileFieldDefinition.class,
                        integerFileField -> {
                            Range<Integer> range = integerFileField.getRange();
                            return new NumberFieldControl<>(
                                    integerFileField,
                                    Integer.class,
                                    new SpinnerValueFactory.IntegerSpinnerValueFactory(
                                            range.min(),
                                            range.max(),
                                            range.start()
                                    )
                            );
                        }
                ));
                registry.register("double", new EditorFormFieldTransform<DoubleFileFieldDefinition, NumberFieldControl<Double>>(
                        DoubleFileFieldDefinition.class,
                        doubleFileField -> {
                            Range<Double> doubleRange = doubleFileField.getRange();
                            return new NumberFieldControl<>(
                                    doubleFileField,
                                    Double.class,
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

    private static final Supplier<ScriptLoadedRegistry<ResourceId, Codec<? extends Validation<?>>>> VALIDATION_CODECS =
            Suppliers.memoize(() -> new ScriptLoadedRegistry<>(
                    "validations",
                    ResourceId.CODEC,
                    Path.of("validation")
            ));

    public static EditorFormFieldRegistry getEditorFormFieldRegistry() {
        return EDITOR_FORM_FIELD_TRANSFORMS.get();
    }

    public static ScriptLoadedRegistry<ResourceId, Codec<? extends Validation<?>>> getValidationRegistry() {
        return VALIDATION_CODECS.get();
    }

    public static void load(Path mineScribeFolder) {
        VALIDATION_CODECS.get().load(mineScribeFolder);
    }
}
