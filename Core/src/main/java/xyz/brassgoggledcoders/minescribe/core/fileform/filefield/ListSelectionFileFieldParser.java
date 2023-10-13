package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.fileform.listhandler.ListHandlerStore;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyUtil;
import xyz.brassgoggledcoders.minescribe.core.util.MineScribeJsonHelper;

public class ListSelectionFileFieldParser implements IFileFieldParser<ListSelectionFileField> {
    @Override
    @NotNull
    public ListSelectionFileField fromJson(JsonObject jsonObject) throws JsonParseException {
        return new ListSelectionFileField(
                MineScribeJsonHelper.getAsString(jsonObject, JsonFieldNames.LABEL),
                MineScribeJsonHelper.getAsString(jsonObject, JsonFieldNames.FIELD),
                MineScribeJsonHelper.getAsInt(jsonObject, JsonFieldNames.SORT_ORDER, 0),
                ListHandlerStore.getListHandler().createAndStoreList(jsonObject.get(JsonFieldNames.LIST))
        );
    }

    @Override
    public ListSelectionFileField fromNetwork(ByteBuf byteBuf) {
        return new ListSelectionFileField(
                NettyUtil.readUtf(byteBuf),
                NettyUtil.readUtf(byteBuf),
                byteBuf.readInt(),
                NettyUtil.readUUID(byteBuf)
        );
    }

    @Override
    public void toNetwork(ByteBuf byteBuf, ListSelectionFileField field) {
        NettyUtil.writeUtf(byteBuf, field.getLabel());
        NettyUtil.writeUtf(byteBuf, field.getField());
        byteBuf.writeInt(field.getSortOrder());
        NettyUtil.writeUUID(byteBuf, field.getListId());
    }
}
