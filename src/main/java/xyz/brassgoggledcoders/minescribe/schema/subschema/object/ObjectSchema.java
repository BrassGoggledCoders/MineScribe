package xyz.brassgoggledcoders.minescribe.schema.subschema.object;

import xyz.brassgoggledcoders.minescribe.api.schema.ISchema;
import xyz.brassgoggledcoders.minescribe.schema.SchemaType;

public class ObjectSchema implements ISchema {
    @Override
    public String getType() {
        return SchemaType.OBJECT.getName();
    }
}
