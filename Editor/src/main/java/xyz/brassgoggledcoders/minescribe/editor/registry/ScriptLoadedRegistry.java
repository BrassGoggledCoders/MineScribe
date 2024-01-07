package xyz.brassgoggledcoders.minescribe.editor.registry;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.editor.javascript.ScriptHandler;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class ScriptLoadedRegistry<K, V> extends FileLoadedRegistry<K, V> {
    private final Multimap<Path, K> registeredPaths;

    public ScriptLoadedRegistry(String name, Codec<K> kCodec, Path directory) {
        super(name, kCodec, Path.of("scripts").resolve(directory).toString(), "js", null);
        this.registeredPaths = Multimaps.newMultimap(new HashMap<>(), HashSet::new);
        ScriptHandler.getInstance()
                .putBinding(name, this);
    }

    @Override
    public boolean register(K key, V value) {
        Path currentScript = ScriptHandler.getInstance()
                .getCurrentScript();
        if (currentScript != null) {
            Entry<Path, PathMatcher> sourcePath = this.findSourcePath(currentScript);
            if (sourcePath != null) {
                this.registeredPaths.put(
                        sourcePath.getKey()
                                .relativize(currentScript),
                        key
                );
            }
        }
        return super.register(key, value);
    }

    @Override
    protected int handleFileInFolder(Path path, ResourceId id, String fileContents) {
        int currentlyLoaded = this.getMap().size();
        ScriptHandler.getInstance()
                .runScript(path, fileContents);
        return this.getMap().size() - currentlyLoaded;
    }
}
