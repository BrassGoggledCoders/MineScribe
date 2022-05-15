package xyz.brassgoggledcoders.minescribe.api.schema.creator;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import xyz.brassgoggledcoders.minescribe.api.json.IPreparedGatherer;
import xyz.brassgoggledcoders.minescribe.api.schema.root.IRootSchema;
import xyz.brassgoggledcoders.minescribe.schema.root.RootSchema;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface ISchemaCreator extends IPreparedGatherer<Map<ResourceLocation, JsonElement>, List<IRootSchema>> {
    @Override
    default Map<ResourceLocation, JsonElement> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return Collections.emptyMap();
    }
}
