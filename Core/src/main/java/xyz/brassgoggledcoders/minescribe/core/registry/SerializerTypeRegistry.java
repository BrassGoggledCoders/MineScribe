package xyz.brassgoggledcoders.minescribe.core.registry;

import xyz.brassgoggledcoders.minescribe.core.packinfo.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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

    public List<SerializerType> getFor(PackContentChildType childType) {
        return getFor(new ResourceId(
                childType.getId().namespace(),
                "types/child/" + childType.getId().path()
        ));
    }

    public List<SerializerType> getFor(ObjectType objectType) {
        return getFor(new ResourceId(
                objectType.id().namespace(),
                "types/object/" + objectType.id().path()
        ));
    }

    public List<SerializerType> getFor(SerializerType serializerType) {
        return getFor(new ResourceId(
                serializerType.id().namespace(),
                "types/serializer/" + serializerType.id().path()
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

    public Supplier<List<SerializerType>> supplyList(IFullName... values) {
        return () -> {
            List<SerializerType> serializerTypes = new ArrayList<>();
            for (IFullName fullName : values) {
                if (fullName != null) {
                    serializerTypes.addAll(this.getFor(fullName.getFullName()));
                }
            }
            return serializerTypes;
        };
    }
}
