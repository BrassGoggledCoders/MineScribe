package xyz.brassgoggledcoders.minescribe.editor.javascript;

import com.mojang.serialization.Codec;

public class MineScribeJSField<T> {
    private final String fieldName;
    private final Codec<T> codec;
    private final T defaultValue;

    public MineScribeJSField(String fieldName, Codec<T> codec, T defaultValue) {
        this.fieldName = fieldName;
        this.codec = codec;
        this.defaultValue = defaultValue;
    }
}
