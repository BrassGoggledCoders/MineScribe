package xyz.brassgoggledcoders.minescribe.editor.javascript;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class MineScribeJSFieldHelper {
    public MineScribeJSField<String> ofString(String fieldName, String defaultValue) {
        return new MineScribeJSField<>(
                fieldName,
                defaultValue,
                JsonElement::getAsString
        );
    }

    public MineScribeJSField<List<String>> ofStringList(String fieldName, String[] defaultValue) {
        return new MineScribeJSField<>(
                fieldName,
                defaultValue != null ? Arrays.asList(defaultValue) : Collections.emptyList(),
                jsonElement -> {
                    List<String> strings = new ArrayList<>();
                    if (jsonElement.isJsonArray()) {
                        for (JsonElement arrayElement : jsonElement.getAsJsonArray()) {
                            strings.add(arrayElement.getAsString());
                        }
                    } else if (jsonElement.isJsonPrimitive()) {
                        strings.add(jsonElement.getAsString());
                    }
                    return strings;
                }
        );
    }

    public MineScribeJSField<Integer> ofInt(String fieldName, Integer defaultValue) {
        return new MineScribeJSField<>(
                fieldName,
                defaultValue,
                JsonElement::getAsInt
        );
    }
}
