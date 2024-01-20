package xyz.brassgoggledcoders.minescribe.editor.service;

import com.google.inject.Module;
import com.google.inject.*;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackRepositoryLocation;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;
import xyz.brassgoggledcoders.minescribe.core.registry.RegistryNames;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;
import xyz.brassgoggledcoders.minescribe.editor.registry.BasicJsonRegistry;
import xyz.brassgoggledcoders.minescribe.editor.service.registry.IRegistryFactory;
import xyz.brassgoggledcoders.minescribe.editor.service.registry.RegistryFactoryImpl;

import java.nio.file.Path;

public class RegistryModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(IRegistryFactory.class).to(RegistryFactoryImpl.class);

        binder.requestStaticInjection(Registries.class);
    }

    @Inject
    @Singleton
    @Provides
    private Registry<ResourceId, PackRepositoryLocation> providePackRepositoryLocationRegistry() {
        return new BasicJsonRegistry<>(
                RegistryNames.PACK_REPOSITORY_LOCATIONS,
                "pack_repositories",
                PackRepositoryLocation.CODEC
        );
    }

    @Inject
    @Singleton
    @ProvidesIntoSet
    private Registry<?, ?> providePackRepositoryLocationRegistryToMap(Registry<ResourceId, PackRepositoryLocation> registry) {
        return registry;
    }

    @Inject
    @Singleton
    @Provides
    private Registry<ResourceId, Codec<? extends Validation<?>>> provideValidationCodecRegistry(IRegistryFactory registryFactory) {
        return registryFactory.createScriptRegistry(
                RegistryNames.VALIDATIONS,
                Path.of(RegistryNames.VALIDATIONS)
        );
    }

    @Inject
    @Singleton
    @ProvidesIntoSet
    private Registry<?, ?> provideValidationCodecRegistryToMap(Registry<ResourceId, Codec<? extends Validation<?>>> registry) {
        return registry;
    }

}
