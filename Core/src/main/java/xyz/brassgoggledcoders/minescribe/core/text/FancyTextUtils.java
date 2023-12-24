package xyz.brassgoggledcoders.minescribe.core.text;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class FancyTextUtils {
    public static FancyText fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            return new LiteralFancyText(jsonElement.getAsString());
        } else if (jsonElement.isJsonObject()) {
            JsonElement textElement = jsonElement.getAsJsonObject()
                    .get("text");
            if (textElement != null && textElement.isJsonPrimitive()) {
                return new LiteralFancyText(textElement.getAsString());
            }
        }

        throw new JsonParseException("Failed to parse %s into fancy text".formatted(jsonElement.toString()));
    }
}
