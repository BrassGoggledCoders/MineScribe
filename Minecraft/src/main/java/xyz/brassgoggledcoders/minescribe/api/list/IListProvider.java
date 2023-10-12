package xyz.brassgoggledcoders.minescribe.api.list;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.core.RegistryAccess;

import java.util.List;
import java.util.UUID;

public interface IListProvider {
    Pair<List<UUID>, List<String>> provideList(RegistryAccess registryAccess);

    Codec<? extends IListProvider> codec();
}
