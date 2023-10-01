package xyz.brassgoggledcoders.minescribe.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.core.remote.GatherRemote;
import xyz.brassgoggledcoders.minescribe.core.remote.GatherRemoteImpl;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class MineScribeCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal(MineScribe.ID)
                .then(Commands.literal("start")
                        .executes(context -> {
                            try {
                                GatherRemote remote = (GatherRemote) UnicastRemoteObject.exportObject(new GatherRemoteImpl(), 0);
                                LocateRegistry.createRegistry(0)
                                        .bind("gather", remote);
                                context.getSource().sendSuccess(Component.literal("Started Remote Server"), true);
                                return 1;
                            } catch (AlreadyBoundException | RemoteException e) {
                                context.getSource().sendFailure(Component.literal("Failed to Start Remote Server"));
                                MineScribe.LOGGER.error("Failed to Start Remote Server", e);
                                return 0;
                            }
                        })
                );
    }
}
