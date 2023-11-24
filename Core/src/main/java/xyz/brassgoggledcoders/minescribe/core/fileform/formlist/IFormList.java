package xyz.brassgoggledcoders.minescribe.core.fileform.formlist;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IFormList {
    List<String> getValues();

    @NotNull
    Codec<? extends IFormList> getCodec();
}
