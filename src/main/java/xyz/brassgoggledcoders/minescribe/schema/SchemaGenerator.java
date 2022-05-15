package xyz.brassgoggledcoders.minescribe.schema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Unit;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.api.json.SimpleGathererInstance;
import xyz.brassgoggledcoders.minescribe.api.schema.creator.SchemaCreatorGatherEvent;
import xyz.brassgoggledcoders.minescribe.schema.creator.RegistrySchemaCreator;
import xyz.brassgoggledcoders.minescribe.schema.root.RootSchema;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

@EventBusSubscriber(modid = MineScribe.ID, bus = Bus.FORGE)
public class SchemaGenerator {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void generate(MinecraftServer server, Runnable finishedMessage) {
        SchemaCreatorGatherEvent event = new SchemaCreatorGatherEvent();
        MinecraftForge.EVENT_BUS.post(event);

        SimpleGathererInstance.of(
                        server.getResourceManager(),
                        event.getSchemaCreators(),
                        Util.backgroundExecutor(),
                        server
                )
                .done()
                .thenApplyAsync(
                        lists -> {
                            List<RootSchema> schemaList = lists.stream()
                                    .flatMap(Collection::stream)
                                    .toList();

                            for (RootSchema rootSchema : schemaList) {
                                File file = rootSchema.getPath().toFile();
                                file.getParentFile().mkdirs();
                                try (
                                        FileWriter fileWriter = new FileWriter(file, false);
                                        JsonWriter jsonWriter = GSON.newJsonWriter(fileWriter)
                                ) {
                                    GSON.toJson(GSON.toJsonTree(rootSchema), jsonWriter);
                                } catch (IOException e) {
                                    MineScribe.LOGGER.error("Failed to write Root Schema: " + rootSchema.id(), e);
                                }
                            }

                            return Unit.INSTANCE;
                        },
                        Util.backgroundExecutor()
                )
                .thenAcceptAsync(
                        unit -> finishedMessage.run(),
                        server
                );


    }

    @SubscribeEvent
    public static void gatherSchemaCreator(SchemaCreatorGatherEvent event) {
        event.addSchemaCreator(new RegistrySchemaCreator());
    }
}
