package xyz.brassgoggledcoders.minescribe.schema.subschema;

import com.google.gson.annotations.SerializedName;
import xyz.brassgoggledcoders.minescribe.api.schema.ISchema;
import xyz.brassgoggledcoders.minescribe.schema.SchemaType;

import java.util.Collection;
import java.util.Objects;

public record StringEnumSchema(
        @SerializedName("enum")
        String[] values
) implements ISchema {
    @Override
    @SerializedName("type")
    public String getType() {
        return SchemaType.STRING.getName();
    }

    public static <T> StringEnumSchema of(Collection<T> collection) {
        return new StringEnumSchema(collection.stream()
                .map(Objects::toString)
                .toArray(String[]::new)
        );
    }
}
