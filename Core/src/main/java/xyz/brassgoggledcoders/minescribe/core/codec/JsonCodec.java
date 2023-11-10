package xyz.brassgoggledcoders.minescribe.core.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;

import java.util.function.BiFunction;
import java.util.function.Function;

public class JsonCodec<T> implements Codec<T> {
    private final BiFunction<JsonElement, Codec<T>, T> fromJson;
    private final Function<T, JsonElement> toJson;

    public JsonCodec(Function<JsonElement, T> fromJson, Function<T, JsonElement> toJson) {
        this(
                (json, codec) -> fromJson.apply(json),
                toJson
        );
    }

    public JsonCodec(BiFunction<JsonElement, Codec<T>, T> fromJson, Function<T, JsonElement> toJson) {
        this.fromJson = fromJson;
        this.toJson = toJson;
    }

    @Override
    public <U> DataResult<Pair<T, U>> decode(DynamicOps<U> ops, U input) {
        JsonElement element = ops.convertTo(JsonOps.INSTANCE, input);

        try {
            return DataResult.success(Pair.of(fromJson.apply(element, this), input));
        } catch (JsonSyntaxException e) {
            return DataResult.error(e.getMessage());
        }
    }

    @Override
    public <U> DataResult<U> encode(T input, DynamicOps<U> ops, U prefix) {
        JsonElement jsonElement = toJson.apply(input);
        if (jsonElement.isJsonNull()) {
            return DataResult.error("Found Null");
        } else {
            return DataResult.success(JsonOps.INSTANCE.convertTo(ops, jsonElement));
        }
    }
}
