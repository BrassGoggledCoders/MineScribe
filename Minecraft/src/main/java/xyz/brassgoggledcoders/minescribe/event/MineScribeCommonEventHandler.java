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
import xyz.brassgoggledcoders.minescribe.api.data.*;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.fileform.SerializerInfo;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.*;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.DoubleFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.IntegerFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object.ReferencedObjectFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.RegistryFormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.RegistryListFormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.ValueFormList;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackRepositoryLocation;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.util.Range;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
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
        event.getGenerator().addProvider(event.includeServer(), MineScribeProviders.createLocationProvider(
                event.getGenerator(),
                event.getExistingFileHelper(),
                MineScribe.ID,
                MineScribeCommonEventHandler::generatePackLocations
        ));
    }

    private static void generateParentTypes(Consumer<PackContentParentData> consumer) {
        consumer.accept(new PackContentParentData(
                MineScribe.rl("parent_type"),
                Component.literal("Parent Type"),
                Path.of("types", "parent"),
                MineScribeAPI.PACK_TYPE,
                Optional.of(FileFormData.of(
                        new FileFieldData<>(
                                new StringFileFieldDefinition(""),
                                new FileFieldInfoData(
                                        "Label",
                                        "label",
                                        1,
                                        true
                                )
                        ),
                        new FileFieldData<>(
                                new StringFileFieldDefinition(""),
                                new FileFieldInfoData(
                                        "Path",
                                        "path",
                                        2,
                                        true
                                )
                        ),
                        new FileFieldData<>(
                                new SingleSelectionFileFieldDefinition(
                                        new RegistryFormList<>(Registries.getPackTypeRegistry())
                                ),
                                new FileFieldInfoData(
                                        "Pack Type",
                                        "packType",
                                        3,
                                        true
                                )
                        ),
                        new FileFieldData<>(
                                new ReferencedObjectFileFieldDefinition(
                                        new ResourceId(MineScribe.ID, "file_form")
                                ),
                                new FileFieldInfoData(
                                        "File Form",
                                        "form",
                                        4,
                                        false
                                )
                        )
                ))
        ));
        consumer.accept(new PackContentParentData(
                MineScribe.rl("child_type"),
                Component.literal("Child Type"),
                Path.of("types", "child"),
                MineScribeAPI.PACK_TYPE,
                Optional.of(FileFormData.of(
                        new FileFieldData<>(
                                new StringFileFieldDefinition(""),
                                new FileFieldInfoData(
                                        "Parent",
                                        "parentId",
                                        1,
                                        true
                                )
                        ),
                        new FileFieldData<>(
                                new StringFileFieldDefinition(""),
                                new FileFieldInfoData(
                                        "Label",
                                        "label",
                                        2,
                                        true
                                )
                        ),
                        new FileFieldData<>(
                                new StringFileFieldDefinition(""),
                                new FileFieldInfoData(
                                        "Path",
                                        "path",
                                        3,
                                        true
                                )
                        ),
                        new FileFieldData<>(
                                new ReferencedObjectFileFieldDefinition(
                                        new ResourceId(MineScribe.ID, "file_form")
                                ),
                                new FileFieldInfoData(
                                        "File Form",
                                        "form",
                                        4,
                                        false
                                )
                        )
                ))
        ));
        consumer.accept(new PackContentParentData(
                MineScribe.rl("serializer_type"),
                Component.literal("Serializer Type"),
                Path.of("types", "serializer"),
                MineScribeAPI.PACK_TYPE,
                Optional.of(FileFormData.of(
                        new FileFieldData<>(
                                new SingleSelectionFileFieldDefinition(List.of(
                                        new RegistryFormList<>(Registries.getContentParentTypes(), true),
                                        new RegistryFormList<>(Registries.getContentChildTypes(), true),
                                        new RegistryFormList<>(Registries.getObjectTypes(), true)
                                )),
                                new FileFieldInfoData(
                                        "Parent",
                                        "parentId",
                                        1,
                                        true
                                )
                        ),
                        new FileFieldData<IFileFieldDefinition>(
                                new StringFileFieldDefinition(""),
                                new FileFieldInfoData(
                                        "ID",
                                        "serializerId",
                                        2,
                                        true
                                )
                        ),
                        new FileFieldData<>(
                                new StringFileFieldDefinition(""),
                                new FileFieldInfoData(
                                        "Label",
                                        "label",
                                        3,
                                        true
                                )
                        ),
                        new FileFieldData<>(
                                new ReferencedObjectFileFieldDefinition(
                                        new ResourceId(MineScribe.ID, "file_form")
                                ),
                                new FileFieldInfoData(
                                        "File Form",
                                        "form",
                                        4,
                                        false
                                )
                        )
                ))
        ));
        consumer.accept(new PackContentParentData(
                MineScribe.rl("object_type"),
                Component.literal("Object Type"),
                Path.of("types", "object"),
                MineScribeAPI.PACK_TYPE,
                Optional.of(FileFormData.of(
                        new FileFieldData<>(
                                new ReferencedObjectFileFieldDefinition(
                                        new ResourceId(MineScribe.ID, "file_form")
                                ),
                                new FileFieldInfoData(
                                        "File Form",
                                        "form",
                                        0,
                                        false
                                )
                        )
                ))
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
                Optional.of(FileFormData.of(
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
                Optional.of(FileFormData.of(
                        new FileFieldData<>(
                                new StringFileFieldDefinition(""),
                                new FileFieldInfoData(
                                        "String Field",
                                        "string",
                                        0,
                                        false,
                                        List.of(
                                                ValidationData.of(
                                                        new ResourceLocation(MineScribe.ID, "min_length"),
                                                        JsonBuilder.forObject()
                                                                .withInt("minLength", 4)
                                                                .build()
                                                )
                                        ),
                                        Optional.of("This is a String Field. It must be at least 4 characters long")
                                )
                        ),
                        new FileFieldData<>(
                                new ListOfFileFieldDefinition(
                                        2,
                                        5,
                                        new StringFileFieldDefinition("")
                                ),
                                new FileFieldInfoData(
                                        "String List",
                                        "stringList",
                                        1,
                                        false,
                                        List.of(
                                                ValidationData.of(
                                                        new ResourceLocation(MineScribe.ID, "min_length"),
                                                        JsonBuilder.forObject()
                                                                .withInt("minLength", 4)
                                                                .build()
                                                )
                                        ),
                                        Optional.of("This is a List of Strings. It also requires at least 4 characters pre row")
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
                FileFormData.of(
                        SerializerInfo.of(
                                "type",
                                "Type",
                                FileForm.of(
                                        List.of(
                                                new FileField<>(
                                                        new SingleSelectionFileFieldDefinition(
                                                                new ValueFormList(new ResourceId("minecraft", "registry/item"))
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
                                                                new ValueFormList(new ResourceId("minecraft", "tag/item"))
                                                        ),
                                                        new FileFieldInfo(
                                                                "Item Tag",
                                                                "tag",
                                                                2,
                                                                false
                                                        )
                                                )
                                        ),
                                        List.of(
                                                ValidationData.of(
                                                        new ResourceLocation("minescribe:only_x"),
                                                        JsonBuilder.forObject()
                                                                .withStringArray("fields", "item", "tag")
                                                                .build()
                                                )
                                        )
                                )
                        )
                )
        ));

        consumer.accept(new ObjectTypeData(
                new ResourceLocation(MineScribe.ID, "file_field_info"),
                FileFormData.of(
                        new FileFieldData<>(
                                new StringFileFieldDefinition(""),
                                new FileFieldInfoData(
                                        "Label",
                                        "label",
                                        0,
                                        true
                                )
                        ),
                        new FileFieldData<>(
                                new StringFileFieldDefinition(""),
                                new FileFieldInfoData(
                                        "Field",
                                        "field",
                                        1,
                                        true
                                )
                        ),
                        new FileFieldData<>(
                                new IntegerFileFieldDefinition(new Range<>(
                                        0,
                                        0,
                                        10000,
                                        1
                                )),
                                new FileFieldInfoData(
                                        "Sort Order",
                                        "sortOrder",
                                        2,
                                        true
                                )
                        ),
                        new FileFieldData<>(
                                new CheckBoxFileFieldDefinition(false),
                                new FileFieldInfoData(
                                        "Required",
                                        "required",
                                        3,
                                        false
                                )
                        )
                )
        ));

        consumer.accept(new ObjectTypeData(
                new ResourceLocation(MineScribe.ID, "file_field_definition"),
                FileFormData.of(
                        SerializerInfo.of(
                                "type",
                                "Type"
                        )
                )
        ));
        consumer.accept(new ObjectTypeData(
                new ResourceLocation(MineScribe.ID, "form_list"),
                FileFormData.of(
                        SerializerInfo.of(
                                "type",
                                "Type"
                        )
                )
        ));

        consumer.accept(new ObjectTypeData(
                new ResourceLocation(MineScribe.ID, "serializer_info"),
                FileFormData.of(
                        new FileFieldData<>(
                                new StringFileFieldDefinition("type"),
                                new FileFieldInfoData(
                                        "Field",
                                        "field",
                                        0,
                                        true
                                )
                        ),
                        new FileFieldData<>(
                                new StringFileFieldDefinition(""),
                                new FileFieldInfoData(
                                        "Label",
                                        "label",
                                        1,
                                        true
                                )
                        ),
                        new FileFieldData<>(
                                new ListOfFileFieldDefinition(
                                        1,
                                        Integer.MAX_VALUE,
                                        new ReferencedObjectFileFieldDefinition(
                                                new ResourceId(MineScribe.ID, "file_field")
                                        )
                                ),
                                new FileFieldInfoData(
                                        "Default Field",
                                        "defaultField",
                                        2,
                                        true
                                )
                        ),
                        new FileFieldData<>(
                                new StringFileFieldDefinition(""),
                                new FileFieldInfoData(
                                        "Default Type",
                                        "defaultType",
                                        3,
                                        false,
                                        List.of(
                                                ValidationData.of(
                                                        new ResourceLocation(MineScribe.ID, "regex"),
                                                        JsonBuilder.forObject()
                                                                .withString("regex", "^[a-z0-9\\.\\-_]+$")
                                                                .build()
                                                )
                                        )
                                )
                        )
                )
        ));

        consumer.accept(new ObjectTypeData(
                new ResourceLocation(MineScribe.ID, "file_field"),
                FileFormData.of(
                        new FileFieldData<>(
                                new ReferencedObjectFileFieldDefinition(
                                        new ResourceId(MineScribe.ID, "file_field_definition")
                                ),
                                new FileFieldInfoData(
                                        "Definition",
                                        JsonFieldNames.DEFINITION,
                                        0,
                                        true
                                )
                        ),
                        new FileFieldData<IFileFieldDefinition>(
                                new ReferencedObjectFileFieldDefinition(
                                        new ResourceId(MineScribe.ID, "file_field_info")
                                ),
                                new FileFieldInfoData(
                                        "Info",
                                        JsonFieldNames.INFO,
                                        1,
                                        true
                                )
                        )
                )
        ));
        consumer.accept(new ObjectTypeData(
                new ResourceLocation(MineScribe.ID, "file_form"),
                FileFormData.of(
                        new FileFieldData<>(
                                new ListOfFileFieldDefinition(
                                        0,
                                        Integer.MAX_VALUE,
                                        new ReferencedObjectFileFieldDefinition(
                                                new ResourceId(MineScribe.ID, "file_field")
                                        )
                                ),
                                new FileFieldInfoData(
                                        "Fields",
                                        "fields",
                                        0,
                                        false
                                )
                        ),
                        new FileFieldData<IFileFieldDefinition>(
                                new ReferencedObjectFileFieldDefinition(
                                        new ResourceId(MineScribe.ID, "serializer_info")
                                ),
                                new FileFieldInfoData(
                                        "Serializer Info",
                                        "serializer",
                                        1,
                                        false
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
                FileFormData.of(
                        new FileFieldData<>(
                                new ReferencedObjectFileFieldDefinition(
                                        new ResourceId("minecraft", "ingredient")
                                ),
                                new FileFieldInfoData(
                                        "Ingredient",
                                        "ingredient",
                                        2,
                                        true
                                )
                        ),
                        new FileFieldData<>(
                                new SingleSelectionFileFieldDefinition(
                                        new ValueFormList(new ResourceId("minecraft", "registry/item"))
                                ),
                                new FileFieldInfoData(
                                        "Result",
                                        "result",
                                        3,
                                        true
                                )
                        ),
                        new FileFieldData<>(
                                new DoubleFileFieldDefinition(
                                        new Range<>(
                                                0.0,
                                                0.7,
                                                Double.MAX_VALUE,
                                                0.1
                                        )
                                ),
                                new FileFieldInfoData(
                                        "Experience",
                                        "experience",
                                        4,
                                        false
                                )
                        ),
                        new FileFieldData<>(
                                new IntegerFileFieldDefinition(
                                        new Range<>(
                                                1,
                                                100,
                                                Integer.MAX_VALUE,
                                                1
                                        )
                                ),
                                new FileFieldInfoData(
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
                FileFormData.of(
                        new FileFieldData<>(
                                new ListOfFileFieldDefinition(
                                        1,
                                        Integer.MAX_VALUE,
                                        new ReferencedObjectFileFieldDefinition(
                                                new ResourceId("minecraft", "ingredient")
                                        )
                                ),
                                new FileFieldInfoData(
                                        "Children",
                                        "children",
                                        1,
                                        true
                                )
                        )

                )
        ));
        consumer.accept(new SerializerTypeData(
                new ResourceLocation(MineScribe.ID, "file_field_definitions/checkbox"),
                new ResourceLocation(MineScribe.ID, "checkbox"),
                new ResourceLocation(MineScribe.ID, "types/object/file_field_definition"),
                Component.literal("Check Box"),
                FileFormData.of(
                        new FileFieldData<>(
                                new CheckBoxFileFieldDefinition(false),
                                new FileFieldInfoData(
                                        "Default Value",
                                        JsonFieldNames.DEFAULT_VALUE,
                                        0,
                                        false
                                )
                        )
                )
        ));
        consumer.accept(new SerializerTypeData(
                new ResourceLocation(MineScribe.ID, "file_field_definitions/string"),
                new ResourceLocation(MineScribe.ID, "string"),
                new ResourceLocation(MineScribe.ID, "types/object/file_field_definition"),
                Component.literal("Text Field"),
                FileFormData.of(
                        new FileFieldData<>(
                                new StringFileFieldDefinition(""),
                                new FileFieldInfoData(
                                        "Default Value",
                                        JsonFieldNames.DEFAULT_VALUE,
                                        0,
                                        false
                                )
                        )
                )
        ));
        consumer.accept(new SerializerTypeData(
                new ResourceLocation(MineScribe.ID, "file_field_definitions/double"),
                new ResourceLocation(MineScribe.ID, "double"),
                new ResourceLocation(MineScribe.ID, "types/object/file_field_definition"),
                Component.literal("Number Field (Double)"),
                FileFormData.of(
                        new FileFieldData<>(
                                new DoubleFileFieldDefinition(new Range<>(Double.MIN_VALUE, 0D, Double.MAX_VALUE, 0.1)),
                                new FileFieldInfoData(
                                        "Minimum",
                                        JsonFieldNames.RANGE + "." + JsonFieldNames.MIN,
                                        0,
                                        false
                                )
                        ),
                        new FileFieldData<>(
                                new DoubleFileFieldDefinition(new Range<>(Double.MIN_VALUE, 0D, Double.MAX_VALUE, 0.1)),
                                new FileFieldInfoData(
                                        "Default",
                                        JsonFieldNames.RANGE + "." + JsonFieldNames.START,
                                        1,
                                        false
                                )
                        ),
                        new FileFieldData<>(
                                new DoubleFileFieldDefinition(new Range<>(Double.MIN_VALUE, 1000D, Double.MAX_VALUE, 0.1)),
                                new FileFieldInfoData(
                                        "Maximum",
                                        JsonFieldNames.RANGE + "." + JsonFieldNames.MAX,
                                        2,
                                        false
                                )
                        )
                )
        ));
        consumer.accept(new SerializerTypeData(
                new ResourceLocation(MineScribe.ID, "file_field_definitions/integer"),
                new ResourceLocation(MineScribe.ID, "integer"),
                new ResourceLocation(MineScribe.ID, "types/object/file_field_definition"),
                Component.literal("Number Field (Integer)"),
                FileFormData.of(
                        new FileFieldData<>(
                                new IntegerFileFieldDefinition(new Range<>(Integer.MIN_VALUE, 0, Integer.MAX_VALUE, 1)),
                                new FileFieldInfoData(
                                        "Minimum",
                                        JsonFieldNames.RANGE + "." + JsonFieldNames.MIN,
                                        0,
                                        false
                                )
                        ),
                        new FileFieldData<>(
                                new IntegerFileFieldDefinition(new Range<>(Integer.MIN_VALUE, 0, Integer.MAX_VALUE, 1)),
                                new FileFieldInfoData(
                                        "Default",
                                        JsonFieldNames.RANGE + "." + JsonFieldNames.START,
                                        1,
                                        false
                                )
                        ),
                        new FileFieldData<>(
                                new IntegerFileFieldDefinition(new Range<>(Integer.MIN_VALUE, 10000, Integer.MAX_VALUE, 1)),
                                new FileFieldInfoData(
                                        "Maximum",
                                        JsonFieldNames.RANGE + "." +  JsonFieldNames.MAX,
                                        2,
                                        false
                                )
                        )
                )
        ));
        consumer.accept(new SerializerTypeData(
                new ResourceLocation(MineScribe.ID, "file_field_definitions/single_selection"),
                new ResourceLocation(MineScribe.ID, "single_selection"),
                new ResourceLocation(MineScribe.ID, "types/object/file_field_definition"),
                Component.literal("Single Selection ComboBox Field"),
                FileFormData.of(
                        new FileFieldData<>(
                                new ListOfFileFieldDefinition(
                                        1,
                                        Integer.MAX_VALUE,
                                        new ReferencedObjectFileFieldDefinition(
                                                new ResourceId(MineScribe.ID, "form_list")
                                        )
                                ),
                                new FileFieldInfoData(
                                        "Form Lists",
                                        JsonFieldNames.LISTS,
                                        0,
                                        true
                                )
                        )
                )
        ));


        consumer.accept(new SerializerTypeData(
                new ResourceLocation(MineScribe.ID, "form_lists/list"),
                new ResourceLocation(MineScribe.ID, "list"),
                new ResourceLocation(MineScribe.ID, "types/object/form_list"),
                Component.literal("List"),
                FileFormData.of(
                        new FileFieldData<>(
                                new SingleSelectionFileFieldDefinition(
                                        new RegistryFormList<>(Registries.getFormListValues())
                                ),
                                new FileFieldInfoData(
                                        "List Id",
                                        JsonFieldNames.ID,
                                        0,
                                        true
                                )
                        )
                )
        ));
        consumer.accept(new SerializerTypeData(
                new ResourceLocation(MineScribe.ID, "form_lists/registry_names"),
                new ResourceLocation(MineScribe.ID, "registry_names"),
                new ResourceLocation(MineScribe.ID, "types/object/form_list"),
                Component.literal("Registry Names"),
                FileFormData.of(
                )
        ));
        consumer.accept(new SerializerTypeData(
                new ResourceLocation(MineScribe.ID, "form_lists/file_name"),
                new ResourceLocation(MineScribe.ID, "file_name"),
                new ResourceLocation(MineScribe.ID, "types/object/form_list"),
                Component.literal("Files"),
                FileFormData.of(
                        new FileFieldData<>(
                                new StringFileFieldDefinition(""),
                                new FileFieldInfoData(
                                        "Folder Matcher",
                                        JsonFieldNames.FOLDER_MATCHER,
                                        0,
                                        true,
                                        Collections.emptyList(),
                                        Optional.of("Folder Matcher is based on Java's glob, with more info found here: " +
                                                "https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob"
                                        )
                                )
                        ),
                        new FileFieldData<>(
                                new StringFileFieldDefinition(""),
                                new FileFieldInfoData(
                                        "Leading Character",
                                        JsonFieldNames.LEADING_CHARACTER,
                                        1,
                                        false,
                                        Collections.emptyList(),
                                        Optional.of("Adds a character like # (for tags) prior to the id created from " +
                                                "the folder name")
                                )
                        )
                )
        ));
        consumer.accept(new SerializerTypeData(
                new ResourceLocation(MineScribe.ID, "form_lists/registry"),
                new ResourceLocation(MineScribe.ID, "registry"),
                new ResourceLocation(MineScribe.ID, "types/object/form_list"),
                Component.literal("Registry Values"),
                FileFormData.of(
                        new FileFieldData<>(
                                new SingleSelectionFileFieldDefinition(new RegistryListFormList()),
                                new FileFieldInfoData(
                                        "Registry",
                                        JsonFieldNames.REGISTRY,
                                        0,
                                        true,
                                        Collections.emptyList()
                                )
                        ),
                        new FileFieldData<>(
                                new StringFileFieldDefinition(""),
                                new FileFieldInfoData(
                                        "Use Full ID",
                                        JsonFieldNames.FULL_NAME_ID,
                                        1,
                                        false,
                                        Collections.emptyList(),
                                        Optional.of("Determines if the registry id should include the registry path")
                                )
                        )
                )
        ));
    }

    private static void generatePackLocations(BiConsumer<ResourceLocation, PackRepositoryLocation> mapConsumer) {
        mapConsumer.accept(
                new ResourceLocation("resource_pack"),
                new PackRepositoryLocation(
                        "Client Resource Packs",
                        "**resourcepacks"
                )
        );
        mapConsumer.accept(
                new ResourceLocation("saves_datapacks"),
                new PackRepositoryLocation(
                        "${PATH:-2} Data Packs",
                        "**saves/*/datapacks"
                )
        );
    }
}
