package xyz.brassgoggledcoders.minescribe.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.model.component.ITextContent;
import xyz.brassgoggledcoders.minescribe.model.component.LiteralTextContent;
import xyz.brassgoggledcoders.minescribe.model.component.TextComponent;
import xyz.brassgoggledcoders.minescribe.service.LocalizationService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextComponentDeserializer extends JsonDeserializer<TextComponent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextComponentDeserializer.class);

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
                    } else {
                        jsonParser.nextToken();
                        LOGGER.warn("Ignoring Property {} {}", fieldName, jsonParser.currentToken());
                    }
                    token = jsonParser.nextToken();
                } else {
                    jsonParser.nextToken();
                }
            }
        } else if (token == JsonToken.START_ARRAY) {
            token = jsonParser.nextToken();
            List<TextComponent> components = new ArrayList<>();
            while (token != JsonToken.END_ARRAY) {
                components.add(this.deserialize(jsonParser, deserializationContext));
                token = jsonParser.nextToken();
            }

            if (!components.isEmpty()) {
                return components.getFirst();
            }
        }

        if (textContent != null) {
            return new TextComponent(textContent);
        } else {
            throw new JsonParseException(jsonParser, "Did not found 'text' or 'translation' fields");
        }
    }
}
