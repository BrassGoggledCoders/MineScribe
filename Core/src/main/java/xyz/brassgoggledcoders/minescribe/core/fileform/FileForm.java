package xyz.brassgoggledcoders.minescribe.core.fileform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.brassgoggledcoders.minescribe.core.codec.ValidationCodec;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;

import java.util.*;

public class FileForm {
    public static final Codec<FileForm> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FileField.LIST_CODEC.fieldOf(JsonFieldNames.FIELDS).forGetter(FileForm::getFieldsForCodec),
            ValidationCodec.LIST_CODEC.optionalFieldOf(JsonFieldNames.VALIDATIONS, Collections.emptyList())
                    .forGetter(FileForm::getValidationsForCodec),
            SerializerInfo.CODEC.optionalFieldOf(JsonFieldNames.SERIALIZER).forGetter(FileForm::getSerializer)
    ).apply(instance, (fields, validations, serializer) -> new FileForm(fields, validations, serializer.orElse(null))));

    private final SerializerInfo serializer;
    private final TreeSet<FileField<?>> fields;
    private final Collection<Validation<?>> validations;

    public FileForm(Collection<FileField<?>> fields, Collection<Validation<?>> validations, SerializerInfo serializer) {
        this.serializer = serializer;
        this.validations = validations;
        this.fields = new TreeSet<>(Comparable::compareTo);
        this.fields.addAll(fields);
    }

    public Collection<FileField<?>> getFields() {
        return fields;
    }

    public Collection<Validation<?>> getValidations() {
        return this.validations;
    }

    public List<FileField<?>> getFieldsForCodec() {
        return new ArrayList<>(this.getFields());
    }

    public List<Validation<?>> getValidationsForCodec() {
        return new ArrayList<>(this.getValidations());
    }

    public Optional<SerializerInfo> getSerializer() {
        return Optional.ofNullable(this.serializer);
    }

    public static FileForm of(FileField<?>... fileFields) {
        return new FileForm(Arrays.asList(fileFields), Collections.emptyList(), null);
    }

    public static FileForm of(List<FileField<?>> fileFields, List<Validation<?>> validations) {
        return new FileForm(fileFields, validations, null);
    }

    public static FileForm of(SerializerInfo serializerField, FileField<?>... fileFields) {
        return new FileForm(Arrays.asList(fileFields), Collections.emptyList(), serializerField);
    }
}
