package xyz.brassgoggledcoders.minescribe.core.fileform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public class FileForm {
    public static final Codec<FileForm> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FileFieldTypeRegistry.LIST_CODEC.fieldOf("fields").forGetter(FileForm::getFieldsForCodec)
    ).apply(instance, FileForm::new));
    private final TreeSet<IFileField> fields;

    public FileForm(Collection<IFileField> fields) {
        this.fields = new TreeSet<>(Comparable::compareTo);
        this.fields.addAll(fields);
    }

    public Collection<IFileField> getFields() {
        return fields;
    }

    private List<IFileField> getFieldsForCodec() {
        return new ArrayList<>(this.getFields());
    }
}
