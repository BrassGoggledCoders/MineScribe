package xyz.brassgoggledcoders.minescribe.api.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonBuilder {
    public static JsonObjectBuilder forObject() {
        return new JsonObjectBuilder(new JsonObject());
    }

    public record JsonObjectBuilder(JsonObject jsonObject) {
        public JsonObjectBuilder withProperty(String name, JsonElement jsonElement) {
            this.jsonObject.add(name, jsonElement);
            return this;
        }

        public JsonObjectBuilder withString(String name, String value) {
            return this.withProperty(name, new JsonPrimitive(value));
        }

        public JsonObject build() {
            return jsonObject;
        }

        public JsonObjectBuilder withInt(String name, int value) {
            return withProperty(name, new JsonPrimitive(value));
        }
    }

}
