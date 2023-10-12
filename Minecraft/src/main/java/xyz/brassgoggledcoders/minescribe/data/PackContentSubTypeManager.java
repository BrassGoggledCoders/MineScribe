package xyz.brassgoggledcoders.minescribe.data;

import com.google.common.base.Suppliers;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.connection.MineScribeNettyClient;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.netty.packet.PackContentSubTypeLoadPacket;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PackContentSubTypeManager extends SimpleJsonResourceReloadListener {
    private final Supplier<String> PACK_TYPE_STRING = Suppliers.memoize(() -> Arrays.stream(PackType.values())
            .map(PackType::name)
            .collect(Collectors.joining(" , "))
    );

    public PackContentSubTypeManager() {
        super(new Gson(), "subtypes");
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        Multimap<ResourceId, PackContentType> packContentTypes = Multimaps.newMultimap(new HashMap<>(), HashSet::new);

        for (Entry<ResourceLocation, JsonElement> entry : pObject.entrySet()) {
            JsonObject entryObject = GsonHelper.convertToJsonObject(entry.getValue(), "top element");
            ResourceLocation resourceLocation = entry.getKey();
            try {
                if (entryObject.has("name")) {
                    Component component = Component.Serializer.fromJson(entryObject.get("name"));
                    if (component != null) {
                        Path path = Path.of(GsonHelper.getAsString(entryObject, "path"));
                        if (path.isAbsolute()) {
                            throw new JsonParseException("Field 'path' should be a relative file path");
                        }
                        String packType = GsonHelper.getAsString(entryObject, "packType", PackType.SERVER_DATA.name());
                        checkPackType(packType);
                        FileForm fileForm = null;
                        if (entryObject.has("form")) {
                            fileForm = FileForm.parseForm(GsonHelper.getAsJsonObject(entryObject, "form"));
                        }
                        String parent = GsonHelper.getAsString(entryObject, "parent");
                        ResourceLocation location = ResourceLocation.tryParse(parent);
                        if (location == null) {
                            throw new JsonParseException(parent + " is an invalid Resource Location");
                        }
                        packContentTypes.put(
                                new ResourceId(location.getNamespace(), location.getPath()),
                                new PackContentType(
                                        new ResourceId(resourceLocation.getNamespace(), resourceLocation.getPath()),
                                        component.getString(),
                                        packType,
                                        path,
                                        Optional.ofNullable(fileForm)
                                )
                        );
                    }
                } else {
                    throw new JsonParseException("Field 'name' is required");
                }
            } catch (JsonParseException jsonParseException) {
                MineScribe.LOGGER.error("Failed to load {}, due to {}", entry.getKey(), jsonParseException.getMessage());
            }

        }

        MineScribe.LOGGER.info("Loaded {} MineScribe Pack Content SubTypes", packContentTypes.size());

        MineScribeNettyClient.getInstance()
                .sendToClient(new PackContentSubTypeLoadPacket(packContentTypes.asMap()));
    }

    private void checkPackType(String name) {
        boolean found = false;
        for (PackType packType : PackType.values()) {
            if (packType.name().equalsIgnoreCase(name)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new JsonParseException("Invalid value for 'packType', valid options are: " + PACK_TYPE_STRING.get());
        }
    }
}
