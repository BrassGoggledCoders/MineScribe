package xyz.brassgoggledcoders.minescribe.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.packs.resources.ReloadInstance;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.data.MineScribeResourceManager;

import java.io.IOException;
import java.nio.file.Files;

public class MineScribeCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal(MineScribe.ID)
                .then(Commands.literal("generate")
                        .executes(context -> {
                            ReloadInstance instance = MineScribeResourceManager.getInstance()
                                    .reloadResources();
                            instance.done().thenAcceptAsync(unused -> {
                                try {
                                    Files.createFile(
                                            MineScribeResourceManager.getInstance()
                                                    .getFileManager()
                                                    .getMineScribeRoot()
                                                    .resolve(".load_complete")
                                    );
                                    MineScribe.LOGGER.info("MineScribe generation complete");
                                } catch (IOException e) {
                                    MineScribe.LOGGER.error("Failed to write .load_complete", e);
                                }
                            });
                            return 1;
                        })
                );
    }
}
