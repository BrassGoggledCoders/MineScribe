package xyz.brassgoggledcoders.minescribe.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import xyz.brassgoggledcoders.minescribe.model.component.ITextContent;
import xyz.brassgoggledcoders.minescribe.model.component.LiteralTextContent;
import xyz.brassgoggledcoders.minescribe.model.component.TextComponent;
import xyz.brassgoggledcoders.minescribe.service.LocalizationService;

import java.io.IOException;

public class TextComponentDeserializer extends JsonDeserializer<TextComponent> {
    private final LocalizationService localizationService;

    public TextComponentDeserializer(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    @Override
    public TextComponent deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ITextContent textContent = null;

        JsonToken token = jsonParser.currentToken();
        if (token == JsonToken.VALUE_STRING) {
            textContent = new LiteralTextContent(jsonParser.getText());
        } else if (token == JsonToken.START_OBJECT) {
            token = jsonParser.nextToken();
            while (token != JsonToken.END_OBJECT) {
                if (token == JsonToken.FIELD_NAME) {
                    String fieldName = jsonParser.currentName();
                    if (fieldName.equals("text")) {
                        jsonParser.nextToken();
                        textContent = new LiteralTextContent(jsonParser.getText());
                    } else if (fieldName.equals("translate")) {
                        jsonParser.nextToken();
                        textContent = new LiteralTextContent(jsonParser.getText());
                    }
                    token = jsonParser.nextToken();
                }
            }
        }

        if (textContent != null) {
            return new TextComponent(textContent);
        } else {
            throw new JsonParseException(jsonParser, "Did not found 'text' or 'translation' fields");
        }
    }
}
