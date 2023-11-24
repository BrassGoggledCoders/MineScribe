package xyz.brassgoggledcoders.minescribe.core.service;

import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.*;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.DoubleFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.IntegerFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object.ReferencedObjectFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.registry.BasicStaticRegistry;
import xyz.brassgoggledcoders.minescribe.core.registry.RegistryNames;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class CoreRegistryProviderService implements IRegistryProviderService {

    private final BasicStaticRegistry<String, Codec<? extends IFileFieldDefinition>> FILE_FIELD_CODECS = new BasicStaticRegistry<>(
            RegistryNames.FILE_FIELD_DEFINITIONS,
            Codec.STRING,
            initializer -> {
                initializer.accept("checkbox", CheckBoxFileFieldDefinition.CODEC);
                initializer.accept("list_selection", ListSelectionFileFieldDefinition.CODEC);
                initializer.accept("list_of_fields", ListOfFileFieldDefinition.CODEC);
                initializer.accept("string", StringFileFieldDefinition.CODEC);
                initializer.accept("single_selection", SingleSelectionFileFieldDefinition.CODEC);
                initializer.accept("integer", IntegerFileFieldDefinition.CODEC);
                initializer.accept("double", DoubleFileFieldDefinition.CODEC);
                initializer.accept("object_ref", ReferencedObjectFileFieldDefinition.CODEC);
            }
    );

    @Override
    public Collection<String> getRegistryNames() {
        return Collections.singleton(FILE_FIELD_CODECS.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Optional<Registry<K, V>> getRegistry(String name) {
        if (name.equals(FILE_FIELD_CODECS.getName())) {
            return Optional.of((Registry<K, V>) FILE_FIELD_CODECS);
        }
        return Optional.empty();
    }

    @Override
    public void load(Path mineScribeRoot) {

    }

    @Override
    public void addSourcePath(Path sourcePath) {

    }
}
