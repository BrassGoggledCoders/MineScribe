package xyz.brassgoggledcoders.minescribe.core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;

import java.util.Optional;

public record Range<T extends Number>(
        T min,
        T start,
        T max,
        T increment
) {

    public static final Codec<Range<Integer>> INT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf(JsonFieldNames.MIN).forGetter(range -> Optional.ofNullable(range.min())),
            Codec.INT.optionalFieldOf(JsonFieldNames.START).forGetter(range -> Optional.ofNullable(range.start())),
            Codec.INT.optionalFieldOf(JsonFieldNames.MAX).forGetter(range -> Optional.ofNullable(range.max())),
            Codec.INT.optionalFieldOf(JsonFieldNames.INCREMENT, 1).forGetter(Range::increment)
    ).apply(instance, (min, start, max, increment) -> new Range<>(min.orElse(0), start.orElse(min.orElse(0)), max.orElse(100000), increment)));

    public static final Codec<Range<Double>> DOUBLE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf(JsonFieldNames.MIN).forGetter(range -> Optional.ofNullable(range.min())),
            Codec.DOUBLE.optionalFieldOf(JsonFieldNames.START).forGetter(range -> Optional.ofNullable(range.start())),
            Codec.DOUBLE.optionalFieldOf(JsonFieldNames.MAX).forGetter(range -> Optional.ofNullable(range.max())),
            Codec.DOUBLE.optionalFieldOf(JsonFieldNames.INCREMENT, 0.1).forGetter(Range::increment)
    ).apply(instance, (min, start, max, increment) -> new Range<>(min.orElse(0D), start.orElse(min.orElse(0D)), max.orElse(100000D), increment)));
}
