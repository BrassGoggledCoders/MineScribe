package xyz.brassgoggledcoders.minescribe.core.util;

import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record FolderCollection(List<IdPath> paths) {
    public List<ResourceId> getFileResourceIds() throws IOException {
        List<ResourceId> ids = new ArrayList<>();
        for (IdPath idPath: paths) {
            ids.addAll(idPath.getChildIds());
        }
        return ids;
    }
}
