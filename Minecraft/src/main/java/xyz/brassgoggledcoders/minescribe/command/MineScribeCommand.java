package xyz.brassgoggledcoders.minescribe.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.datafixers.util.Unit;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ReloadInstance;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.data.MineScribeResourceManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class MineScribeCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal(MineScribe.ID)
                .then(Commands.literal("generate")
                        .executes(context -> {
                            MineScribeResourceManager.getInstance()
                                    .getFileManager()
                                    .clearRoot();
                            ReloadInstance instance = MineScribeResourceManager.getInstance()
                                    .reloadResources();
                            instance.done()
                                    .thenApply(unused -> Unit.INSTANCE)
                                    .exceptionallyAsync(throwable -> {
                                        context.getSource()
                                                .sendFailure(Component.literal("Failed to generate MineScribe data"));
                                        MineScribe.LOGGER.error("Failed to generate MineScribe data", throwable);
                                        return Unit.INSTANCE;
                                    })
                                    .thenAcceptAsync(unused -> {
                                        try {
                                            Files.writeString(
                                                    MineScribeResourceManager.getInstance()
                                                            .getFileManager()
                                                            .getMineScribeRoot()
                                                            .resolve(".load_complete"),
                                                    "",
                                                    StandardCharsets.UTF_8,
                                                    StandardOpenOption.CREATE,
                                                    StandardOpenOption.TRUNCATE_EXISTING,
                                                    StandardOpenOption.WRITE
                                            );
                                            context.getSource()
                                                    .sendSuccess(
                                                            Component.literal("MineScribe generation complete"),
                                                            true
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
