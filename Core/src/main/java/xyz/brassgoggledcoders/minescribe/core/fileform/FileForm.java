package xyz.brassgoggledcoders.minescribe.core.fileform;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.util.MineScribeJsonHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class FileForm {
    private final TreeMap<String, FileField> fields;

    public FileForm(Map<String, FileField> fields) {
        this.fields = new TreeMap<>(Comparable::compareTo);
        this.fields.putAll(fields);
    }

    public static FileForm parseForm(JsonObject jsonObject) {
        JsonArray jsonArray = MineScribeJsonHelper.getAsJsonArray(jsonObject, JsonFieldNames.FIELDS);
        Map<String, FileField> fields = new HashMap<>();
        for (JsonElement jsonElement : jsonArray) {
            if (jsonElement.isJsonObject()) {
                FileField field = FileFieldTypeRegistry.getInstance().parseField(jsonElement.getAsJsonObject());
                fields.put(field.getField(), field);
            } else {
                throw new JsonParseException("All elements in 'fields' should be a Json Object");
            }
        }
        return new FileForm(fields);
    }
}
