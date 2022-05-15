package xyz.brassgoggledcoders.minescribe.schema;

public enum SchemaType {
    STRING("string"),
    ARRAY("array"),
    NUMBER("number"),
    OBJECT("object"),
    BOOLEAN("boolean");

    private final String name;

    SchemaType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
