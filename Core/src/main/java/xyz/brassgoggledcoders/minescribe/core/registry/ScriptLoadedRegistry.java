package xyz.brassgoggledcoders.minescribe.core.registry;

import com.mojang.serialization.Codec;
import org.graalvm.polyglot.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import xyz.brassgoggledcoders.minescribe.core.validation.ScriptHandler;

import java.nio.file.Path;

public class ScriptLoadedRegistry<K, V> extends FileLoadedRegistry<K, V> {
    private final Logger LOGGER = LoggerFactory.getLogger(ScriptLoadedRegistry.class);

    public ScriptLoadedRegistry(String name, Codec<K> kCodec, Path directory) {
        super(name, kCodec, directory, "js");
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
        ScriptHandler.getInstance().runScript(fileContents);
    }
}
