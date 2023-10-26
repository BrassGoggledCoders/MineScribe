package xyz.brassgoggledcoders.minescribe.event;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.server.ServerLifecycleHooks;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.api.event.GatherFormListsEvent;
import xyz.brassgoggledcoders.minescribe.api.event.GatherPackRepositoryLocationsEvent;
import xyz.brassgoggledcoders.minescribe.api.event.RegisterMineScribeReloadListenerEvent;
import xyz.brassgoggledcoders.minescribe.codec.MineScribeCodecs;
import xyz.brassgoggledcoders.minescribe.core.fileform.FormList;
import xyz.brassgoggledcoders.minescribe.core.packinfo.*;
import xyz.brassgoggledcoders.minescribe.core.util.MineScribeStringHelper;
import xyz.brassgoggledcoders.minescribe.data.CodecMineScribeReloadListener;
import xyz.brassgoggledcoders.minescribe.data.GameGatheredMineScribeReloadListener;
import xyz.brassgoggledcoders.minescribe.util.PackTypeHelper;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

@EventBusSubscriber(modid = MineScribe.ID, bus = Bus.FORGE)
public class ForgeCommonEventHandler {

    @SubscribeEvent
    public static void registerMineScribeResourceReloadListeners(RegisterMineScribeReloadListenerEvent event) {
        event.registerReloadListener(new GameGatheredMineScribeReloadListener<>(
                "registry",
                MineScribePackType.CODEC.listOf(),
                unused -> Path.of("packTypes.json"),
                () -> Stream.of(PackTypeHelper.gatherPackTypes()
                        .toList()
                )
        ));
        event.registerReloadListener(new GameGatheredMineScribeReloadListener<>(
                "registry",
                PackRepositoryLocation.CODEC.listOf(),
                unused -> Path.of("packRepositories.json"),
                () -> {
                    GatherPackRepositoryLocationsEvent locations = new GatherPackRepositoryLocationsEvent();
                    MinecraftForge.EVENT_BUS.post(locations);
                    return Stream.of(locations.getPackRepositoryLocations());
                }
        ));
        event.registerReloadListener(new GameGatheredMineScribeReloadListener<>(
                "formLists",
                FormList.CODEC,
                formList -> {
                    ResourceId id = formList.id();
                    return Path.of(id.namespace(), id.path() + ".json");
                },
                () -> {
                    GatherFormListsEvent formListsEvent = new GatherFormListsEvent();
                    MinecraftForge.EVENT_BUS.post(formListsEvent);
                    return formListsEvent.getFormLists()
                            .stream();
                }
        ));
        event.registerReloadListener(new CodecMineScribeReloadListener<>(
                "types/parent",
                "registry/types/parent",
                MineScribeCodecs.PACK_CONTENT_PARENT_TYPE,
                PackContentParentType.CODEC,
                true
        ));
        event.registerReloadListener(new CodecMineScribeReloadListener<>(
                "types/child",
                "registry/types/child",
                MineScribeCodecs.PACK_CONTENT_CHILD_TYPE,
                PackContentChildType.CODEC,
                true
        ));
        event.registerReloadListener(new CodecMineScribeReloadListener<>(
                "types/object",
                "registry/types/object",
                MineScribeCodecs.OBJECT_TYPE,
                ObjectType.CODEC,
                true
        ));
        event.registerReloadListener(new CodecMineScribeReloadListener<>(
                "types/serializer",
                "registry/types/serializer",
                MineScribeCodecs.SERIALIZER_TYPE,
                SerializerType.CODEC,
                true
        ));
    }

    @SubscribeEvent
    public static void registerPackRepositoryLocations(GatherPackRepositoryLocationsEvent packRepositoryLocations) {
        Optional.ofNullable(ServerLifecycleHooks.getCurrentServer())
                .ifPresent(minecraftServer -> packRepositoryLocations.register(new PackRepositoryLocation(
                        "Level Data Packs",
                        minecraftServer.getWorldPath(LevelResource.DATAPACK_DIR).toAbsolutePath()
                )));
    }

    @SubscribeEvent
    public static void registerFormLists(GatherFormListsEvent formListsEvent) {
        MinecraftServer minecraftServer = ServerLifecycleHooks.getCurrentServer();
        if (minecraftServer != null) {
            RegistryAccess registryAccess = minecraftServer.registryAccess();
            registryAccess.registries()
                    .map(RegistryAccess.RegistryEntry::value)
                    .map(registry -> {
                        ResourceLocation registryId = registry.key().location();
                        ResourceId id = new ResourceId(registryId.getNamespace(), "registry/" + registryId.getPath());
                        return new FormList(
                                id,
                                MineScribeStringHelper.toTitleCase(registryId.getPath()
                                        .replace("_", " ")
                                        .replace("/", " ")
                                ),
                                registry.keySet()
                                        .stream()
                                        .map(ResourceLocation::toString)
                                        .toList()
                        );
                    })
                    .forEach(formListsEvent::register);

            registryAccess.registries()
                    .map(RegistryAccess.RegistryEntry::value)
                    .map(registry -> {
                        ResourceLocation registryId = registry.key().location();
                        ResourceId id = new ResourceId(registryId.getNamespace(), "tag/" + registryId.getPath());
                        return new FormList(
                                id,
                                MineScribeStringHelper.toTitleCase(registryId.getPath()
                                        .replace("_", " ")
                                        .replace("/", " ")
                                ),
                                registry.getTagNames()
                                        .map(tagKey -> "#" + tagKey.location())
                                        .toList()
                        );
                    })
                    .forEach(formListsEvent::register);
        }
    }
}