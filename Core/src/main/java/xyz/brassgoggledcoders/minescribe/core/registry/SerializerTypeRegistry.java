package xyz.brassgoggledcoders.minescribe.core.registry;

import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentChildType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentParentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.packinfo.SerializerType;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SerializerTypeRegistry extends BasicJsonRegistry<ResourceId, SerializerType> {
    public SerializerTypeRegistry() {
        super(
                "serializerTypes",
                Path.of("types", "serializer"),
                ResourceId.CODEC,
                SerializerType.CODEC,
                SerializerType::id
        );
    }

    public List<SerializerType> getFor(PackContentParentType parentType) {
        return getFor(new ResourceId(
                parentType.getId().namespace(),
                "types/parent/" + parentType.getId().path()
        ));
    }

    public List<SerializerType> getFor(PackContentChildType parentType) {
        return getFor(new ResourceId(
                parentType.getId().namespace(),
                "types/child/" + parentType.getId().path()
        ));
    }

    public List<SerializerType> getFor(ResourceId parentId) {
        List<SerializerType> serializerTypes = new ArrayList<>();

        for (SerializerType serializerType : this.getValues()) {
            if (serializerType.parentId().equals(parentId)) {
                serializerTypes.add(serializerType);
            }
        }

        return serializerTypes;
    }

    public SerializerType getForSerializerId(ResourceId resourceId) {
        for (SerializerType type: this.getValues()) {
            if (type.serializerId().equals(resourceId)) {
                return type;
            }
        }
        return null;
    }
}
