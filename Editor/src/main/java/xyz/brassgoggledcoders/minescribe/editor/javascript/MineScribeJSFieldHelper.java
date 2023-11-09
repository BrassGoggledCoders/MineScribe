package xyz.brassgoggledcoders.minescribe.editor.javascript;

import com.mojang.serialization.Codec;

@SuppressWarnings("unused")
public class MineScribeJSFieldHelper {
    public MineScribeJSField<String> ofString(String fieldName, String defaultValue) {
        return new MineScribeJSField<>(
                fieldName,
                Codec.STRING,
                defaultValue
        );
    }
}
