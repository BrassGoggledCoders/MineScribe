package xyz.brassgoggledcoders.minescribe.core.fileform.formlist;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.codec.LazyCodec;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.util.List;
import java.util.function.Function;

public interface IFormList {
    Codec<IFormList> CODEC = LazyCodec.of(() -> Registries.getFormListCodecs()
            .getCodec()
            .dispatch(
                    IFormList::getCodec,
                    Function.identity()
            )
    );

    List<String> getValues();

    @NotNull
    Codec<? extends IFormList> getCodec();
}
