package xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy;

import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.packinfo.*;
import xyz.brassgoggledcoders.minescribe.core.registry.Holder;
import xyz.brassgoggledcoders.minescribe.editor.registry.EditorRegistries;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public record NodeTracker(
        Holder<ResourceId, PackContentParentType> parentTypeHolder,
        Optional<Holder<ResourceId, PackContentChildType>> childTypeHolderOpt,
        int depth
) {
    @NotNull
    public Collection<NodeTracker> advancePath(Path path) {
        PackContentParentType parentType = this.parentTypeHolder()
                .get();

        if (parentType != null) {
            Path parentPath = parentType.getPath();
            if (depth >= parentPath.getNameCount()) {
                PackContentChildType packContentChildType = this.childTypeHolderOpt.flatMap(Holder::getOpt)
                        .orElse(null);
                if (packContentChildType != null) {
                    int childDepth = this.depth() - parentPath.getNameCount();
                    Path childSubPath = packContentChildType.getPath();
                    if (childDepth > 0 && childDepth < childSubPath.getNameCount()) {
                        childSubPath = childSubPath.subpath(childDepth, childSubPath.getNameCount());
                    }
                    if (childDepth >= childSubPath.getNameCount() || childSubPath.startsWith(path)) {
                        return Collections.singletonList(new NodeTracker(
                                this.parentTypeHolder(),
                                this.childTypeHolderOpt(),
                                depth + 1
                        ));
                    }
                } else if (depth == parentPath.getNameCount()) {
                    return createNodeTracksForChildren(path);
                } else {
                    return Collections.singletonList(new NodeTracker(
                            this.parentTypeHolder(),
                            Optional.empty(),
                            depth + 1
                    ));
                }
            } else {
                Path parentSubPath = parentPath.subpath(depth, parentPath.getNameCount());
                if (parentSubPath.startsWith(path)) {
                    return Collections.singletonList(new NodeTracker(
                            this.parentTypeHolder(),
                            Optional.empty(),
                            depth + 1
                    ));
                }
            }
        }

        return Collections.emptyList();
    }

    @NotNull
    private List<NodeTracker> createNodeTracksForChildren(Path path) {
        List<NodeTracker> nodeTrackerList = new ArrayList<>();
        for (Holder<ResourceId, PackContentChildType> childTypeHolder : EditorRegistries.getContentChildTypes().getHolders()) {
            ResourceId parentId = this.parentTypeHolder.getKey();
            if (childTypeHolder.exists(childType -> childType.getParentId().equals(parentId) && childType.getPath().startsWith(path))) {
                nodeTrackerList.add(new NodeTracker(
                        this.parentTypeHolder(),
                        Optional.of(childTypeHolder),
                        depth + 1
                ));
            }
        }
        if (nodeTrackerList.isEmpty()) {
            nodeTrackerList.add(new NodeTracker(
                    this.parentTypeHolder(),
                    Optional.empty(),
                    depth + 1
            ));
        }
        return nodeTrackerList;
    }

    public Optional<FileForm> getForm() {
        return this.parentTypeHolder()
                .getOpt()
                .flatMap(PackContentType::getForm)
                .or(() -> this.childTypeHolderOpt()
                        .flatMap(Holder::getOpt)
                        .flatMap(PackContentType::getForm)
                );
    }

    public List<IFullName> getFullNames() {
        return Stream.of(
                        this.parentTypeHolder().getOpt(),
                        this.childTypeHolderOpt.flatMap(Holder::getOpt)
                )
                .<IFullName>flatMap(Optional::stream)
                .toList();
    }
}
