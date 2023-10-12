package xyz.brassgoggledcoders.minescribe.core.fileform;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.CheckBoxFileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldParser;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.ListSelectionFileField;
import xyz.brassgoggledcoders.minescribe.core.util.MineScribeJsonHelper;

import java.util.function.Supplier;

public class FileFieldTypeRegistry {
    private static final Supplier<FileFieldTypeRegistry> INSTANCE = Suppliers.memoize(FileFieldTypeRegistry::new);
    private final BiMap<String, IFileFieldParser<?>> fileFieldParsers;

    public FileFieldTypeRegistry() {
        this.fileFieldParsers = initializeFields();
    }

    private static BiMap<String, IFileFieldParser<?>> initializeFields() {
        BiMap<String, IFileFieldParser<?>> fields = HashBiMap.create();
        fields.put("checkbox", CheckBoxFileField.PARSER);
        fields.put("list_selection", ListSelectionFileField.PARSER);
        return fields;
    }

    public FileField parseField(JsonObject jsonObject) throws JsonParseException {
        String type = MineScribeJsonHelper.getAsString(jsonObject, "type");
        IFileFieldParser<?> parser = fileFieldParsers.get(type);
        if (parser != null) {
            return parser.fromJson(jsonObject);
        } else {
            throw new JsonParseException("Type " + type + " is not a value field type");
        }
    }

    public static FileFieldTypeRegistry getInstance() {
        return INSTANCE.get();
    }
}
