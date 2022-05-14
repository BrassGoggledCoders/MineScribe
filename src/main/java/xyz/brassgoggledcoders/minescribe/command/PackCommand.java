package xyz.brassgoggledcoders.minescribe.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.server.command.EnumArgument;
import xyz.brassgoggledcoders.minescribe.pack.MSPackType;
import xyz.brassgoggledcoders.minescribe.pack.PackLocation;

import java.io.File;
import java.nio.file.Path;

public class PackCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("pack")
                .then(PackCommand.generate());
    }

    public static LiteralArgumentBuilder<CommandSourceStack> generate() {
        return Commands.literal("generate")
                .then(Commands.argument("type", EnumArgument.enumArgument(MSPackType.class))
                        .then(Commands.argument("location", EnumArgument.enumArgument(PackLocation.class))
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .executes(commandContext -> {
                                            CommandSourceStack commandSource = commandContext.getSource();
                                            MSPackType packType = commandContext.getArgument("type", MSPackType.class);
                                            PackLocation packLocation = commandContext.getArgument("location", PackLocation.class);
                                            String name = StringArgumentType.getString(commandContext, "name");

                                            Path locationPath = packLocation.resolve(
                                                    packType,
                                                    FMLPaths.GAMEDIR.get(),
                                                    commandSource.getServer()
                                                            .getWorldPath(LevelResource.ROOT)
                                            );

                                            Path packPath = packType.resolvePack(
                                                    FMLPaths.GAMEDIR.get(),
                                                    locationPath,
                                                    name
                                            );

                                            Path typePath = packType.resolveType(packPath);
                                            File typeFile = typePath.toFile();
                                            boolean exists = typeFile.exists() || typePath.toFile().mkdirs();

                                            if (!exists) {
                                                commandSource.sendFailure(new TextComponent("Failed to create files"));
                                                return 0;
                                            } else {
                                                return packType.onCreate(packPath, name)
                                                        .map(
                                                                throwable -> {
                                                                    commandSource.sendFailure(new TextComponent(
                                                                            "Failed to Create Pack: " + throwable.getMessage()
                                                                    ));
                                                                    return 0;
                                                                },
                                                                string -> {
                                                                    commandSource.sendSuccess(new TextComponent(
                                                                            "Created New Pack"
                                                                    ), true);
                                                                    return 1;
                                                                }
                                                        );
                                            }
                                        })
                                )
                        )
                );
    }
}
