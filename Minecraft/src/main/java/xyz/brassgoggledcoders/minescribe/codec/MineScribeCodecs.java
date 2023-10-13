package xyz.brassgoggledcoders.minescribe.codec;

import com.google.gson.JsonNull;
import com.mojang.serialization.Codec;
import net.minecraft.server.packs.PackType;
import xyz.brassgoggledcoders.minescribe.core.fileform.listhandler.ListHandlerStore;
import xyz.brassgoggledcoders.minescribe.list.GsonListHandler;

import java.util.UUID;

public class MineScribeCodecs {
    public static Codec<UUID> LIST_ID = new JsonCodec<>(
            jsonElement -> ListHandlerStore.getListHandler().createAndStoreList(jsonElement),
            id -> {
                if (ListHandlerStore.getListHandler() instanceof GsonListHandler gsonListHandler) {
                    return gsonListHandler.getElementFor(id);
                } else {
                    return JsonNull.INSTANCE;
                }
            }
    );
}
