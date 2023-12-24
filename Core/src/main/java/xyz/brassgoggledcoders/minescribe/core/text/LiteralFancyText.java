package xyz.brassgoggledcoders.minescribe.core.text;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class LiteralFancyText extends FancyText {
    private final String text;

    public LiteralFancyText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", this.text);
        return jsonObject;
    }
}
