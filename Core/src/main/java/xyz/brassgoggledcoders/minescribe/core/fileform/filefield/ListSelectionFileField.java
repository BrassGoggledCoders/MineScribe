package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.fileform.listhandler.ListHandlerStore;
import xyz.brassgoggledcoders.minescribe.core.util.MineScribeJsonHelper;

import java.util.UUID;

public class ListSelectionFileField extends FileField {
    public static final ListSelectionFileFieldParser PARSER = new ListSelectionFileFieldParser();
    private final UUID listId;
    public ListSelectionFileField(String name, String field, int sortOrder, UUID listId) {
        super(name, field, sortOrder);
        this.listId = listId;
    }

    public UUID getListId() {
        return this.listId;
    }

    @Override
    public IFileFieldParser<?> getParser() {
        return PARSER;
    }
}
