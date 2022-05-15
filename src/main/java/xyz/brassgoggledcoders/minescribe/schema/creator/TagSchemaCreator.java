package xyz.brassgoggledcoders.minescribe.schema.creator;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import xyz.brassgoggledcoders.minescribe.api.schema.ISchema;
import xyz.brassgoggledcoders.minescribe.api.schema.creator.ISchemaCreator;
import xyz.brassgoggledcoders.minescribe.schema.root.RootSchema;

import java.util.List;
import java.util.Map;

public class TagSchemaCreator implements ISchemaCreator {
    @Override
    public Map<ResourceLocation, JsonElement> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return null;
    }

    @Override
    public List<RootSchema> apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return null;
    }
}
