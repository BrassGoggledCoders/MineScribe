package xyz.brassgoggledcoders.minescribe.core.packinfo.parent;

import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.codec.EnumCodec;

public enum RootType {
    PACK,
    PACK_TYPE,
    NAMESPACE,
    CONTENT;

    public static Codec<RootType> CODEC = new EnumCodec<>(RootType.class);
}
