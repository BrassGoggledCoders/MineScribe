package xyz.brassgoggledcoders.minescribe.data;

import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FileCopyMineScribeReloadListener extends MineScribeReloadListener<Map<ResourceLocation, String>, Map<ResourceLocation, String>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String pathSuffix;
    private final String packDirectory;

    public FileCopyMineScribeReloadListener(String packDirectory, String pathSuffix) {
        this.packDirectory = packDirectory;
        this.pathSuffix = pathSuffix;
    }

    protected Map<ResourceLocation, String> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        Map<ResourceLocation, String> files = new HashMap<>();

        for (Entry<ResourceLocation, Resource> entry : pResourceManager.listResources(this.packDirectory, this::isFile).entrySet()) {
            ResourceLocation fileResourceLocation = entry.getKey();

            try (BufferedReader reader = entry.getValue().openAsReader()) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line)
                            .append(System.lineSeparator());
                }
                files.put(fileResourceLocation, stringBuilder.toString());
            } catch (IllegalArgumentException | IOException | JsonParseException exception) {
                LOGGER.error("Couldn't parse data file {}", fileResourceLocation, exception);
            }
        }

        return files;
    }

    private boolean isFile(ResourceLocation resourceLocation) {
        return resourceLocation.getPath().endsWith(pathSuffix);
    }

    @Override
    protected Map<ResourceLocation, String> apply(Map<ResourceLocation, String> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        LOGGER.info("Loaded {} values for {}", pObject.size(), this.packDirectory);
        return pObject;
    }

    @Override
    protected void finalize(Map<ResourceLocation, String> files, MineScribeFileManager fileManager, ProfilerFiller profilerFiller) {
        for (Entry<ResourceLocation, String> entry : files.entrySet()) {

            fileManager.writeFile(
                    Path.of(entry.getKey().getNamespace())
                            .resolve(entry.getKey().getPath()),
                    entry.getValue()
            );
        }

    }
}
