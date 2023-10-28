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
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.DoubleFileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.IntegerFileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object.ReferencedObjectFileField;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.util.Range;

import java.nio.file.Path;
import java.util.List;
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
                        new StringFileField("String Field", "string", 0, ""),
                        new ListOfFileField(
                                "String List",
                                "stringList",
                                1,
                                2,
                                5,
                                new StringFileField(
                                        "",
                                        "",
                                        1,
                                        ""
                                )
                        )
                ))
        ));
    }

    private static void generateChildTypes(Consumer<PackContentChildData> consumer) {
        consumer.accept(new PackContentChildData(
                new ResourceLocation("tag/blocks"),
                new ResourceLocation("tag"),
                Component.literal("Block Tags"),
                Path.of("blocks"),
                Optional.of(FileForm.of(
                        new CheckBoxFileField("Replace", "replace", 0, false),
                        new ListSelectionFileField(
                                "Values",
                                "values",
                                1,
                                List.of(
                                        new ResourceId("minecraft", "registry/block"),
                                        new ResourceId("minecraft", "tag/block")
                                )
                        )
                ))
        ));
        consumer.accept(new PackContentChildData(
                new ResourceLocation("tag/items"),
                new ResourceLocation("tag"),
                Component.literal("Item Tags"),
                Path.of("items"),
                Optional.of(FileForm.of(
                        new CheckBoxFileField("Replace", "replace", 0, false),
                        new ListSelectionFileField(
                                "Values",
                                "values",
                                1,
                                List.of(
                                        new ResourceId("minecraft", "registry/item"),
                                        new ResourceId("minecraft", "tag/item")
                                )
                        )
                ))
        ));
    }

    private static void generateObjectTypes(Consumer<ObjectTypeData> consumer) {
        consumer.accept(new ObjectTypeData(
                new ResourceLocation("ingredient"),
                FileForm.of(
                        SerializerInfo.of(
                                "type",
                                "Type",
                                new SingleSelectionFileField(
                                        "Item",
                                        "item",
                                        1,
                                        new ResourceId("minecraft", "registry/item")
                                ),
                                new SingleSelectionFileField(
                                        "Item Tag",
                                        "tag",
                                        2,
                                        new ResourceId("minecraft", "tag/item")
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
                        new ReferencedObjectFileField(
                                "Ingredient",
                                "ingredient",
                                2,
                                new ResourceId("minecraft", "types/object/ingredient")
                        ),
                        new SingleSelectionFileField(
                                "Result",
                                "result",
                                3,
                                new ResourceId("minecraft", "registry/item")
                        ),
                        new DoubleFileField(
                                "Experience",
                                "experience",
                                4,
                                new Range<>(
                                        0.0,
                                        0.7,
                                        Double.MAX_VALUE
                                )
                        ),
                        new IntegerFileField(
                                "Cooking Ticks",
                                "cookingtime",
                                5,
                                new Range<>(
                                        1,
                                        100,
                                        Integer.MAX_VALUE
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
                        new ListOfFileField(
                                "Children",
                                "children",
                                1,
                                1,
                                Integer.MAX_VALUE,
                                new ReferencedObjectFileField(
                                        "",
                                        "",
                                        1,
                                        new ResourceId("minecraft", "ingredient")
                                )
                        )
                )
        ));
    }
}
