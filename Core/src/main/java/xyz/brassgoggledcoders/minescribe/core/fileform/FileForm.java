package xyz.brassgoggledcoders.minescribe.core.fileform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;

import java.util.*;

public class FileForm {
    public static final Codec<FileForm> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IFileField.SET_CODEC.fieldOf("fields").forGetter(FileForm::getFieldsForCodec)
    ).apply(instance, FileForm::new));
    private final TreeSet<IFileField> fields;

    public FileForm(Collection<IFileField> fields) {
        this.fields = new TreeSet<>(Comparable::compareTo);
        this.fields.addAll(fields);
    }

    public Collection<IFileField> getFields() {
        return fields;
    }

    private Set<IFileField> getFieldsForCodec() {
        return new HashSet<>(this.getFields());
    }
}
