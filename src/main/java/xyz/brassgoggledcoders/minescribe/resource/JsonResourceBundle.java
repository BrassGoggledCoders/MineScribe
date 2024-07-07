package xyz.brassgoggledcoders.minescribe.resource;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

public class JsonResourceBundle extends ListResourceBundle {
    private final JsonNode jsonNode;

    public JsonResourceBundle(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    @Override
    protected Object[][] getContents() {
        return this.jsonNode.properties()
                .stream()
                .map(entry -> new Object[]{
                        entry.getKey(),
                        fromJson(entry.getValue())
                })
                .toArray(Object[][]::new);
    }

    private Object fromJson(JsonNode jsonNode) {
        return switch (jsonNode.getNodeType()) {
            case OBJECT:
                Map<String, Object> map = new HashMap<>();
                for (Map.Entry<String, JsonNode> entry : jsonNode.properties()) {
                    map.put(entry.getKey(), fromJson(entry.getValue()));
                }
                yield map;
            case ARRAY:
                List<Object> list = new ArrayList<>();
                for (JsonNode nodes : jsonNode) {
                    list.add(fromJson(nodes));
                }
                yield list;
            case BOOLEAN:
                yield jsonNode.asBoolean();
            case NUMBER:
                yield jsonNode.asLong();
            case STRING:
                yield jsonNode.asText();
            case NULL:
                yield null;
            default:
                throw new IllegalArgumentException("Unexpected node type: " + jsonNode.getNodeType());
        };
    }
}
