package xyz.brassgoggledcoders.minescribe.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ReloadInstance;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.data.MineScribeResourceManager;
import xyz.brassgoggledcoders.minescribe.util.PackTypeHelper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MineScribeCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal(MineScribe.ID)
                .then(Commands.literal("generate")
                        .executes(context -> {
                            if (Registries.getPackTypes().isEmpty()) {
                                Registries.getPackTypes()
                                        .getMap()
                                        .putAll(PackTypeHelper.gatherPackTypes()
                                                .collect(Collectors.toMap(
                                                        MineScribePackType::name,
                                                        Function.identity()
                                                ))
                                        );
                            }
                            ReloadInstance instance = MineScribeResourceManager.getInstance()
                                    .reloadResources();
                            instance.done().thenAcceptAsync(unused -> {
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
