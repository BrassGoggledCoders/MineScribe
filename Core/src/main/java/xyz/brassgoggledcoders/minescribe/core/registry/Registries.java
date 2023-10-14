package xyz.brassgoggledcoders.minescribe.core.registry;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.CheckBoxFileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.ListSelectionFileField;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;

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
                    }
            ));

    private static final Supplier<BasicJsonRegistry<MineScribePackType>> PACK_TYPES = Suppliers.memoize(() -> new BasicJsonRegistry<>(
            "packTypes",
            MineScribePackType.CODEC,
            MineScribePackType::name
    ));

    public static BasicStaticRegistry<String , Codec<? extends IFileField>> getFileFieldCodecRegistry() {
        return FILE_FIELD_CODECS.get();
    }

    public static BasicJsonRegistry<MineScribePackType> getPackTypes() {
        return PACK_TYPES.get();
    }

    public static void load(Path mineScribeRoot) {
        PACK_TYPES.get().load(mineScribeRoot);
    }
}
