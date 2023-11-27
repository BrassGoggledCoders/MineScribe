package xyz.brassgoggledcoders.minescribe.core.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;

public class RegistryCodec implements Codec<Registry<?, ?>> {
    @Override
    public <T> DataResult<Pair<Registry<?, ?>, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getStringValue(input)
                .flatMap(key -> Registries.getRegistryOpt(key)
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(("No Registry for name %s exists").formatted(key)))
                )
                .map(registry -> Pair.of(registry, input));
    }

    @Override
    public <T> DataResult<T> encode(Registry<?, ?> input, DynamicOps<T> ops, T prefix) {
        return DataResult.success(ops.createString(input.getName()));
    }
}
