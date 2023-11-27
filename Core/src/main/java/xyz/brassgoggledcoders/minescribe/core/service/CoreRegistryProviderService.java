package xyz.brassgoggledcoders.minescribe.core.service;

import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.*;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.DoubleFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number.IntegerFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object.ReferencedObjectFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.FileIdFormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.IFormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.RegistryFormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.formlist.ValueFormList;
import xyz.brassgoggledcoders.minescribe.core.registry.BasicStaticRegistry;
import xyz.brassgoggledcoders.minescribe.core.registry.RegistryNames;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

    private final BasicStaticRegistry<String, Codec<? extends IFormList<?>>> FORM_LIST_CODECS = new BasicStaticRegistry<>(
            RegistryNames.FORM_LISTS,
            Codec.STRING,
            register -> {
                register.accept("registry", RegistryFormList.CODEC);
                register.accept("file_name", FileIdFormList.CODEC);
                register.accept("list", ValueFormList.CODEC);
            }
    );

    @Override
    public Collection<String> getRegistryNames() {
        return List.of(FILE_FIELD_CODECS.getName(), FORM_LIST_CODECS.getName());
    }

    @Override
    public Collection<? extends Registry<?, ?>> getRegistries() {
        return List.of(FILE_FIELD_CODECS, FORM_LIST_CODECS);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Optional<Registry<K, V>> getRegistry(String name) {
        if (name.equals(FILE_FIELD_CODECS.getName())) {
            return Optional.of((Registry<K, V>) FILE_FIELD_CODECS);
        } else if (name.equals(FORM_LIST_CODECS.getName())) {
            return Optional.of((Registry<K, V>) FORM_LIST_CODECS);
        }
        return Optional.empty();
    }

}
