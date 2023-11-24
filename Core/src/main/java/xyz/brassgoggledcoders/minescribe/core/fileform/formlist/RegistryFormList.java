package xyz.brassgoggledcoders.minescribe.core.fileform.formlist;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.codec.RegistryCodec;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;

import java.util.List;

public record RegistryFormList(
        Registry<?, ?> registry
) implements IFormList {
    public static final Codec<RegistryFormList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            new RegistryCodec().fieldOf("registry").forGetter(RegistryFormList::registry)
    ).apply(instance, RegistryFormList::new));

    @Override
    public List<String> getValues() {
        return registry.getKeys()
                .stream()
                .map(Object::toString)
                .toList();
    }

    @Override
    @NotNull
    public Codec<? extends IFormList> getCodec() {
        return CODEC;
    }
}
