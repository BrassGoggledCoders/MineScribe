package xyz.brassgoggledcoders.minescribe.editor.service;

import javafx.scene.control.SpinnerValueFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.FormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.*;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.DoubleFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.IntegerFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object.ReferencedObjectFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.packinfo.*;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;
import xyz.brassgoggledcoders.minescribe.core.registry.RegistryNames;
import xyz.brassgoggledcoders.minescribe.core.service.IRegistryProviderService;
import xyz.brassgoggledcoders.minescribe.core.util.Range;
import xyz.brassgoggledcoders.minescribe.editor.registry.*;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control.*;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class EditorRegistryProviderService implements IRegistryProviderService {
    private static final BasicJsonRegistry<MineScribePackType> packTypes = new BasicJsonRegistry<>(
            RegistryNames.PACK_TYPES,
            "pack_types",
            MineScribePackType.CODEC,
            MineScribePackType::name
    );
    private static final BasicJsonRegistry<PackRepositoryLocation> packRepositoryLocations = new BasicJsonRegistry<>(
            RegistryNames.PACK_REPOSITORY_LOCATIONS,
            "pack_repositories",
            PackRepositoryLocation.CODEC
    );
    private static final BasicJsonRegistry<PackContentParentType> contentParentTypes = new BasicJsonRegistry<>(
            RegistryNames.CONTENT_PARENT_TYPES,
            Path.of("types", "parent").toString(),
            PackContentParentType.CODEC
    );
    private static final BasicJsonRegistry<PackContentChildType> contentChildTypes = new BasicJsonRegistry<>(
            RegistryNames.CONTENT_CHILD_TYPES,
            Path.of("types", "child").toString(),
            PackContentChildType.CODEC
    );
    private static final BasicJsonRegistry<FormList> formValueLists = new BasicJsonRegistry<>(
            RegistryNames.FORM_LIST_VALUES,
            Path.of("form_lists").toString(),
            FormList.CODEC
    );
    private static final BasicJsonRegistry<ObjectType> objectTypes = new BasicJsonRegistry<>(
            RegistryNames.OBJECT_TYPES,
            Path.of("types", "object").toString(),
            ObjectType.CODEC
    );
    private static final SerializerTypeRegistry serializerTypes = new SerializerTypeRegistry();

    private static final FolderContentsRegistry FOLDER_COLLECTION_REGISTRY = new FolderContentsRegistry();

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
        registry.accept("multiple_selection", new EditorFormFieldTransform<>(
                MultiSelectionFileFieldDefinition.class,
                MultiSelectionFieldContent::of
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
    private final Collection<Registry<?, ?>> registries;

    public EditorRegistryProviderService() {
        this.registries = List.of(
                packTypes,
                packRepositoryLocations,
                formValueLists,
                contentParentTypes,
                contentChildTypes,
                objectTypes,
                serializerTypes,
                editorFormFieldTransforms,
                FOLDER_COLLECTION_REGISTRY
        );
    }

    @Override
    public Collection<Registry<?, ?>> getRegistries() {
        return registries;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Optional<Registry<K, V>> getRegistry(String name) {
        for (Registry<?, ?> registry : registries) {
            if (registry.getId().equals(name)) {
                return Optional.of((Registry<K, V>) registry);
            }
        }
        return Optional.empty();
    }

    @Override
    public void load(Path mineScribeRoot) {
        Path dataRoot = mineScribeRoot.resolve("minescribe");

        for (Registry<?, ?> registry : registries) {
            if (registry instanceof FileLoadedRegistry<?, ?> fileLoadedRegistry) {
                fileLoadedRegistry.load(dataRoot);
            }
        }
        //formValueLists.setDataPath(dataRoot);

        this.validate();
    }

    @Override
    public void addSourcePath(Path sourcePath) {
        for (Registry<?, ?> registry : registries) {
            if (registry instanceof ISourceRootListener sourceRootListener) {
                sourceRootListener.addSourceRoot(sourcePath);
            }
        }
    }
}
