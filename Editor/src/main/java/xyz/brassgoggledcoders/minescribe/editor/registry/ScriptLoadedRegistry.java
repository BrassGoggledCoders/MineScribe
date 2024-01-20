package xyz.brassgoggledcoders.minescribe.editor.registry;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.editor.javascript.ScriptHandler;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class ScriptLoadedRegistry<V> extends FileLoadedRegistry<ResourceId, V> {
    private final Multimap<Path, ResourceId> registeredPaths;

    private final ScriptHandler scriptHandler;

    public ScriptLoadedRegistry(String name, Path directory, ScriptHandler scriptHandler) {
        super(name, ResourceId.CODEC, Path.of("scripts").resolve(directory).toString(), "js", null);
        this.scriptHandler = scriptHandler;
        this.registeredPaths = Multimaps.newMultimap(new HashMap<>(), HashSet::new);
        scriptHandler.putBinding(name, this);
    }

    @Override
    public boolean register(ResourceId key, V value) {
        Path currentScript = scriptHandler.getCurrentScript();
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
        scriptHandler.runScript(path, fileContents);
        return this.getMap().size() - currentlyLoaded;
    }
}
