package xyz.brassgoggledcoders.minescribe.api.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import xyz.brassgoggledcoders.minescribe.core.codec.JsonCodec;
import xyz.brassgoggledcoders.minescribe.core.util.IDataObject;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;
import xyz.brassgoggledcoders.minescribe.core.validation.ValidationResult;

import java.util.List;

public class ValidationData<T> extends Validation<T> implements IDataObject {
    public static final JsonCodec<ValidationData<?>> CODEC = new JsonCodec<>(
            json -> {
                throw new JsonParseException("Only Meant for Data");
            },
            validationData -> validationData.data
    );

    public static final Codec<List<ValidationData<?>>> LIST_CODEC = CODEC.listOf();

    private final JsonObject data;

    public ValidationData(ResourceLocation type, JsonObject data) {
        this.data = data;
        this.data.addProperty("type", type.toString());
    }

    @Override
    public ValidationResult validate(Object value) {
        throw new UnsupportedOperationException("This Class is for Data Generation only");
    }

    @Override
    public Codec<? extends Validation<?>> getCodec() {
        return CODEC;
    }

    @Override
    public JsonElement getData() {
        return this.data;
    }

    public static ValidationData<?> of(ResourceLocation type, JsonObject data) {
        return new ValidationData<>(type, data);
    }
}
