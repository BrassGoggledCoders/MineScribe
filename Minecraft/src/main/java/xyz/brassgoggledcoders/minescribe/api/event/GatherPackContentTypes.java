package xyz.brassgoggledcoders.minescribe.api.event;

import xyz.brassgoggledcoders.minescribe.api.data.PackContentData;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;

public class GatherPackContentTypes extends GatherMineScribeRegistryObjectEvent<PackContentData, PackContentType> {
    public GatherPackContentTypes() {
        super(PackContentData::id, PackContentData::toType);
    }
}
