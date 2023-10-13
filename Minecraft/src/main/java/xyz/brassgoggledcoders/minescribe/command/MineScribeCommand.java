package xyz.brassgoggledcoders.minescribe.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.connection.MineScribeNettyClient;
import xyz.brassgoggledcoders.minescribe.core.fileform.listhandler.ListHandlerStore;
import xyz.brassgoggledcoders.minescribe.data.MineScribeResourceManager;
import xyz.brassgoggledcoders.minescribe.list.GsonListHandler;

public class MineScribeCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal(MineScribe.ID)
                .then(Commands.literal("start")
                        .executes(context -> {
                            ListHandlerStore.setListHandler(new GsonListHandler());
                            MineScribeNettyClient.getInstance()
                                    .tryStart();
                            MineScribeResourceManager.getInstance()
                                    .reloadResources();

                            return 1;
                        })
                );
    }
}
