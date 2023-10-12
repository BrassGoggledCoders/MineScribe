package xyz.brassgoggledcoders.minescribe.list;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.api.list.IListProvider;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record RegistryListProvider(
        ResourceKey<Registry<?>> registry
) implements IListProvider {
    public static final ResourceLocation ID = MineScribe.rl("registry");
    public static final Codec<RegistryListProvider> CODEC = ExtraCodecs.lazyInitializedCodec(() -> RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(ResourceKey.<Registry<?>>createRegistryKey(Registry.ROOT_REGISTRY_NAME))
                    .fieldOf("registry")
                    .forGetter(RegistryListProvider::registry)
    ).apply(instance, RegistryListProvider::new)));

    @Override
    public Pair<List<UUID>, List<String>> provideList(RegistryAccess registryAccess) {
        return Pair.of(
                Collections.emptyList(),
                registryAccess.registry(registry)
                        .map(Registry::registryKeySet)
                        .stream()
                        .flatMap(Set::stream)
                        .map(ResourceKey::location)
                        .map(ResourceLocation::toString)
                        .toList()
        );
    }

    @Override
    public Codec<? extends IListProvider> codec() {
        return CODEC;
    }
}
