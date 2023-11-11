package xyz.brassgoggledcoders.minescribe.core.registry;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.fileform.FormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.*;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.DoubleFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.IntegerFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object.ReferencedObjectFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.packinfo.*;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;

public class Registries {
    private static final Supplier<BasicStaticRegistry<String, Codec<? extends IFileFieldDefinition>>> FILE_FIELD_CODECS =
            Suppliers.memoize(() -> new BasicStaticRegistry<>(
                    "fileFields",
                    Codec.STRING,
                    initializer -> {
                        initializer.accept("checkbox", CheckBoxFileFieldDefinition.CODEC);
                        initializer.accept("list_selection", ListSelectionFileFieldDefinition.CODEC);
                        initializer.accept("list_of_fields", ListOfFileFieldDefinition.CODEC);
                        initializer.accept("string", StringFileFieldDefinition.CODEC);
                        initializer.accept("single_selection", SingleSelectionFileFieldDefinition.CODEC);
                        initializer.accept("integer", IntegerFileFieldDefinition.CODEC);
                        initializer.accept("double", DoubleFileFieldDefinition.CODEC);
                        initializer.accept("object_ref", ReferencedObjectFileFieldDefinition.CODEC);
                    }
            ));

    private static final Supplier<BasicJsonRegistry<String, MineScribePackType>> PACK_TYPES = Suppliers.memoize(() -> BasicJsonRegistry.ofString(
            "packTypes",
            MineScribePackType.CODEC,
            MineScribePackType::name
    ));

    private static final Supplier<BasicJsonRegistry<String, PackRepositoryLocation>> PACK_REPOSITORY_LOCATIONS =
            Suppliers.memoize(() -> BasicJsonRegistry.ofString(
                    "packRepositories",
                    PackRepositoryLocation.CODEC,
                    PackRepositoryLocation::label
            ));

    private static final Supplier<BasicJsonRegistry<ResourceId, PackContentParentType>> CONTENT_PARENT_TYPES =
            Suppliers.memoize(() -> new BasicJsonRegistry<>(
                    "contentParentTypes",
                    Path.of("types", "parent"),
                    ResourceId.CODEC,
                    PackContentParentType.CODEC,
                    PackContentType::getId
            ));

    private static final Supplier<BasicJsonRegistry<ResourceId, PackContentChildType>> CONTENT_CHILD_TYPES =
            Suppliers.memoize(() -> new BasicJsonRegistry<>(
                    "contentChildTypes",
                    Path.of("types", "child"),
                    ResourceId.CODEC,
                    PackContentChildType.CODEC,
                    PackContentType::getId
            ));

    private static final Supplier<LoadOnGetJsonRegistry<FormList>> FORM_LISTS = Suppliers.memoize(
            () -> new LoadOnGetJsonRegistry<>(
                    "formLists",
                    Path.of("formLists"),
                    FormList.CODEC
            )
    );

    private static final Supplier<SerializerTypeRegistry> SERIALIZER_TYPES =
            Suppliers.memoize(SerializerTypeRegistry::new);

    private static final Supplier<BasicJsonRegistry<ResourceId, ObjectType>> OBJECT_TYPES =
            Suppliers.memoize(() -> new BasicJsonRegistry<>(
                    "objectTypes",
                    Path.of("types", "object"),
                    ResourceId.CODEC,
                    ObjectType.CODEC,
                    ObjectType::id
            ));

    private static Registry<ResourceId, Codec<? extends Validation<?>>> validations = null;

    public static BasicStaticRegistry<String, Codec<? extends IFileFieldDefinition>> getFileFieldCodecRegistry() {
        return FILE_FIELD_CODECS.get();
    }

    public static BasicJsonRegistry<String, MineScribePackType> getPackTypes() {
        return PACK_TYPES.get();
    }

    public static BasicJsonRegistry<String, PackRepositoryLocation> getPackRepositoryLocations() {
        return PACK_REPOSITORY_LOCATIONS.get();
    }

    public static BasicJsonRegistry<ResourceId, PackContentParentType> getContentParentTypes() {
        return CONTENT_PARENT_TYPES.get();
    }

    public static BasicJsonRegistry<ResourceId, PackContentChildType> getContentChildTypes() {
        return CONTENT_CHILD_TYPES.get();
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

    public static Registry<ResourceId, Codec<? extends Validation<?>>> getValidations() {
        return Objects.requireNonNull(validations);
    }

    public static Registry<ResourceId, Codec<? extends Validation<?>>> getValidationsNullable() {
        return validations;
    }

    public static void load(Path mineScribeRoot, Registry<ResourceId, Codec<? extends Validation<?>>> validations) {
        Registries.validations = validations;
        PACK_REPOSITORY_LOCATIONS.get().load(mineScribeRoot);
        PACK_TYPES.get().load(mineScribeRoot);
        FORM_LISTS.get().setMineScribePath(mineScribeRoot);
        CONTENT_PARENT_TYPES.get().load(mineScribeRoot);
        CONTENT_CHILD_TYPES.get().load(mineScribeRoot);
        SERIALIZER_TYPES.get().load(mineScribeRoot);
        OBJECT_TYPES.get().load(mineScribeRoot);
    }
}
