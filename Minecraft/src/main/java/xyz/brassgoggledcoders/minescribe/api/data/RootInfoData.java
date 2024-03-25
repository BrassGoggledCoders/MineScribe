package xyz.brassgoggledcoders.minescribe.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootInfo;
import xyz.brassgoggledcoders.minescribe.core.packinfo.parent.RootType;

import java.util.Optional;

public record RootInfoData(
        RootType type,
        Optional<ResourceLocation> id
) {
    public static final Codec<RootInfoData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RootType.CODEC.fieldOf(JsonFieldNames.TYPE).forGetter(RootInfoData::type),
            ResourceLocation.CODEC.optionalFieldOf(JsonFieldNames.ID).forGetter(RootInfoData::id)
    ).apply(instance, RootInfoData::new));

    public RootInfoData(RootType rootType) {
        this(rootType, Optional.empty());
    }

    public RootInfoData(RootType rootType, ResourceLocation resourceLocation) {
        this(rootType, Optional.of(resourceLocation));
    }

    public RootInfo toInfo() {
        return new RootInfo(
                this.type(),
                this.id()
                        .map(rId -> new ResourceId(
                                rId.getNamespace(),
                                rId.getPath()
                        ))
        );
    }
}
