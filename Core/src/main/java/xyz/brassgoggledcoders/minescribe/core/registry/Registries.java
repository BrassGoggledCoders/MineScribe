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

    public static Registry<ResourceId, Codec<? extends Validation<?>>> getValidations() {
        return Objects.requireNonNull(validations);
    }

    public static void load(Path mineScribeRoot, Registry<ResourceId, Codec<? extends Validation<?>>> validations) {
        Registries.validations = validations;
        PACK_REPOSITORY_LOCATIONS.get().load(mineScribeRoot);
        PACK_TYPES.get().load(mineScribeRoot);
    }
}
