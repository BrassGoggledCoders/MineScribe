package xyz.brassgoggledcoders.minescribe.editor.registry;

import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.registry.FileLoadedRegistry;
import xyz.brassgoggledcoders.minescribe.editor.javascript.ScriptHandler;

import java.nio.file.Path;

public class ScriptLoadedRegistry<K, V> extends FileLoadedRegistry<K, V> {

    public ScriptLoadedRegistry(String name, Codec<K> kCodec, Path directory) {
        super(name, kCodec, Path.of("scripts").resolve(directory), "js");
        ScriptHandler.getInstance()
                .putBinding(name, this);
    }

    @Override
    protected void handleSingleFile(String fileContents) {
        //Do Nothing, Shouldn't happen
    }

    @Override
    public void register(K key, V value) {
        super.register(key, value);
    }

    @Override
    protected void handleFileInFolder(String fileName, String fileContents) {
        ScriptHandler.getInstance()
                .runScript(fileName, fileContents);
    }
}
