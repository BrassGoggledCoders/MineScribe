package xyz.brassgoggledcoders.minescribe.editor.javascript;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.codec.JsonCodec;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.FormValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;
import xyz.brassgoggledcoders.minescribe.core.validation.ValidationResult;
import xyz.brassgoggledcoders.minescribe.editor.validation.JSFieldValidation;
import xyz.brassgoggledcoders.minescribe.editor.validation.JSFormValidation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("unused")
public class MineScribeJSValidationHelper {
    public Codec<FieldValidation> createForField(Function<Object[], Object> function, MineScribeJSField<?>[] fields) {
        return createCodec(JSFieldValidation::new, function, fields);
    }

    public Codec<FormValidation> createForForm(Function<Object[], Object> function, MineScribeJSField<?>[] fields) {
        return createCodec(JSFormValidation::new, function, fields);
    }

    public ValidationResult createValidResult() {
        return ValidationResult.valid();
    }

    public ValidationResult createErrorResult(String message) {
        return ValidationResult.error(message);
    }

    private static <T extends Validation<U>, U> JsonCodec<T> createCodec(
            Function4<Function<Object[], Object>, MineScribeJSField<?>[], Codec<T>, Map<String, Object>, T> constructor,
            Function<Object[], Object> jsMethod,
            MineScribeJSField<?>[] fields
    ) {
        return new JsonCodec<>(
                createJSValidation(constructor, jsMethod, fields),
                jsFieldValidation -> JsonNull.INSTANCE
        );
    }

    private static <T extends Validation<U>, U> BiFunction<JsonElement, Codec<T>, T> createJSValidation(
            Function4<Function<Object[], Object>, MineScribeJSField<?>[], Codec<T>, Map<String, Object>, T> constructor,
            Function<Object[], Object> jsMethod,
            MineScribeJSField<?>[] fields
    ) {
        return (jsonElement, codec) -> {
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Map<String, Object> values = new HashMap<>();
                for (MineScribeJSField<?> field : fields) {
                    Object value = field.loadValue(jsonObject);
                    if (value == null) {
                        throw new JsonParseException(field.getFieldName() + " does not have a value.");
                    } else {
                        values.put(field.getFieldName(), value);
                    }
                }
                return constructor.apply(
                        jsMethod,
                        fields,
                        codec,
                        values
                );
            } else {
                throw new JsonParseException("Validations must be JsonObjects");
            }
        };
    }
}
