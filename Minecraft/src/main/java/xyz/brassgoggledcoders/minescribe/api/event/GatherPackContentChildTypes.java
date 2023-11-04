package xyz.brassgoggledcoders.minescribe.api.event;

import xyz.brassgoggledcoders.minescribe.api.data.PackContentChildData;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentChildType;

public class GatherPackContentChildTypes extends GatherMineScribeRegistryObjectEvent<PackContentChildData, PackContentChildType> {
    public GatherPackContentChildTypes() {
        super(PackContentChildData::id, PackContentChildData::toType);
    }
}
