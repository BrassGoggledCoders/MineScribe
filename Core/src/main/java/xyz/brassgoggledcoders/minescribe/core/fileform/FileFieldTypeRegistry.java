package xyz.brassgoggledcoders.minescribe.core.fileform;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.codec.BiMapDispatchCodec;
import xyz.brassgoggledcoders.minescribe.core.codec.LazyCodec;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.CheckBoxFileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.ListSelectionFileField;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class FileFieldTypeRegistry {
    public static final Codec<IFileField> CODEC = LazyCodec.of(() ->
            new BiMapDispatchCodec<>("fileField", Codec.STRING, () -> FileFieldTypeRegistry.getInstance().getCodecMap())
                    .dispatch(
                            IFileField::getCodec,
                            Function.identity()
                    )
    );

    public static final Codec<List<IFileField>> LIST_CODEC = LazyCodec.of(CODEC::listOf);

    private static final Supplier<FileFieldTypeRegistry> INSTANCE = Suppliers.memoize(FileFieldTypeRegistry::new);
    private final BiMap<String, Codec<? extends IFileField>> codecMap;

    public FileFieldTypeRegistry() {
        this.codecMap = initializeFields();
    }

    private BiMap<String, Codec<? extends IFileField>> getCodecMap() {
        return this.codecMap;
    }

    private static BiMap<String, Codec<? extends IFileField>> initializeFields() {
        BiMap<String, Codec<? extends IFileField>> fields = HashBiMap.create();
        fields.put("checkbox", CheckBoxFileField.CODEC);
        fields.put("list_selection", ListSelectionFileField.CODEC);
        return fields;
    }

    public static FileFieldTypeRegistry getInstance() {
        return INSTANCE.get();
    }
}
