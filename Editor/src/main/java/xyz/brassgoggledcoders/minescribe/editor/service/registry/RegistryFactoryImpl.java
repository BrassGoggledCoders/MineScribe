package xyz.brassgoggledcoders.minescribe.editor.service.registry;

import com.google.inject.Inject;
import com.google.inject.Provider;
import xyz.brassgoggledcoders.minescribe.editor.javascript.ScriptHandler;
import xyz.brassgoggledcoders.minescribe.editor.registry.ScriptLoadedRegistry;

import java.nio.file.Path;

public class RegistryFactoryImpl implements IRegistryFactory {
    private final Provider<ScriptHandler> scriptHandlerProvider;

    @Inject
    public RegistryFactoryImpl(Provider<ScriptHandler> scriptHandlerProvider) {
        this.scriptHandlerProvider = scriptHandlerProvider;
    }

    @Override
    public <V> ScriptLoadedRegistry<V> createScriptRegistry(String name, Path path) {
        return new ScriptLoadedRegistry<>(
                name,
                path,
                scriptHandlerProvider.get()
        );
    }
}
