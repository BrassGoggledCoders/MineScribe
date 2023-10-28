package xyz.brassgoggledcoders.minescribe.core.fileform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;

import java.util.*;

public class FileForm {
    public static final Codec<FileForm> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IFileField.LIST_CODEC.fieldOf("fields").forGetter(FileForm::getFieldsForCodec),
            SerializerInfo.CODEC.optionalFieldOf("serializer").forGetter(FileForm::getSerializer)
    ).apply(instance, (fields, serializer) -> new FileForm(fields, serializer.orElse(null))));

    private final SerializerInfo serializer;
    private final TreeSet<IFileField> fields;

    public FileForm(Collection<IFileField> fields, SerializerInfo serializer) {
        this.serializer = serializer;
        this.fields = new TreeSet<>(Comparable::compareTo);
        this.fields.addAll(fields);
    }

    public Collection<IFileField> getFields() {
        return fields;
    }

    private List<IFileField> getFieldsForCodec() {
        return new ArrayList<>(this.getFields());
    }

    public Optional<SerializerInfo> getSerializer() {
        return Optional.ofNullable(this.serializer);
    }

    public static FileForm of(IFileField... fileFields) {
        return new FileForm(Arrays.asList(fileFields), null);
    }

    public static FileForm of(SerializerInfo serializerField, IFileField... fileFields) {
        return new FileForm(Arrays.asList(fileFields), serializerField);
    }
}
