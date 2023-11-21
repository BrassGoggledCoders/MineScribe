package xyz.brassgoggledcoders.minescribe.api.data;

import com.mojang.serialization.JsonOps;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import xyz.brassgoggledcoders.minescribe.api.MineScribeAPI;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackRepositoryLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MineScribeProviders {
    public static JsonCodecProvider<PackRepositoryLocation> createLocationProvider(
            DataGenerator dataGenerator,
            ExistingFileHelper existingFileHelper,
            String modid,
            Consumer<BiConsumer<ResourceLocation, PackRepositoryLocation>> collectValues
    ) {
        Map<ResourceLocation, PackRepositoryLocation> values = new HashMap<>();
        collectValues.accept(values::put);
        return new JsonCodecProvider<>(
                dataGenerator,
                existingFileHelper,
                modid,
                JsonOps.INSTANCE,
                MineScribeAPI.PACK_TYPE,
                "pack_repositories",
                PackRepositoryLocation.CODEC,
                values
        );
    }
}
