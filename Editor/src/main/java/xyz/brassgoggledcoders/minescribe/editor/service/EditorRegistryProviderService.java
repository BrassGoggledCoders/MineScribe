package xyz.brassgoggledcoders.minescribe.editor.service;

import com.mojang.serialization.Codec;
import javafx.scene.control.SpinnerValueFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.FormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.*;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.DoubleFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.IntegerFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object.ReferencedObjectFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.packinfo.*;
import xyz.brassgoggledcoders.minescribe.core.registry.*;
import xyz.brassgoggledcoders.minescribe.core.service.IRegistryProviderService;
import xyz.brassgoggledcoders.minescribe.core.util.Range;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;
import xyz.brassgoggledcoders.minescribe.editor.registry.EditorFormFieldRegistry;
import xyz.brassgoggledcoders.minescribe.editor.registry.EditorFormFieldTransform;
import xyz.brassgoggledcoders.minescribe.editor.registry.ScriptLoadedRegistry;
import xyz.brassgoggledcoders.minescribe.editor.registry.SerializerTypeRegistry;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control.*;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class EditorRegistryProviderService implements IRegistryProviderService {
    private static final BasicJsonRegistry<String, MineScribePackType> packTypes = BasicJsonRegistry.ofString(
            RegistryNames.PACK_TYPES,
            "pack_types",
            MineScribePackType.CODEC,
            MineScribePackType::name
    );
    private static final BasicJsonRegistry<String, PackRepositoryLocation> packRepositoryLocations = BasicJsonRegistry.ofString(
            RegistryNames.PACK_REPOSITORY_LOCATIONS,
            "pack_repositories",
            PackRepositoryLocation.CODEC,
            PackRepositoryLocation::label
    );
    private static final BasicJsonRegistry<ResourceId, PackContentParentType> contentParentTypes = new BasicJsonRegistry<>(
            RegistryNames.CONTENT_PARENT_TYPES,
            Path.of("types", "parent").toString(),
            ResourceId.CODEC,
            PackContentParentType.CODEC,
            PackContentType::getId
    );
    private static final BasicJsonRegistry<ResourceId, PackContentChildType> contentChildTypes = new BasicJsonRegistry<>(
            RegistryNames.CONTENT_CHILD_TYPES,
            Path.of("types", "child").toString(),
            ResourceId.CODEC,
            PackContentChildType.CODEC,
            PackContentType::getId
    );
    private static final LoadOnGetJsonRegistry<FormList> formLists = new LoadOnGetJsonRegistry<>(
            RegistryNames.FORM_LISTS,
            Path.of("form_lists"),
            FormList.CODEC
    );
    private static final BasicJsonRegistry<ResourceId, ObjectType> objectTypes = new BasicJsonRegistry<>(
            RegistryNames.OBJECT_TYPES,
            Path.of("types", "object").toString(),
            ResourceId.CODEC,
            ObjectType.CODEC,
            ObjectType::id
    );
    private static final SerializerTypeRegistry serializerTypes = new SerializerTypeRegistry();
    private static final ScriptLoadedRegistry<ResourceId, Codec<? extends Validation<?>>> validations = new ScriptLoadedRegistry<>(
            RegistryNames.VALIDATIONS,
            ResourceId.CODEC,
            Path.of("validation")
    );
    private static final EditorFormFieldRegistry editorFormFieldTransforms = new EditorFormFieldRegistry(registry -> {
        registry.accept("checkbox", new EditorFormFieldTransform<>(
                CheckBoxFileFieldDefinition.class,
                CheckBoxFieldControl::of
        ));
        registry.accept("string", new EditorFormFieldTransform<>(
                StringFileFieldDefinition.class,
                StringFieldControl::of
        ));
        registry.accept("list_selection", new EditorFormFieldTransform<>(
                ListSelectionFileFieldDefinition.class,
                ListSelectionFieldContent::of
        ));

        registry.accept("list_of_fields", new EditorFormFieldTransform<>(
                ListOfFileFieldDefinition.class,
                ListOfFieldFieldControl::of
        ));
        registry.accept("single_selection", new EditorFormFieldTransform<>(
                SingleSelectionFileFieldDefinition.class,
                SingleSelectionFieldControl::of
        ));
        registry.accept("integer", new EditorFormFieldTransform<>(
                IntegerFileFieldDefinition.class,
                integerFileField -> {
                    Range<Integer> range = integerFileField.getRange();
                    return new NumberFieldControl<>(
                            integerFileField,
                            Integer.class,
                            new SpinnerValueFactory.IntegerSpinnerValueFactory(
                                    range.min(),
                                    range.max(),
                                    range.start()
                            )
                    );
                }
        ));
        registry.accept("double", new EditorFormFieldTransform<>(
                DoubleFileFieldDefinition.class,
                doubleFileField -> {
                    Range<Double> doubleRange = doubleFileField.getRange();
                    return new NumberFieldControl<>(
                            doubleFileField,
                            Double.class,
                            new SpinnerValueFactory.DoubleSpinnerValueFactory(
                                    doubleRange.min(),
                                    doubleRange.max(),
                                    doubleRange.start()
                            )
                    );
                }
        ));
        registry.accept("object_ref", new EditorFormFieldTransform<>(
                ReferencedObjectFileFieldDefinition.class,
                ObjectFieldControl::of
        ));
    });

    private final Collection<String> registryNames;
    private final Collection<Registry<?, ?>> registries;

    public EditorRegistryProviderService() {
        this.registries = List.of(
                packTypes,
                packRepositoryLocations,
                formLists,
                validations,
                contentParentTypes,
                contentChildTypes,
                objectTypes,
                serializerTypes,
                editorFormFieldTransforms
        );
        this.registryNames = this.registries.stream()
                .map(Registry::getName)
                .toList();
    }


    @Override
    public Collection<String> getRegistryNames() {
        return this.registryNames;
    }


    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Optional<Registry<K, V>> getRegistry(String name) {
        for (Registry<?, ?> registry : registries) {
            if (registry.getName().equals(name)) {
                return Optional.of((Registry<K, V>) registry);
            }
        }
        return Optional.empty();
    }

    @Override
    public void load(Path mineScribeRoot) {
        Path dataRoot = mineScribeRoot.resolve("data");

        for (Registry<?, ?> registry : registries) {
            if (registry instanceof FileLoadedRegistry<?, ?> fileLoadedRegistry) {
                fileLoadedRegistry.load(dataRoot);
            }
        }
        formLists.setDataPath(dataRoot);

        this.validate();
    }

    @Override
    public void addSourcePath(Path sourcePath) {
        for (Registry<?, ?> registry : registries) {
            if (registry instanceof FileLoadedRegistry<?, ?> fileLoadedRegistry) {
                fileLoadedRegistry.load(sourcePath);
            }
        }
    }
}
