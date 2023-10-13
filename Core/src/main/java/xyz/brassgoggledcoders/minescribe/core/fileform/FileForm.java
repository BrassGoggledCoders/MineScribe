package xyz.brassgoggledcoders.minescribe.core.fileform;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.util.MineScribeJsonHelper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class FileForm {
    private final TreeSet<FileField> fields;

    public FileForm(Collection<FileField> fields) {
        this.fields = new TreeSet<>(Comparable::compareTo);
        this.fields.addAll(fields);
    }

    public Collection<FileField> getFields() {
        return fields;
    }

    public static FileForm parseForm(JsonObject jsonObject) {
        JsonArray jsonArray = MineScribeJsonHelper.getAsJsonArray(jsonObject, JsonFieldNames.FIELDS);
        Set<FileField> fields = new HashSet<>();
        for (JsonElement jsonElement : jsonArray) {
            if (jsonElement.isJsonObject()) {
                FileField field = FileFieldTypeRegistry.getInstance().parseField(jsonElement.getAsJsonObject());
                fields.add(field);
            } else {
                throw new JsonParseException("All elements in 'fields' should be a Json Object");
            }
        }
        return new FileForm(fields);
    }
}
