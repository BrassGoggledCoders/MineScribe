package xyz.brassgoggledcoders.minescribe.data;

import com.google.common.base.Suppliers;
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
import xyz.brassgoggledcoders.minescribe.core.netty.packet.PackContentTypeLoadPacket;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PackContentTypeManager extends SimpleJsonResourceReloadListener {
    private final Supplier<String> PACK_TYPE_STRING = Suppliers.memoize(() -> Arrays.stream(PackType.values())
            .map(PackType::name)
            .collect(Collectors.joining(" , "))
    );

    public PackContentTypeManager() {
        super(new Gson(), "types");
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        List<PackContentType> packContentTypes = new ArrayList<>();

        for (Entry<ResourceLocation, JsonElement> entry : pObject.entrySet()) {
            JsonObject entryObject = GsonHelper.convertToJsonObject(entry.getValue(), "top element");
            ResourceLocation resourceLocation = entry.getKey();
            if (entryObject.has("name")) {
                Component component = Component.Serializer.fromJson(entryObject);
                if (component != null) {
                    Path path = Path.of(GsonHelper.getAsString(entryObject, "path"));
                    if (path.isAbsolute()) {
                        throw new JsonParseException("Field 'path' should be a relative file path");
                    }
                    String packType = GsonHelper.getAsString(entryObject, "packType", PackType.SERVER_DATA.name());
                    checkPackType(packType);
                    packContentTypes.add(new PackContentType(
                            new ResourceId(resourceLocation.getNamespace(), resourceLocation.getPath()),
                            component.getString(),
                            packType,
                            path
                    ));
                }
            } else {
                throw new JsonParseException("Field 'name' is required");
            }
        }

        MineScribe.LOGGER.info("Loaded {} MineScribe Pack Content Types", packContentTypes.size());

        MineScribeNettyClient.getInstance()
                .sendToClient(new PackContentTypeLoadPacket(packContentTypes));
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
