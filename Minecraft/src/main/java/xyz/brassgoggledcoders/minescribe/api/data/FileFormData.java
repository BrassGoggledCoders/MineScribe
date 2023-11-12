package xyz.brassgoggledcoders.minescribe.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.codec.ValidationCodec;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.fileform.SerializerInfo;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;

import java.util.*;

public class FileFormData {
    public static final Codec<FileFormData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FileFieldData.LIST_CODEC.fieldOf(JsonFieldNames.FIELDS).forGetter(FileFormData::getFieldsForCodec),
            ValidationCodec.LIST_CODEC.optionalFieldOf(JsonFieldNames.VALIDATIONS, Collections.emptyList())
                    .forGetter(FileFormData::getValidationsForCodec),
            SerializerInfo.CODEC.optionalFieldOf(JsonFieldNames.SERIALIZER).forGetter(FileFormData::getSerializer)
    ).apply(instance, (fields, validations, serializer) -> new FileFormData(fields, validations, serializer.orElse(null))));

    private final SerializerInfo serializer;
    private final TreeSet<FileFieldData<?>> fields;
    private final Collection<Validation<?>> validations;

    public FileFormData(Collection<FileFieldData<?>> fields, Collection<Validation<?>> validations, SerializerInfo serializer) {
        this.serializer = serializer;
        this.fields = new TreeSet<>(Comparable::compareTo);
        this.fields.addAll(fields);
        this.validations = validations;
    }

    public Collection<FileFieldData<?>> getFields() {
        return fields;
    }

    public Collection<Validation<?>> getValidations() {
        return this.validations;
    }

    public List<FileFieldData<?>> getFieldsForCodec() {
        return new ArrayList<>(this.getFields());
    }

    public List<Validation<?>> getValidationsForCodec() {
        return new ArrayList<>(this.getValidations());
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
                new ArrayList<>(this.validations),
                this.serializer
        );
    }

    public static FileFormData of(FileFieldData<?>... fileFields) {
        return new FileFormData(Arrays.asList(fileFields), Collections.emptyList(), null);
    }

    public static FileFormData of(List<FileFieldData<?>> fileFields, List<Validation<?>> validations) {
        return new FileFormData(fileFields, validations, null);
    }

    public static FileFormData of(SerializerInfo serializerField, FileFieldData<?>... fileFields) {
        return new FileFormData(Arrays.asList(fileFields), Collections.emptyList(), serializerField);
    }
}
