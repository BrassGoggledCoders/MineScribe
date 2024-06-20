package xyz.brassgoggledcoders.minescribe;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.MineScribeRuntime;

@Mod(MineScribe.ID)
public class MineScribe {
    public static final String ID = "minescribe";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    public MineScribe() {
        MineScribeRuntime.setRuntime(MineScribeRuntime.MINECRAFT);
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(ID, path);
    }
}
