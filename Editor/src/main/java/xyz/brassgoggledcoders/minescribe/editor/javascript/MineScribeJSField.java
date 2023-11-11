package xyz.brassgoggledcoders.minescribe.editor.javascript;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class MineScribeJSField<T> {
    private final String fieldName;
    private final T defaultValue;
    private final Function<JsonElement, T> parseJson;

    public MineScribeJSField(String fieldName, T defaultValue, Function<JsonElement, T> parseJson) {
        this.fieldName = fieldName;
        this.defaultValue = defaultValue;
        this.parseJson = parseJson;
    }

    @Nullable
    public T loadValue(JsonObject jsonObject) {
        JsonElement jsonElement = jsonObject.get(this.fieldName);
        if (jsonElement != null && !jsonElement.isJsonNull()) {
            return parseJson.apply(jsonElement);
        } else {
            return defaultValue;
        }

    }

    public String getFieldName() {
        return this.fieldName;
    }
}
