package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.codec.LazyCodec;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.util.List;
import java.util.function.Function;

public interface IFileField extends Comparable<IFileField> {
    Codec<IFileField> CODEC = LazyCodec.of(() -> Registries.getFileFieldCodecRegistry()
            .getDispatchCodec()
            .dispatch(
                    IFileField::getCodec,
                    Function.identity()
            )
    );

    Codec<List<IFileField>> LIST_CODEC = LazyCodec.of(CODEC::listOf);

    String getLabel();

    String getField();

    int getSortOrder();

    Codec<? extends IFileField> getCodec();
}
