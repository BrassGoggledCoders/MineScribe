package xyz.brassgoggledcoders.minescribe.editor.registry;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import javafx.scene.control.SpinnerValueFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.FormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.*;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.DoubleFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.IntegerFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object.ReferencedObjectFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.packinfo.*;
import xyz.brassgoggledcoders.minescribe.core.registry.BasicJsonRegistry;
import xyz.brassgoggledcoders.minescribe.core.registry.LoadOnGetJsonRegistry;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.util.Range;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control.*;

import java.nio.file.Path;
import java.util.function.Supplier;

public class EditorRegistries {

    private static final Supplier<BasicJsonRegistry<ResourceId, PackContentParentType>> CONTENT_PARENT_TYPES =
            Suppliers.memoize(() -> new BasicJsonRegistry<>(
                    "contentParentTypes",
                    Path.of("types", "parent").toString(),
                    ResourceId.CODEC,
                    PackContentParentType.CODEC,
                    PackContentType::getId
            ));

    private static final Supplier<BasicJsonRegistry<ResourceId, PackContentChildType>> CONTENT_CHILD_TYPES =
            Suppliers.memoize(() -> new BasicJsonRegistry<>(
                    "contentChildTypes",
                    Path.of("types", "child").toString(),
                    ResourceId.CODEC,
                    PackContentChildType.CODEC,
                    PackContentType::getId
            ));
    @SuppressWarnings("Convert2Diamond")
    private static final Supplier<EditorFormFieldRegistry> EDITOR_FORM_FIELD_TRANSFORMS = Suppliers.memoize(() -> {
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

    private static final Supplier<LoadOnGetJsonRegistry<FormList>> FORM_LISTS = Suppliers.memoize(
            () -> new LoadOnGetJsonRegistry<>(
                    "formLists",
                    Path.of("formLists"),
                    FormList.CODEC
            )
    );

    private static final Supplier<BasicJsonRegistry<ResourceId, ObjectType>> OBJECT_TYPES =
            Suppliers.memoize(() -> new BasicJsonRegistry<>(
                    "objectTypes",
                    Path.of("types", "object").toString(),
                    ResourceId.CODEC,
                    ObjectType.CODEC,
                    ObjectType::id
            ));

    private static final Supplier<SerializerTypeRegistry> SERIALIZER_TYPES = Suppliers.memoize(SerializerTypeRegistry::new);

    private static final Supplier<ScriptLoadedRegistry<ResourceId, Codec<? extends Validation<?>>>> VALIDATION_CODECS =
            Suppliers.memoize(() -> new ScriptLoadedRegistry<>(
                    "validations",
                    ResourceId.CODEC,
                    Path.of("validation")
            ));

    public static BasicJsonRegistry<ResourceId, PackContentParentType> getContentParentTypes() {
        return CONTENT_PARENT_TYPES.get();
    }

    public static BasicJsonRegistry<ResourceId, PackContentChildType> getContentChildTypes() {
        return CONTENT_CHILD_TYPES.get();
    }

    public static EditorFormFieldRegistry getEditorFormFieldRegistry() {
        return EDITOR_FORM_FIELD_TRANSFORMS.get();
    }

    public static LoadOnGetJsonRegistry<FormList> getFormLists() {
        return FORM_LISTS.get();
    }

    public static SerializerTypeRegistry getSerializerTypes() {
        return SERIALIZER_TYPES.get();
    }

    public static BasicJsonRegistry<ResourceId, ObjectType> getObjectTypes() {
        return OBJECT_TYPES.get();
    }

    public static ScriptLoadedRegistry<ResourceId, Codec<? extends Validation<?>>> getValidationRegistry() {
        return VALIDATION_CODECS.get();
    }

    public static void load(Path mineScribeRoot) {
        Path dataRoot = mineScribeRoot.resolve("data");
        //Validations are required by the others;

        Registries.load(dataRoot, EditorRegistries.getValidationRegistry());
        FORM_LISTS.get().setMineScribePath(dataRoot);
        CONTENT_PARENT_TYPES.get().load(dataRoot);
        CONTENT_CHILD_TYPES.get().load(dataRoot);
        SERIALIZER_TYPES.get().load(dataRoot);
        OBJECT_TYPES.get().load(dataRoot);
    }

    public static void addSourcePath(Path sourcePath) {
        VALIDATION_CODECS.get().load(sourcePath);
        CONTENT_PARENT_TYPES.get().load(sourcePath);
        CONTENT_CHILD_TYPES.get().load(sourcePath);
        SERIALIZER_TYPES.get().load(sourcePath);
        OBJECT_TYPES.get().load(sourcePath);
    }
}
