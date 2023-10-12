package xyz.brassgoggledcoders.minescribe.core.fileform.listhandler;

import java.util.Objects;

public class ListHandlerStore {
    private static IListHandler listHandler;

    public static void setListHandler(IListHandler listHandler) {
        ListHandlerStore.listHandler = listHandler;
    }

    public static IListHandler getListHandler() {
        return Objects.requireNonNull(ListHandlerStore.listHandler, "No List Handler registered");
    }
}
