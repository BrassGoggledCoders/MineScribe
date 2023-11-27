package xyz.brassgoggledcoders.minescribe.core.util;

import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public record IdPath(
        Path namespacePath,
        Path typePath
) {
    public List<ResourceId> getChildIds() throws IOException {
        List<ResourceId> childIds = new ArrayList<>();
        if (Files.isDirectory(typePath)) {
            childIds.addAll(getIdsFromFolder(typePath));
        }

        return childIds;
    }

    private List<ResourceId> getIdsFromFolder(Path folderPath) throws IOException {
        List<ResourceId> resourceIds = new ArrayList<>();
        try (DirectoryStream<Path> childPaths = Files.newDirectoryStream(folderPath)) {
            for (Path childPath : childPaths) {
                if (Files.isDirectory(childPath)) {
                    resourceIds.addAll(getIdsFromFolder(childPath));
                } else {
                    String relativePath = this.typePath()
                            .relativize(childPath)
                            .toString();

                    int indexOf = relativePath.lastIndexOf(".");
                    relativePath = relativePath.substring(0, indexOf);
                    resourceIds.add(new ResourceId(
                            this.namespacePath()
                                    .getFileName()
                                    .toString(),
                            relativePath.replace("\\", "/")
                    ));
                }
            }
        }
        return resourceIds;
    }
}
