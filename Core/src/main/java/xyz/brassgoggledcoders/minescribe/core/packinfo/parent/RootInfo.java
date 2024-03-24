package xyz.brassgoggledcoders.minescribe.core.packinfo.parent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

import java.util.Optional;

public record RootInfo(
        RootType type,
        Optional<ResourceId> id
) {
    public static final Codec<RootInfo> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RootType.CODEC.fieldOf(JsonFieldNames.TYPE).forGetter(RootInfo::type),
            ResourceId.CODEC.optionalFieldOf(JsonFieldNames.ID).forGetter(RootInfo::id)
    ).apply(inst, RootInfo::new));

    public static final RootInfo NAMESPACE = new RootInfo(
            RootType.NAMESPACE,
            Optional.empty()
    );

    public static final RootInfo PACK = new RootInfo(
            RootType.PACK
    );

    public RootInfo(RootType type) {
        this(type, Optional.empty());
    }

    public RootInfo(RootType type, @Nullable ResourceId id) {
        this(type, Optional.ofNullable(id));
    }

    public boolean matches(RootType otherType, @Nullable ResourceId otherId) {
        if (otherType == this.type) {
            return this.id().equals(Optional.ofNullable(otherId));
        }

        return false;
    }
}
