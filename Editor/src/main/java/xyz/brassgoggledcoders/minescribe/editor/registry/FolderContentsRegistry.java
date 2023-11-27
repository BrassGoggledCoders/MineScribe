package xyz.brassgoggledcoders.minescribe.editor.registry;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.mojang.serialization.Codec;
import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.Nullable;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;
import xyz.brassgoggledcoders.minescribe.core.registry.RegistryNames;
import xyz.brassgoggledcoders.minescribe.core.util.FolderCollection;
import xyz.brassgoggledcoders.minescribe.core.util.IdPath;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.file.FileUpdate;
import xyz.brassgoggledcoders.minescribe.editor.scene.editortree.EditorItem;
import xyz.brassgoggledcoders.minescribe.editor.scene.editortree.NamespaceEditorItem;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.StandardWatchEventKinds;
import java.util.*;

public class FolderContentsRegistry extends Registry<String, FolderCollection> implements IFileUpdateListener, ISourceRootListener {
    private final Multimap<Path, String> watchedPaths;
    private final Set<Path> sourceRoots;

    public FolderContentsRegistry() {
        super(RegistryNames.FOLDER_COLLECTIONS, Codec.STRING);
        this.watchedPaths = Multimaps.newMultimap(new HashMap<>(), HashSet::new);
        this.sourceRoots = new HashSet<>();
    }

    @Override
    public FolderCollection getValue(String key) {
        if (!this.getMap().containsKey(key)) {
            this.load(key);
        }
        return super.getValue(key);
    }

    private void load(String pathMatcherString) {
        List<TreeItem<EditorItem>> repositoryRoots = FileHandler.getInstance()
                .getRootModel()
                .getChildren();
        List<IdPath> matchingPaths = new ArrayList<>();
        for (TreeItem<EditorItem> repositoryRoot : repositoryRoots) {
            if (repositoryRoot.getValue() != null) {
                PathMatcher pathMatcher = repositoryRoot.getValue()
                        .getPath()
                        .getFileSystem()
                        .getPathMatcher("glob:" + pathMatcherString);

                matchingPaths.addAll(checkFolder(repositoryRoot, pathMatcher, null));
            }
        }
        this.register(pathMatcherString, new FolderCollection(matchingPaths));
    }

    private List<IdPath> checkFolder(TreeItem<EditorItem> treeItem, PathMatcher pathMatcher, @Nullable Path namespacePath) {
        EditorItem editorItem = treeItem.getValue();
        if (editorItem != null) {
            if (namespacePath == null && editorItem instanceof NamespaceEditorItem namespaceEditorItem) {
                namespacePath = namespaceEditorItem.getPath();
            }
            if (namespacePath != null && pathMatcher.matches(editorItem.getPath())) {
                this.watchedPaths.put(editorItem.getPath(), pathMatcher.toString());
                return Collections.singletonList(new IdPath(
                        namespacePath,
                        editorItem.getPath()
                ));
            } else if (!treeItem.getChildren().isEmpty()) {
                List<IdPath> matchedPaths = new ArrayList<>();
                for (TreeItem<EditorItem> childItem : treeItem.getChildren()) {
                    matchedPaths.addAll(checkFolder(childItem, pathMatcher, namespacePath));
                }
                return matchedPaths;
            }
        }

        return Collections.emptyList();
    }

    @Override
    public void fileUpdated(FileUpdate fileUpdate) {
        if (fileUpdate.eventKind() == StandardWatchEventKinds.ENTRY_CREATE) {
            if (Files.isDirectory(fileUpdate.path())) {
                for (String pathMatcherString : this.getKeys()) {
                    PathMatcher pathMatcher = fileUpdate.path()
                            .getFileSystem()
                            .getPathMatcher("glob:" + pathMatcherString);
                    if (pathMatcher.matches(fileUpdate.path())) {
                        Queue<TreeItem<EditorItem>> treeItems = FileHandler.getInstance()
                                .getNodePath(fileUpdate.path());
                        for (TreeItem<EditorItem> treeItem : treeItems) {
                            if (treeItem.getValue() instanceof NamespaceEditorItem namespaceEditorItem) {
                                this.getOptionalValue(pathMatcherString)
                                        .ifPresent(folderCollection -> folderCollection.paths()
                                                .add(new IdPath(namespaceEditorItem.getPath(), fileUpdate.path()))
                                        );
                            }
                        }
                    }
                }
            }
        }


    }

    @Override
    public void addSourceRoot(Path sourceRoot) {
        if (this.sourceRoots.add(sourceRoot)) {
            this.getMap()
                    .clear();
        }
    }
}
