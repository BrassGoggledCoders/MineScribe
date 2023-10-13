package xyz.brassgoggledcoders.minescribe.list;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.api.list.IListProvider;
import xyz.brassgoggledcoders.minescribe.codec.MineScribeCodecs;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record ListListProvider(
        List<UUID> lists
) implements IListProvider {
    public static final ResourceLocation ID = MineScribe.rl("lists");
    public static final Codec<ListListProvider> CODEC = ExtraCodecs.lazyInitializedCodec(
            () -> RecordCodecBuilder.create(instance -> instance.group(
                    MineScribeCodecs.LIST_ID.listOf().fieldOf("lists").forGetter(ListListProvider::lists)
            ).apply(instance, ListListProvider::new))
    );

    @Override
    public Pair<List<UUID>, List<String>> provideList(RegistryAccess registryAccess) {
        return Pair.of(lists, Collections.emptyList());
    }

    @Override
    public Codec<? extends IListProvider> codec() {
        return CODEC;
    }
}
