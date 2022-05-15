package xyz.brassgoggledcoders.minescribe.schema.creator;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.api.schema.creator.ISchemaCreator;
import xyz.brassgoggledcoders.minescribe.api.schema.root.IRootSchema;
import xyz.brassgoggledcoders.minescribe.schema.root.JsonRootSchema;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class CopyManualSchemaCreator implements ISchemaCreator {
    private static final int PATH_SUFFIX_LENGTH = ".json".length();
    private static final Gson GSON = new Gson();

    @Override
    public Map<ResourceLocation, JsonElement> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        Map<ResourceLocation, JsonElement> map = Maps.newHashMap();
        String directory = "minescribe/manual_schemas";
        int i = directory.length() + 1;

        for (ResourceLocation fileLocation : pResourceManager.listResources(directory, (fileName) -> fileName.endsWith(".json"))) {
            String s = fileLocation.getPath();
            ResourceLocation resourceLocation = new ResourceLocation(fileLocation.getNamespace(), s.substring(i, s.length() - PATH_SUFFIX_LENGTH));

            try (
                    Resource resource = pResourceManager.getResource(fileLocation);
                    InputStream inputstream = resource.getInputStream();
                    Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))
            ) {
                JsonElement jsonElement = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                if (jsonElement != null) {
                    JsonElement existingElement = map.put(resourceLocation, jsonElement);
                    if (existingElement != null) {
                        throw new IllegalStateException("Duplicate data file ignored with ID " + resourceLocation);
                    }
                } else {
                    MineScribe.LOGGER.error("Couldn't load data file {} from {} as it's null or empty", resourceLocation, fileLocation);
                }
            } catch (IOException e) {
                MineScribe.LOGGER.error("Couldn't parse data file {} from {}", resourceLocation, fileLocation, e);
            }
        }
        return map;
    }

    @Override
    public List<IRootSchema> apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return pObject.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isJsonObject())
                .<IRootSchema>map(entry -> new JsonRootSchema(entry.getKey(), entry.getValue().getAsJsonObject()))
                .toList();
    }
}
