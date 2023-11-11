package xyz.brassgoggledcoders.minescribe.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.fileform.SerializerInfo;

import java.util.*;

public class FileFormData {
    public static final Codec<FileFormData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FileFieldData.LIST_CODEC.fieldOf(JsonFieldNames.FIELDS).forGetter(FileFormData::getFieldsForCodec),
            SerializerInfo.CODEC.optionalFieldOf(JsonFieldNames.SERIALIZER).forGetter(FileFormData::getSerializer)
    ).apply(instance, (fields, serializer) -> new FileFormData(fields, serializer.orElse(null))));

    private final SerializerInfo serializer;
    private final TreeSet<FileFieldData<?>> fields;

    public FileFormData(Collection<FileFieldData<?>> fields, SerializerInfo serializer) {
        this.serializer = serializer;
        this.fields = new TreeSet<>(Comparable::compareTo);
        this.fields.addAll(fields);
    }

    public Collection<FileFieldData<?>> getFields() {
        return fields;
    }

    public List<FileFieldData<?>> getFieldsForCodec() {
        return new ArrayList<>(this.getFields());
    }

    public Optional<SerializerInfo> getSerializer() {
        return Optional.ofNullable(this.serializer);
    }

    public FileForm toFileForm() {
        return new FileForm(
                new ArrayList<>(this.getFields()
                        .stream()
                        .map(FileFieldData::toFileField)
                        .toList()
                ),
                this.serializer
        );
    }

    public static FileFormData of(FileFieldData<?>... fileFields) {
        return new FileFormData(Arrays.asList(fileFields), null);
    }

    public static FileFormData of(SerializerInfo serializerField, FileFieldData<?>... fileFields) {
        return new FileFormData(Arrays.asList(fileFields), serializerField);
    }
}
