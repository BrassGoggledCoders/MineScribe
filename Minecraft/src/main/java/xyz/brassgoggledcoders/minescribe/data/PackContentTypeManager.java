package xyz.brassgoggledcoders.minescribe.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import xyz.brassgoggledcoders.minescribe.connection.MineScribeNettyClient;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PackContentTypeManager extends SimpleJsonResourceReloadListener {
    public PackContentTypeManager() {
        super(new Gson(), "{}/types");
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        List<PackContentType> packContentTypes = new ArrayList<>(pObject.size());

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
                    packContentTypes.add(new PackContentType(
                            new ResourceId(resourceLocation.getNamespace(), resourceLocation.getPath()),
                            component.getString(),
                            path
                    ));
                }
            } else {
                throw new JsonParseException("Field 'name' is required");
            }
        }

        //MineScribeNettyClient.getInstance().sendToClient();
    }
}
