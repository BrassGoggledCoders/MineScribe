package xyz.brassgoggledcoders.minescribe.api.schema.creator;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import xyz.brassgoggledcoders.minescribe.api.json.IPreparedGatherer;
import xyz.brassgoggledcoders.minescribe.schema.root.RootSchema;

import java.util.List;
import java.util.Map;

public interface ISchemaCreator extends IPreparedGatherer<Map<ResourceLocation, JsonElement>, List<RootSchema>> {
    @Override
    default Map<ResourceLocation, JsonElement> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return Maps.newHashMap();
    }
}
