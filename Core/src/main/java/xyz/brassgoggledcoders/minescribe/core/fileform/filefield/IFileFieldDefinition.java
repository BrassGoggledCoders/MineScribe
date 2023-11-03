package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.codec.LazyCodec;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.util.List;
import java.util.function.Function;

public interface IFileFieldDefinition {
    Codec<IFileFieldDefinition> CODEC = LazyCodec.of(() -> Registries.getFileFieldCodecRegistry()
            .getCodec()
            .dispatch(
                    IFileFieldDefinition::getCodec,
                    Function.identity()
            )
    );

    Codec<List<IFileFieldDefinition>> LIST_CODEC = LazyCodec.of(CODEC::listOf);

    @NotNull
    Codec<? extends IFileFieldDefinition> getCodec();
}
