package xyz.brassgoggledcoders.minescribe.core.registry;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.fileform.FormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.*;
import xyz.brassgoggledcoders.minescribe.core.packinfo.*;

import java.nio.file.Path;
import java.util.function.Supplier;

public class Registries {
    private static final Supplier<BasicStaticRegistry<String, Codec<? extends IFileField>>> FILE_FIELD_CODECS =
            Suppliers.memoize(() -> new BasicStaticRegistry<>(
                    "fileFields",
                    Codec.STRING,
                    initializer -> {
                        initializer.accept("checkbox", CheckBoxFileField.CODEC);
                        initializer.accept("list_selection", ListSelectionFileField.CODEC);
                        initializer.accept("list_of_fields", ListOfFileField.CODEC);
                        initializer.accept("string", StringFileField.CODEC);
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

    public static BasicStaticRegistry<String, Codec<? extends IFileField>> getFileFieldCodecRegistry() {
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

    public static void load(Path mineScribeRoot) {
        PACK_REPOSITORY_LOCATIONS.get().load(mineScribeRoot);
        PACK_TYPES.get().load(mineScribeRoot);
        FORM_LISTS.get().setMineScribePath(mineScribeRoot);
        CONTENT_PARENT_TYPES.get().load(mineScribeRoot);
        CONTENT_CHILD_TYPES.get().load(mineScribeRoot);
    }
}
