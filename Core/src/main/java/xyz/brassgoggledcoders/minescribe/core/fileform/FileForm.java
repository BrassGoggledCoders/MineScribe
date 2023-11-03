package xyz.brassgoggledcoders.minescribe.core.fileform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;

import java.util.*;

public class FileForm {
    public static final Codec<FileForm> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FileField.LIST_CODEC.fieldOf("fields").forGetter(FileForm::getFieldsForCodec),
            SerializerInfo.CODEC.optionalFieldOf("serializer").forGetter(FileForm::getSerializer)
    ).apply(instance, (fields, serializer) -> new FileForm(fields, serializer.orElse(null))));

    private final SerializerInfo serializer;
    private final TreeSet<FileField> fields;

    public FileForm(Collection<FileField> fields, SerializerInfo serializer) {
        this.serializer = serializer;
        this.fields = new TreeSet<>(Comparable::compareTo);
        this.fields.addAll(fields);
    }

    public Collection<FileField> getFields() {
        return fields;
    }

    private List<FileField> getFieldsForCodec() {
        return new ArrayList<>(this.getFields());
    }

    public Optional<SerializerInfo> getSerializer() {
        return Optional.ofNullable(this.serializer);
    }

    public static FileForm of(FileField... fileFields) {
        return new FileForm(Arrays.asList(fileFields), null);
    }

    public static FileForm of(SerializerInfo serializerField, FileField... fileFields) {
        return new FileForm(Arrays.asList(fileFields), serializerField);
    }
}
