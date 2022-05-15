package xyz.brassgoggledcoders.minescribe.schema.root;

import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import xyz.brassgoggledcoders.minescribe.api.schema.ISchema;

import java.util.Map;

public class RootSchemaBuilder {
    private final ResourceLocation id;
    private final Map<String, ISchema> definitions;
    private ISchema actualSchema;
    private String[] fileMatches;

    public RootSchemaBuilder(ResourceLocation id) {
        this.id = id;
        this.definitions = Maps.newHashMap();
    }

    public RootSchemaBuilder withDefinition(String name, ISchema definition) {
        this.definitions.put(name, definition);
        return this;
    }

    public RootSchemaBuilder withFileMatches(String... matches) {
        this.fileMatches = matches;
        return this;
    }

    public RootSchema build() {
        return new RootSchema(
                id,
                definitions,
                actualSchema,
                fileMatches == null ? new String[0] : fileMatches
        );
    }

    public RootSchemaBuilder withSchema(ISchema schema) {
        this.actualSchema = schema;
        return this;
    }

    public static RootSchemaBuilder builder(ResourceLocation id) {
        return new RootSchemaBuilder(id);
    }
}
