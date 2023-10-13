package xyz.brassgoggledcoders.minescribe.core.packinfo;

import io.netty.buffer.ByteBuf;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyUtil;

import java.nio.file.Path;

public record PackTypeInfo(
        String label,
        String name,
        Path folder,
        int version,
        String versionKey
) {

    @Override
    public String toString() {
        return "%s (./%s)".formatted(toTitleCase(this.label()), folder.toString());
    }

    public void encode(ByteBuf byteBuf) {
        NettyUtil.writeUtf(byteBuf, this.label());
        NettyUtil.writeUtf(byteBuf, this.name());
        NettyUtil.writeUtf(byteBuf, this.folder().toString());
        byteBuf.writeInt(this.version());
        NettyUtil.writeUtf(byteBuf, this.versionKey());
    }

    public static PackTypeInfo decode(ByteBuf byteBuf) {
        return new PackTypeInfo(
                NettyUtil.readUtf(byteBuf),
                NettyUtil.readUtf(byteBuf),
                Path.of(NettyUtil.readUtf(byteBuf)),
                byteBuf.readInt(),
                NettyUtil.readUtf(byteBuf)
        );
    }

    public static String toTitleCase(String phrase) {

        // convert the string to an array
        char[] phraseChars = phrase.toCharArray();
        if (phraseChars.length > 0) {
            phraseChars[0] = Character.toTitleCase(phraseChars[0]);
        }

        for (int i = 0; i < phraseChars.length - 1; i++) {
            if(Character.isWhitespace(phraseChars[i])) {
                phraseChars[i+1] = Character.toUpperCase(phraseChars[i+1]);
            }
        }

        // convert the array to string
        return String.valueOf(phraseChars);
    }
}
