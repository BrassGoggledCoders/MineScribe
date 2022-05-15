package xyz.brassgoggledcoders.minescribe.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import xyz.brassgoggledcoders.minescribe.schema.SchemaGenerator;

public class SchemaCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("schema")
                .then(Commands.literal("generate")
                        .executes(context -> {
                            CommandSourceStack commandSource = context.getSource();
                            commandSource.sendSuccess(new TextComponent("Starting Schema Generation"), false);
                            SchemaGenerator.generate(
                                    commandSource.getServer(),
                                    () -> commandSource.sendSuccess(new TextComponent("Finished Schema Generation"), false)
                            );
                            return 1;
                        })
                );
    }
}
