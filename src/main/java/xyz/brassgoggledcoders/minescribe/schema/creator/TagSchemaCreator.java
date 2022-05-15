package xyz.brassgoggledcoders.minescribe.schema.creator;

import com.google.gson.JsonElement;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import xyz.brassgoggledcoders.minescribe.api.schema.ISchema;
import xyz.brassgoggledcoders.minescribe.api.schema.creator.ISchemaCreator;
import xyz.brassgoggledcoders.minescribe.api.schema.root.IRootSchema;
import xyz.brassgoggledcoders.minescribe.schema.root.RootSchema;
import xyz.brassgoggledcoders.minescribe.schema.root.RootSchemaBuilder;
import xyz.brassgoggledcoders.minescribe.schema.subschema.StringEnumSchema;
import xyz.brassgoggledcoders.minescribe.util.ResourceLocationHelper;

import java.util.List;
import java.util.Map;

public class TagSchemaCreator implements ISchemaCreator {
    @Override
    public List<IRootSchema> apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return Registry.REGISTRY.entrySet()
                .stream()
                .<IRootSchema>map(entry -> {
                    ResourceLocation location = entry.getKey().location();
                    return RootSchemaBuilder.builder(
                                    ResourceLocationHelper.prependPath(location, "definition/tag")
                            )
                            .withSchema(StringEnumSchema.of(entry.getValue().keySet()))
                            .build();
                })
                .toList();
    }
}
