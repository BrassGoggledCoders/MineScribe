package xyz.brassgoggledcoders.minescribe.editor.service.registry;

import xyz.brassgoggledcoders.minescribe.editor.registry.ScriptLoadedRegistry;

import java.nio.file.Path;

public interface IRegistryFactory {
    <V> ScriptLoadedRegistry<V> createScriptRegistry(String name, Path path);
}
