package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public record ResourceId(
        String namespace,
        String path
) {

    public static final Codec<ResourceId> CODEC = Codec.STRING.flatXmap(
            ResourceId::fromString,
            resourceId -> DataResult.success(resourceId.toString())
    );

    public static final ResourceId NULL = new ResourceId("null", "null");

    public ResourceId(String id) {
        this(
                id.indexOf(':') != -1 ? id.substring(0, id.indexOf(':') - 1) : "minescribe",
                id.indexOf(':') != -1 ? id.substring(id.indexOf(':')) : id
        );
    }

    @Override
    public String toString() {
        return this.namespace() + ":" + this.path();
    }

    public static DataResult<ResourceId> fromString(String s) {
        if (s.isEmpty()) {
            return DataResult.error("String is empty");
        } else {
            String[] strings = s.split(":");
            if (strings.length != 2) {
                return DataResult.error("%s should have exact one :".formatted(s));
            } else {
                return DataResult.success(new ResourceId(strings[0], strings[1]));
            }
        }
    }
}
