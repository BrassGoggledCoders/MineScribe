package xyz.brassgoggledcoders.minescribe.editor.scene.dialog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.util.List;
import java.util.Optional;

public record PackCreationResult(
        List<MineScribePackType> packTypes,
        String name,
        Optional<String> description
) {
    public static Codec<PackCreationResult> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registries.getPackTypeRegistry().getCodec().listOf().fieldOf("packTypes").forGetter(PackCreationResult::packTypes),
            Codec.STRING.fieldOf("name").forGetter(PackCreationResult::name),
            Codec.STRING.optionalFieldOf("description").forGetter(PackCreationResult::description)
    ).apply(instance, PackCreationResult::new));
}
