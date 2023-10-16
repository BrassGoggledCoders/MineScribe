package xyz.brassgoggledcoders.minescribe;


import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MineScribe.ID)
public class MineScribe {
    public static final String ID = "minescribe";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    public MineScribe() {

    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(ID, path);
    }
}
