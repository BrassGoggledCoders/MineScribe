package xyz.brassgoggledcoders.minescribe.core.text;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.codec.JsonCodec;

public abstract class FancyText implements Comparable<FancyText> {
    public static final Codec<FancyText> CODEC = new JsonCodec<>(
            FancyTextUtils::fromJson,
            FancyText::toJson
    );

    public abstract String getText();

    public abstract JsonElement toJson();

    @Override
    public int compareTo(@NotNull FancyText o) {
        return this.getText().compareTo(o.getText());
    }

    public static FancyText literal(String text) {
        return new LiteralFancyText(text);
    }
}
