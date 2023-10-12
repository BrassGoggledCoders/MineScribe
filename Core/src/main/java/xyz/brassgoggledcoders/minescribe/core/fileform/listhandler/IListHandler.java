package xyz.brassgoggledcoders.minescribe.core.fileform.listhandler;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;

import java.util.List;
import java.util.UUID;

public interface IListHandler {
    UUID createAndStoreList(JsonElement listElement) throws JsonParseException;

    Pair<List<UUID>, List<String>> retrieveList(UUID id);
}
