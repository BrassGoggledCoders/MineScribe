package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.netty.buffer.ByteBuf;

public class ListSelectionFileFieldParser implements IFileFieldParser<ListSelectionFileField> {
    @Override
    public ListSelectionFileField fromJson(JsonObject jsonObject) throws JsonParseException {
        return null;
    }

    @Override
    public ListSelectionFileField fromNetwork(ByteBuf byteBuf) {
        return null;
    }

    @Override
    public void toNetwork(ByteBuf byteBuf, ListSelectionFileField field) {

    }
}
