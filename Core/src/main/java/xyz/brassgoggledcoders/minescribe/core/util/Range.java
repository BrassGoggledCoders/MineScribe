package xyz.brassgoggledcoders.minescribe.core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record Range<T extends Number>(
        T min,
        T start,
        T max
) {
    public static final Codec<Range<Integer>> INT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("min", 0).forGetter(Range::min),
            Codec.INT.optionalFieldOf("start").forGetter(range -> Optional.of(range.start())),
            Codec.INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(Range::max)
    ).apply(instance, (min, start, max) -> new Range<>(min, start.orElse(min), max)));

    public static final Codec<Range<Double>> DOUBLE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("min", 0D).forGetter(Range::min),
            Codec.DOUBLE.optionalFieldOf("start").forGetter(range -> Optional.of(range.start())),
            Codec.DOUBLE.optionalFieldOf("max", Double.MAX_VALUE).forGetter(Range::max)
    ).apply(instance, (min, start, max) -> new Range<>(min, start.orElse(min), max)));
}
