package xyz.brassgoggledcoders.minescribe.api.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import xyz.brassgoggledcoders.minescribe.core.codec.JsonCodec;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;
import xyz.brassgoggledcoders.minescribe.core.validation.ValidationResult;

public class FieldValidationData extends FieldValidation {
    public static final JsonCodec<FieldValidationData> CODEC = new JsonCodec<>(
            json -> {
                throw new JsonParseException("Only Meant for Data");
            },
            fieldValidationData -> fieldValidationData.data
    );

    private final JsonObject data;

    public FieldValidationData(ResourceLocation type, JsonObject data) {
        this.data = data;
        this.data.addProperty("type", type.toString());
    }

    @Override
    public ValidationResult validate(Object value) {
        throw new UnsupportedOperationException("This Class is for Data Generation only");
    }

    @Override
    public Codec<? extends Validation<Object>> getCodec() {
        return CODEC;
    }
}
