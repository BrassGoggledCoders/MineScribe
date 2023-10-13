package xyz.brassgoggledcoders.minescribe.editor.list;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import xyz.brassgoggledcoders.minescribe.core.fileform.listhandler.IListHandler;
import xyz.brassgoggledcoders.minescribe.core.netty.packet.ListValueRequest;
import xyz.brassgoggledcoders.minescribe.core.netty.packet.ListValueResponse;
import xyz.brassgoggledcoders.minescribe.editor.server.MineScribeNettyServer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EditorListHandler implements IListHandler {
    private final Map<UUID, ObservableList<String>> lists = new HashMap<>();
    private final Map<UUID, Set<UUID>> waitingLists = new ConcurrentHashMap<>();

    @Override
    public UUID createAndStoreList(JsonElement listElement) throws JsonParseException {
        throw new UnsupportedOperationException("Editor does not contain List Providers");
    }

    @Override
    public Pair<List<UUID>, List<String>> retrieveList(UUID id) {
        ObservableList<String> values = lists.computeIfAbsent(id, listKey -> {
            ObservableList<String> newList = FXCollections.observableList(new ArrayList<>());
            waitingLists.computeIfAbsent(id, waitingKey -> new HashSet<>()).add(id);
            MineScribeNettyServer.getInstance()
                    .sendToClient(new ListValueRequest(id));
            return newList;
        });
        return Pair.of(Collections.emptyList(), values);
    }

    public void handleListResponse(ListValueResponse listValueResponse) {
        UUID owningList = listValueResponse.requestedId();
        List<String> values = listValueResponse.values();
        Set<UUID> dependents = waitingLists.remove(owningList);
        if (dependents != null) {
            if (!values.isEmpty()) {
                for (UUID uuid : dependents) {
                    ObservableList<String> existingList = this.lists.computeIfAbsent(uuid, key -> FXCollections.observableList(new ArrayList<>()));
                    existingList.addAll(values);
                }
            }
        }
        for (UUID childList : listValueResponse.additionalLists()) {
            this.waitingLists.computeIfAbsent(childList, key -> new HashSet<>()).add(owningList);
            MineScribeNettyServer.getInstance()
                    .sendToClient(new ListValueRequest(childList));
        }

    }
}
