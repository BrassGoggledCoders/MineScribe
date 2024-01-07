package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import xyz.brassgoggledcoders.minescribe.core.MineScribeInfo;

import java.util.Locale;
import java.util.Objects;

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
                id.indexOf(':') != -1 ? id.substring(0, id.indexOf(':')) : "minescribe",
                id.indexOf(':') != -1 ? id.substring(id.indexOf(':') + 1).replace("\\", "/") : id.replace("\\", "/")
        );
    }

    @Override
    public String toString() {
        return this.namespace() + ":" + this.path();
    }

    public boolean matches(String value) {
        if (this.toString().equals(value)) {
            return true;
        } else if (value.indexOf(':') == -1){
            return this.path()
                    .equals(value);
        }
        return false;
    }

    public static DataResult<ResourceId> fromString(String s) {
        if (s.isEmpty()) {
            return DataResult.error("String is empty");
        } else {
            String[] strings = s.toLowerCase(Locale.ROOT)
                    .split(":");
            if (strings.length > 2 || strings.length == 0) {
                return DataResult.error("%s should have exact one :".formatted(s));
            } else if (strings.length == 2) {
                return DataResult.success(new ResourceId(strings[0], strings[1]));
            } else {
                return DataResult.success(new ResourceId(strings[0]));
            }
        }
    }
}
