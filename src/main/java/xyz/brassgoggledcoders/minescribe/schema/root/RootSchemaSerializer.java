package xyz.brassgoggledcoders.minescribe.schema.root;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;

public class RootSchemaSerializer implements JsonSerializer<RootSchema> {
    @Override
    public JsonElement serialize(RootSchema src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject schemaObject = GsonHelper.convertToJsonObject(context.serialize(src.actualSchema()), "sub-schema");

        schemaObject.addProperty("$schema", "https://json-schema.org/draft/2020-12/schema");
        schemaObject.addProperty("$id", "file://" + src.getPath());

        return schemaObject;
    }
}
