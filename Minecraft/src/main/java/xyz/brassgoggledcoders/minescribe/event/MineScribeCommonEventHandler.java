package xyz.brassgoggledcoders.minescribe.event;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import xyz.brassgoggledcoders.minescribe.MineScribe;
import xyz.brassgoggledcoders.minescribe.api.MineScribeAPI;
import xyz.brassgoggledcoders.minescribe.api.data.ObjectTypeData;
import xyz.brassgoggledcoders.minescribe.api.data.PackContentChildData;
import xyz.brassgoggledcoders.minescribe.api.data.PackContentParentData;
import xyz.brassgoggledcoders.minescribe.api.data.SerializerTypeData;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.SerializerInfo;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.*;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.DoubleFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.IntegerFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object.ReferencedObjectFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.util.Range;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

@EventBusSubscriber(modid = MineScribe.ID, bus = Bus.MOD)
public class MineScribeCommonEventHandler {

    @SubscribeEvent
    public static void gatherProviders(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeServer(), PackContentParentData.createProvider(
                event.getGenerator(),
                event.getExistingFileHelper(),
                MineScribe.ID,
                MineScribeCommonEventHandler::generateParentTypes
        ));
        event.getGenerator().addProvider(event.includeServer(), PackContentChildData.createProvider(
                event.getGenerator(),
                event.getExistingFileHelper(),
                MineScribe.ID,
                MineScribeCommonEventHandler::generateChildTypes
        ));
        event.getGenerator().addProvider(event.includeServer(), ObjectTypeData.createProvider(
                event.getGenerator(),
                event.getExistingFileHelper(),
                MineScribe.ID,
                MineScribeCommonEventHandler::generateObjectTypes
        ));
        event.getGenerator().addProvider(event.includeServer(), SerializerTypeData.createProvider(
                event.getGenerator(),
                event.getExistingFileHelper(),
                MineScribe.ID,
                MineScribeCommonEventHandler::generateSerializerTypes
        ));
    }

    private static void generateParentTypes(Consumer<PackContentParentData> consumer) {
        consumer.accept(new PackContentParentData(
                MineScribe.rl("parent_type"),
                Component.literal("Parent Type"),
                Path.of("types", "parent"),
                MineScribeAPI.PACK_TYPE,
                Optional.empty()
        ));
        consumer.accept(new PackContentParentData(
                MineScribe.rl("child_type"),
                Component.literal("Child Type"),
                Path.of("types", "child"),
                MineScribeAPI.PACK_TYPE,
                Optional.empty()
        ));
        consumer.accept(new PackContentParentData(
                new ResourceLocation("tag"),
                Component.literal("Tag"),
                Path.of("tags"),
                PackType.SERVER_DATA,
                Optional.empty()
        ));
        consumer.accept(new PackContentParentData(
                new ResourceLocation("recipe"),
                Component.literal("Recipe"),
                Path.of("recipes"),
                PackType.SERVER_DATA,
                Optional.of(FileForm.of(
                        SerializerInfo.of(
                                "type",
                                "Type"
                        )
                ))
        ));
        consumer.accept(new PackContentParentData(
                new ResourceLocation("test_lists"),
                Component.literal("Testing Lists of Fields"),
                Path.of("test"),
                MineScribeAPI.PACK_TYPE,
                Optional.of(FileForm.of(
                        new FileField<>(
                                new StringFileFieldDefinition(""),
                                new FileFieldInfo(
                                        "String Field",
                                        "string",
                                        0,
                                        false
                                )
                        ),
                        new FileField<>(
                                new ListOfFileFieldDefinition(
                                        2,
                                        5,
                                        new StringFileFieldDefinition("")
                                ),
                                new FileFieldInfo(
                                        "String List",
                                        "stringList",
                                        1,
                                        false
                                )
                        )
                ))
        ));
    }

    private static void generateChildTypes(Consumer<PackContentChildData> consumer) {

    }

    private static void generateObjectTypes(Consumer<ObjectTypeData> consumer) {
        consumer.accept(new ObjectTypeData(
                new ResourceLocation("ingredient"),
                FileForm.of(
                        SerializerInfo.of(
                                "type",
                                "Type",
                                new FileField<>(
                                        new SingleSelectionFileFieldDefinition(
                                                new ResourceId("minecraft", "registry/item")
                                        ),
                                        new FileFieldInfo(
                                                "Item",
                                                "item",
                                                1,
                                                false
                                        )
                                ),
                                new FileField<>(
                                        new SingleSelectionFileFieldDefinition(
                                                new ResourceId("minecraft", "tag/item")
                                        ),
                                        new FileFieldInfo(
                                                "Item Tag",
                                                "tag",
                                                2,
                                                false
                                        )
                                )
                        )
                )
        ));
    }

    private static void generateSerializerTypes(Consumer<SerializerTypeData> consumer) {
        consumer.accept(new SerializerTypeData(
                new ResourceLocation("recipes/blasting"),
                new ResourceLocation("blasting"),
                new ResourceLocation("types/parent/recipe"),
                Component.literal("Blasting"),
                FileForm.of(
                        new FileField<>(
                                new ReferencedObjectFileFieldDefinition(
                                        new ResourceId("minecraft", "ingredient")
                                ),
                                new FileFieldInfo(
                                        "Ingredient",
                                        "ingredient",
                                        2,
                                        true
                                )
                        ),
                        new FileField<>(
                                new SingleSelectionFileFieldDefinition(
                                        new ResourceId("minecraft", "registry/item")
                                ),
                                new FileFieldInfo(
                                        "Result",
                                        "result",
                                        3,
                                        true
                                )
                        ),
                        new FileField<>(
                                new DoubleFileFieldDefinition(
                                        new Range<>(
                                                0.0,
                                                0.7,
                                                Double.MAX_VALUE
                                        )
                                ),
                                new FileFieldInfo(
                                        "Experience",
                                        "experience",
                                        4,
                                        false
                                )
                        ),
                        new FileField<>(
                                new IntegerFileFieldDefinition(
                                        new Range<>(
                                                1,
                                                100,
                                                Integer.MAX_VALUE
                                        )
                                ),
                                new FileFieldInfo(
                                        "Cooking Ticks",
                                        "cookingtime",
                                        5,
                                        false
                                )
                        )
                )
        ));
        consumer.accept(new SerializerTypeData(
                new ResourceLocation("forge", "ingredients/compound"),
                new ResourceLocation("forge", "compound"),
                new ResourceLocation("types/object/ingredient"),
                Component.literal("Compound"),
                FileForm.of(
                        new FileField<>(
                                new ListOfFileFieldDefinition(
                                        1,
                                        Integer.MAX_VALUE,
                                        new ReferencedObjectFileFieldDefinition(
                                                new ResourceId("minecraft", "ingredient")
                                        )
                                ),
                                new FileFieldInfo(
                                        "Children",
                                        "children",
                                        1,
                                        true
                                )
                        )

                )
        ));
    }
}
