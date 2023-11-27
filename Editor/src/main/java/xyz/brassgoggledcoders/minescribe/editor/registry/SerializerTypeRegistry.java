package xyz.brassgoggledcoders.minescribe.editor.registry;

import xyz.brassgoggledcoders.minescribe.core.packinfo.*;
import xyz.brassgoggledcoders.minescribe.core.registry.RegistryNames;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SerializerTypeRegistry extends BasicJsonRegistry<SerializerType> {
    public SerializerTypeRegistry() {
        super(
                RegistryNames.SERIALIZER_TYPES,
                Path.of("types", "serializer").toString(),
                SerializerType.CODEC
        );
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
