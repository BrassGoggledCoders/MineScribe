package xyz.brassgoggledcoders.minescribe.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ServerConfig {

    public ForgeConfigSpec.IntValue maxStack;

    public ServerConfig(ForgeConfigSpec.Builder builder) {
        maxStack = builder.defineInRange("debug.max_stack", 10, 1, Integer.MAX_VALUE);
    }

    public static Pair<ServerConfig, ForgeConfigSpec> setup() {
        return new ForgeConfigSpec.Builder().configure(ServerConfig::new);
    }
}
