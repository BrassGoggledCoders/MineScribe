package xyz.brassgoggledcoders.minescribe.event;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.server.ServerLifecycleHooks;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.api.data.FileFieldData;
import xyz.brassgoggledcoders.minescribe.api.data.FileFieldInfoData;
import xyz.brassgoggledcoders.minescribe.api.data.FileFormData;
import xyz.brassgoggledcoders.minescribe.api.data.PackContentChildData;
import xyz.brassgoggledcoders.minescribe.api.event.GatherFormListsEvent;
import xyz.brassgoggledcoders.minescribe.api.event.GatherPackContentChildTypes;
import xyz.brassgoggledcoders.minescribe.api.event.RegisterMineScribeReloadListenerEvent;
import xyz.brassgoggledcoders.minescribe.codec.MineScribeCodecs;
import xyz.brassgoggledcoders.minescribe.core.fileform.FormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.CheckBoxFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.ListSelectionFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.FileIdFormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.ValueFormList;
import xyz.brassgoggledcoders.minescribe.core.packinfo.*;
import xyz.brassgoggledcoders.minescribe.core.util.MineScribeStringHelper;
import xyz.brassgoggledcoders.minescribe.data.CodecMineScribeReloadListener;
import xyz.brassgoggledcoders.minescribe.data.FileCopyMineScribeReloadListener;
import xyz.brassgoggledcoders.minescribe.util.PackTypeHelper;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = MineScribe.ID, bus = Bus.FORGE)
public class ForgeCommonEventHandler {

    @SubscribeEvent
    public static void registerMineScribeResourceReloadListeners(RegisterMineScribeReloadListenerEvent event) {
        event.registerReloadListener(new CodecMineScribeReloadListener<>(
                "pack_types",
                MineScribePackType.CODEC,
                MineScribePackType.CODEC,
                false,
                () -> PackTypeHelper.gatherPackTypes()
                        .collect(Collectors.<MineScribePackType, ResourceLocation, MineScribePackType>toMap(
                                packType -> MineScribe.rl(packType.name().toLowerCase(Locale.ROOT)),
                                Function.identity()
                        ))
        ));
        event.registerReloadListener(new CodecMineScribeReloadListener<>(
                "pack_repositories",
                PackRepositoryLocation.CODEC,
                PackRepositoryLocation.CODEC,
                false
        ));
        event.registerReloadListener(new CodecMineScribeReloadListener<>(
                "form_lists",
                FormList.CODEC,
                FormList.CODEC,
                false,
                () -> {
                    GatherFormListsEvent formListsEvent = new GatherFormListsEvent();
                    MinecraftForge.EVENT_BUS.post(formListsEvent);
                    return formListsEvent.getValues();
                }
        ));
        event.registerReloadListener(new CodecMineScribeReloadListener<>(
                "types/parent",
                MineScribeCodecs.PACK_CONTENT_PARENT_TYPE,
                PackContentParentType.CODEC,
                true
        ));
        event.registerReloadListener(new CodecMineScribeReloadListener<>(
                "types/child",
                MineScribeCodecs.PACK_CONTENT_CHILD_TYPE,
                PackContentChildType.CODEC,
                true,
                () -> {
                    GatherPackContentChildTypes gatherEvent = new GatherPackContentChildTypes();
                    MinecraftForge.EVENT_BUS.post(gatherEvent);
                    return gatherEvent.getValues();
                }
        ));
        event.registerReloadListener(new CodecMineScribeReloadListener<>(
                "types/object",
                MineScribeCodecs.OBJECT_TYPE,
                ObjectType.CODEC,
                true
        ));
        event.registerReloadListener(new CodecMineScribeReloadListener<>(
                "types/serializer",
                MineScribeCodecs.SERIALIZER_TYPE,
                SerializerType.CODEC,
                true
        ));
        event.registerReloadListener(new FileCopyMineScribeReloadListener(
                "scripts/validation",
                ".js"
        ));
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

    @SubscribeEvent
    public static void registerChildTypes(GatherPackContentChildTypes gatherPackContentChildTypes) {
        MinecraftServer minecraftServer = ServerLifecycleHooks.getCurrentServer();
        if (minecraftServer != null) {
            RegistryAccess registryAccess = minecraftServer.registryAccess();
            registryAccess.registries()
                    .map(registry -> {
                        ResourceLocation registryId = registry.key().location();
                        ResourceLocation id = new ResourceLocation(registryId.getNamespace(), "tag/" + registryId.getPath());
                        Path tagPath = Path.of(TagManager.getTagDir(registry.key()));
                        if (tagPath.startsWith("tags")) {
                            tagPath = tagPath.subpath(1, tagPath.getNameCount());
                        }
                        return new PackContentChildData(
                                id,
                                new ResourceLocation("tag"),
                                Component.literal(MineScribeStringHelper.toTitleCase(registryId.getPath()
                                        .replace("_", " ")
                                        .replace("/", " ")
                                ) + " Tags"),
                                tagPath,
                                Optional.of(FileFormData.of(
                                        new FileFieldData<>(
                                                new CheckBoxFileFieldDefinition(false),
                                                new FileFieldInfoData(
                                                        "Replace",
                                                        "replace",
                                                        0,
                                                        false
                                                )
                                        ),
                                        new FileFieldData<>(
                                                new ListSelectionFileFieldDefinition(List.of(
                                                        new ValueFormList(new ResourceId(registryId.getNamespace(), "registry/" + registryId.getPath())),
                                                        new ValueFormList(new ResourceId(registryId.getNamespace(), "tag/" + registryId.getPath())),
                                                        new FileIdFormList("**tags/" + tagPath, "#")
                                                )),
                                                new FileFieldInfoData(
                                                        "Values",
                                                        "values",
                                                        1,
                                                        false
                                                )
                                        )
                                ))
                        );
                    })
                    .forEach(gatherPackContentChildTypes::register);

        }
    }
}