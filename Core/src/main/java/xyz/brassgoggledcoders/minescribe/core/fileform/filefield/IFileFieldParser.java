package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public interface IFileFieldParser<T extends FileField> {
    @NotNull
    T fromJson(JsonObject jsonObject) throws JsonParseException;

    T fromNetwork(ByteBuf byteBuf);

    void toNetwork(ByteBuf byteBuf, T field);
}
