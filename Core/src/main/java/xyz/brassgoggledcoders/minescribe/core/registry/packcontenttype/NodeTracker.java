package xyz.brassgoggledcoders.minescribe.core.registry.packcontenttype;

import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentChildType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentParentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.nio.file.Path;
import java.util.*;

public record NodeTracker(
        PackContentParentType parentType,
        Optional<PackContentChildType> childTypeOpt,
        int depth
) {
    @NotNull
    public Collection<NodeTracker> advancePath(Path path) {
        Path parentPath = this.parentType().getPath();
        if (depth >= parentPath.getNameCount()) {
            if (this.childTypeOpt().isPresent()) {
                PackContentChildType childType = this.childTypeOpt().get();
                int childDepth = this.depth() - parentPath.getNameCount();
                Path childSubPath = childType.getPath();
                if (childDepth > 0 && childDepth < childSubPath.getNameCount()) {
                    childSubPath = childSubPath.subpath(childDepth, childSubPath.getNameCount());
                }
                if (childDepth > childSubPath.getNameCount() || childSubPath.startsWith(path)) {
                    return Collections.singletonList(new NodeTracker(
                            this.parentType(),
                            this.childTypeOpt(),
                            depth + 1
                    ));
                }
            } else {
                return createNodeTracksForChildren(path);
            }
        } else {
            Path parentSubPath = parentPath.subpath(depth, parentPath.getNameCount());
            if (parentSubPath.startsWith(path)) {
                return Collections.singletonList(new NodeTracker(
                        this.parentType(),
                        Optional.empty(),
                        depth + 1
                ));
            }
        }
        return Collections.emptyList();
    }

    @NotNull
    private List<NodeTracker> createNodeTracksForChildren(Path path) {
        List<NodeTracker> nodeTrackerList = new ArrayList<>();
        for (PackContentChildType childType : Registries.getContentChildTypes()) {
            if (childType.getParentId().equals(this.parentType().getId()) && childType.getPath().startsWith(path)) {
                nodeTrackerList.add(new NodeTracker(
                        this.parentType(),
                        Optional.of(childType),
                        depth + 1
                ));
            }
        }
        return nodeTrackerList;
    }

    public Optional<FileForm> getForm() {
        return this.parentType()
                .getForm()
                .or(() -> this.childTypeOpt()
                        .flatMap(PackContentType::getForm)
                );
    }
}
