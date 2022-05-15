package xyz.brassgoggledcoders.minescribe.schema.root;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;

public class JsonRootSchemaSerializer implements JsonSerializer<JsonRootSchema> {
    @Override
    public JsonElement serialize(JsonRootSchema src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = src.jsonObject();
        if (jsonObject.has("$id")) {
            String id = GsonHelper.getAsString(jsonObject, "$id");
            if (id.startsWith("minescribe://")) {
                jsonObject.addProperty("$id", "file://" + src.getPath());
            }
        }
        return jsonObject;
    }
}
