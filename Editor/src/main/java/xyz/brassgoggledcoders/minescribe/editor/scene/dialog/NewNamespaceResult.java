package xyz.brassgoggledcoders.minescribe.editor.scene.dialog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record NewNamespaceResult(
        String namespace
) {
    public static final Codec<NewNamespaceResult> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("namespace").forGetter(NewNamespaceResult::namespace)
    ).apply(instance, NewNamespaceResult::new));
}
