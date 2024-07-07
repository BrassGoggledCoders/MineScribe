package xyz.brassgoggledcoders.minescribe.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import org.springframework.beans.factory.ObjectProvider;
import xyz.brassgoggledcoders.minescribe.registry.Registry;
import xyz.brassgoggledcoders.minescribe.registry.RegistryHolder;
import xyz.brassgoggledcoders.minescribe.registry.RegistryId;

import java.io.IOException;
import java.util.List;

public class RegistryHolderDeserializer extends JsonDeserializer<RegistryHolder<?>> implements ContextualDeserializer {
    private ObjectProvider<List<Registry<?>>> registries;
    private Registry<?> registry = null;

    public void setRegistries(ObjectProvider<List<Registry<?>>> registries) {
        this.registries = registries;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        if (this.registries != null) {
            JavaType type = beanProperty.getType();
            Class<?> registryClass = null;
            while (type.containedTypeCount() > 0 && registryClass == null) {
                type = type.containedType(0);
                if (type.getRawClass() == RegistryHolder.class) {
                    registryClass = type.containedType(0)
                            .getRawClass();
                }
            }

            if (registryClass != null) {
                for (Registry<?> registry : registries.getObject()) {
                    if (registry.getValueClass() == registryClass) {
                        this.registry = registry;
                        return this;
                    }
                }
            }

            throw JsonMappingException.from(deserializationContext, "No registry found for " + type);
        } else {
            throw JsonMappingException.from(deserializationContext, "No registries found");
        }
    }

    @Override
    public RegistryHolder<?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonToken currentToken = jsonParser.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return this.registry.getValue(new RegistryId(jsonParser.getText()));
        } else {
            throw new JsonParseException(jsonParser, "Unexpected token " + jsonParser.currentToken());
        }
    }
}
