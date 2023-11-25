package xyz.brassgoggledcoders.minescribe.core.registry;

import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.fileform.FormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.packinfo.MineScribePackType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackRepositoryLocation;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.service.IRegistryProviderService;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;

import java.util.Optional;
import java.util.ServiceLoader;

public class Registries {
    public static final ServiceLoader<IRegistryProviderService> REGISTRY_PROVIDER_SERVICE_LOADER =
            ServiceLoader.load(IRegistryProviderService.class);

    public static Registry<String, PackRepositoryLocation> getPackRepositoryLocationRegistry() {
        return getRegistry(RegistryNames.PACK_REPOSITORY_LOCATIONS);
    }

    public static Registry<ResourceId, Codec<? extends Validation<?>>> getValidationCodecRegistry() {
        return getRegistry(RegistryNames.VALIDATIONS);
    }

    public static Registry<String, MineScribePackType> getPackTypeRegistry() {
        return getRegistry(RegistryNames.PACK_TYPES);
    }

    public static Registry<String, Codec<? extends IFileFieldDefinition>> getFileFieldDefinitionCodecRegistry() {
        return getRegistry(RegistryNames.FILE_FIELD_DEFINITIONS);
    }

    public static Registry<ResourceId, FormList> getFormListValues() {
        return Registries.getRegistry(RegistryNames.FORM_LIST_VALUES);
    }

    public static <K, V> Registry<K, V> getRegistry(String name) {
        return Registries.<K, V>getRegistryOpt(name)
                .orElseThrow();
    }

    public static <K, V> Optional<Registry<K, V>> getRegistryOpt(String name) {
        return REGISTRY_PROVIDER_SERVICE_LOADER.stream()
                .flatMap(registryProvider -> registryProvider.get()
                        .<K,V>getRegistry(name)
                        .stream()
                )
                .findFirst();
    }
}
