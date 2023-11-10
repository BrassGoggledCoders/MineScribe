package xyz.brassgoggledcoders.minescribe.editor.javascript;

import com.google.gson.JsonElement;

@SuppressWarnings("unused")
public class MineScribeJSFieldHelper {
    public MineScribeJSField<String> ofString(String fieldName, String defaultValue) {
        return new MineScribeJSField<>(
                fieldName,
                defaultValue,
                JsonElement::getAsString
        );
    }
}
