package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Function3;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyUtil;
import xyz.brassgoggledcoders.minescribe.core.util.MineScribeJsonHelper;

public class BasicFileFieldParser<T extends FileField> implements IFileFieldParser<T> {
    private final Function3<String, String, Integer, T> creator;

    public BasicFileFieldParser(Function3<String, String, Integer, T> creator) {
        this.creator = creator;
    }

    @Override
    @NotNull
    public T fromJson(JsonObject jsonObject) throws JsonParseException {
        return creator.apply(
                MineScribeJsonHelper.getAsString(jsonObject, JsonFieldNames.LABEL),
                MineScribeJsonHelper.getAsString(jsonObject, JsonFieldNames.FIELD),
                MineScribeJsonHelper.getAsInt(jsonObject, JsonFieldNames.SORT_ORDER, 0)
        );
    }

    @Override
    public T fromNetwork(ByteBuf byteBuf) {
        return creator.apply(
                NettyUtil.readUtf(byteBuf),
                NettyUtil.readUtf(byteBuf),
                byteBuf.readInt()
        );
    }

    @Override
    public void toNetwork(ByteBuf byteBuf, T field) {
        NettyUtil.writeUtf(byteBuf, field.getLabel());
        NettyUtil.writeUtf(byteBuf, field.getField());
        byteBuf.writeInt(field.getSortOrder());
    }
}
