package xyz.brassgoggledcoders.minescribe.core.codec;

import com.mojang.serialization.Codec;

import java.nio.file.Path;

public class MineScribeCoreCodecs {
    public static final Codec<Path> PATH = Codec.STRING.xmap(
            Path::of,
            Path::toString
    );
}
