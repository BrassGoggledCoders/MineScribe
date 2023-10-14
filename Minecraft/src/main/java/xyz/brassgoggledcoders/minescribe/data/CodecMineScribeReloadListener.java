package xyz.brassgoggledcoders.minescribe.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class CodecMineScribeReloadListener<U> extends MineScribeReloadListener<Map<ResourceLocation, JsonElement>, Map<ResourceLocation, U>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String PATH_SUFFIX = ".json";
    private static final int PATH_SUFFIX_LENGTH = ".json".length();
    private final Gson gson;
    private final String directory;
    private final Codec<U> prepareCodec;
    private final Codec<U> finalizeCodec;

    public CodecMineScribeReloadListener(String directory, Codec<U> prepareCodec) {
        this(directory, prepareCodec, prepareCodec);
    }

    public CodecMineScribeReloadListener(String directory, Codec<U> prepareCodec, Codec<U> finalizeCodec) {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        this.directory = directory;
        this.prepareCodec = prepareCodec;
        this.finalizeCodec = finalizeCodec;
    }

    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        Map<ResourceLocation, JsonElement> map = Maps.newHashMap();
        int i = this.directory.length() + 1;

        for (Entry<ResourceLocation, Resource> entry : pResourceManager.listResources(this.directory, this::isJson).entrySet()) {
            ResourceLocation fileResourceLocation = entry.getKey();
            String path = fileResourceLocation.getPath();
            ResourceLocation resourceLocation = new ResourceLocation(
                    fileResourceLocation.getNamespace(),
                    path.substring(i, path.length() - PATH_SUFFIX_LENGTH)
            );

            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement jsonelement = GsonHelper.fromJson(this.gson, reader, JsonElement.class);
                if (jsonelement != null) {
                    if (map.put(resourceLocation, jsonelement) != null) {
                        throw new IllegalStateException("Duplicate data file ignored with ID " + resourceLocation);
                    }
                } else {
                    LOGGER.error("Couldn't load data file {} from {} as it's null or empty", resourceLocation, fileResourceLocation);
                }
            } catch (IllegalArgumentException | IOException | JsonParseException exception) {
                LOGGER.error("Couldn't parse data file {} from {}", resourceLocation, fileResourceLocation, exception);
            }
        }

        return map;
    }

    private boolean isJson(ResourceLocation resourceLocation) {
        return resourceLocation.getPath().endsWith(PATH_SUFFIX);
    }

    @Override
    protected Map<ResourceLocation, U> apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        final Map<ResourceLocation, U> values = pObject.entrySet()
                .stream()
                .flatMap(jsonEntry -> this.prepareCodec.decode(JsonOps.INSTANCE, jsonEntry.getValue())
                        .get()
                        .ifRight(partial -> LOGGER.error("Failed to decode {}, due to {}", jsonEntry.getKey(), partial.message()))
                        .mapLeft(result -> Pair.of(jsonEntry.getKey(), result.getFirst()))
                        .left()
                        .stream()
                )
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        LOGGER.info("Loaded {} values for {}", values.size(), this.directory);
        return values;
    }

    @Override
    protected void finalize(Map<ResourceLocation, U> pObject, MineScribeFileManager fileManager, ProfilerFiller profilerFiller) {
        for (Entry<ResourceLocation, U> entry: pObject.entrySet()) {
            Path writePath = Path.of(entry.getKey().getNamespace(), directory.split("/"));
            fileManager.writeFile(writePath.resolve(entry.getKey().getPath()), this.finalizeCodec, entry.getValue());
        }
    }
}
