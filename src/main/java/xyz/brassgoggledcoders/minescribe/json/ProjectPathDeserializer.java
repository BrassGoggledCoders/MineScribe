package xyz.brassgoggledcoders.minescribe.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import xyz.brassgoggledcoders.minescribe.model.ProjectPath;
import xyz.brassgoggledcoders.minescribe.model.ProjectPathAnchor;

import java.io.IOException;

public class ProjectPathDeserializer extends JsonDeserializer<ProjectPath> {
    @Override
    public ProjectPath deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonToken token = jsonParser.currentToken();
        if (token == JsonToken.VALUE_STRING) {
            return new ProjectPath(
                    ProjectPathAnchor.ROOT,
                    jsonParser.getText()
            );
        } else if (token == JsonToken.START_OBJECT) {
            token = jsonParser.nextToken();
            ProjectPathAnchor anchor = ProjectPathAnchor.ROOT;
            String path = null;
            while (token != JsonToken.END_OBJECT) {
                if (token == JsonToken.FIELD_NAME) {
                    String fieldName = jsonParser.currentName();
                    if (fieldName.equals("anchor")) {
                        jsonParser.nextToken();
                        anchor = ProjectPathAnchor.valueOf(jsonParser.getText());
                    } else if (fieldName.equals("path")) {
                        jsonParser.nextToken();
                        path = jsonParser.getText();
                    }
                    token = jsonParser.nextToken();
                }
            }

            if (path == null) {
                throw new JsonParseException(jsonParser, "No field 'path' found in JSON", jsonParser.currentLocation());
            }

            return new ProjectPath(
                    anchor,
                    path
            );
        } else {
            throw new JsonParseException(jsonParser, "Unexpected token " + token, jsonParser.currentLocation());
        }
    }
}
