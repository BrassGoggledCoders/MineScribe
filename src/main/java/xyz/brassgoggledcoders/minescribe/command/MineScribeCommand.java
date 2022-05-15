package xyz.brassgoggledcoders.minescribe.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.collections.StackAwareArrayList;

import java.util.List;

@SuppressWarnings("deprecation")
public class MineScribeCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal(MineScribe.ID)
                .requires(commandSource -> commandSource.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(PackCommand.create())
                .then(SchemaCommand.create())
                .then(Commands.literal("loot")
                        .then(Commands.literal("block")
                                .then(Commands.argument("blockState", BlockStateArgument.block())
                                        .executes(context -> {
                                            CommandSourceStack sourceStack = context.getSource();
                                            Entity entity = sourceStack.getEntityOrException();
                                            BlockState blockState = BlockStateArgument.getBlock(context, "blockState")
                                                    .getState();

                                            ResourceLocation lootTableId = blockState.getBlock()
                                                    .getLootTable();
                                            if (lootTableId != BuiltInLootTables.EMPTY && entity instanceof LivingEntity livingEntity) {
                                                context.getSource().sendSuccess(new TextComponent("Starting Loot Test"), true);
                                                LootContext.Builder builder = new LootContext.Builder(context.getSource()
                                                        .getLevel()
                                                );
                                                LootContext lootContext = builder.withParameter(LootContextParams.BLOCK_STATE, blockState)
                                                        .withParameter(LootContextParams.ORIGIN, entity.position())
                                                        .withParameter(LootContextParams.THIS_ENTITY, entity)
                                                        .withParameter(LootContextParams.TOOL, livingEntity.getItemInHand(InteractionHand.MAIN_HAND))
                                                        .create(LootContextParamSets.BLOCK);

                                                ServerLevel serverlevel = lootContext.getLevel();
                                                LootTable loottable = serverlevel.getServer()
                                                        .getLootTables()
                                                        .get(lootTableId);
                                                List<ItemStack> itemStackList = new StackAwareArrayList<>(MineScribe.LOGGER::info);

                                                loottable.getRandomItems(lootContext, itemStackList::add);
                                                net.minecraftforge.common.ForgeHooks.modifyLoot(lootTableId, itemStackList, lootContext);
                                                context.getSource().sendSuccess(new TextComponent("Finished Loot Test"), true);
                                            } else {
                                                context.getSource().sendFailure(new TextComponent("Not valid entity"));
                                            }
                                            return 1;
                                        })
                                )
                        )
                );
    }
}
