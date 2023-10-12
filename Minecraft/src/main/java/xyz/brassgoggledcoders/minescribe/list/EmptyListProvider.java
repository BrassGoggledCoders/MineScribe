package xyz.brassgoggledcoders.minescribe.list;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.api.list.IListProvider;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class EmptyListProvider implements IListProvider {
    public static final ResourceLocation ID = MineScribe.rl("empty");
    public static final Codec<EmptyListProvider> CODEC = Codec.unit(EmptyListProvider::new);

    @Override
    public Pair<List<UUID>, List<String>> provideList(RegistryAccess registryAccess) {
        return Pair.of(Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public Codec<? extends IListProvider> codec() {
        return CODEC;
    }
}
