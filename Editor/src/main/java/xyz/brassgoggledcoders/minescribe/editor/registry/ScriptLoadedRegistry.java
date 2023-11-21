package xyz.brassgoggledcoders.minescribe.editor.registry;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.registry.FileLoadedRegistry;
import xyz.brassgoggledcoders.minescribe.editor.javascript.ScriptHandler;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;

public class ScriptLoadedRegistry<K, V> extends FileLoadedRegistry<K, V> {
    private final Multimap<Path, K> registeredPaths;

    public ScriptLoadedRegistry(String name, Codec<K> kCodec, Path directory) {
        super(name, kCodec, Path.of("scripts").resolve(directory).toString(), "js");
        this.registeredPaths = Multimaps.newMultimap(new HashMap<>(), HashSet::new);
        ScriptHandler.getInstance()
                .putBinding(name, this);
    }

    @Override
    public void register(K key, V value) {
        Path currentScript = ScriptHandler.getInstance()
                .getCurrentScript();
        if (currentScript != null) {
            Path sourcePath = this.findSourcePath(currentScript);
            if (sourcePath != null) {
                this.registeredPaths.put(
                        sourcePath.relativize(currentScript),
                        key
                );
            }
        }
        super.register(key, value);
    }

    @Override
    protected void handleFileInFolder(Path path, String fileName, String fileContents) {
        ScriptHandler.getInstance()
                .runScript(path, fileContents);
    }
}
