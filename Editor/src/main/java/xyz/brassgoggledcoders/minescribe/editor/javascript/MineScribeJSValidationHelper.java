package xyz.brassgoggledcoders.minescribe.editor.javascript;

import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.ValidationResult;

import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unused")
public class MineScribeJSValidationHelper {
    public Codec<FieldValidation> createForField(Function<Object[], Object> function, MineScribeJSField<?>[] fields) {
        return null;
    }

    public ValidationResult createValidResult() {
        return ValidationResult.valid();
    }

    public ValidationResult createErrorResult(String message) {
        return ValidationResult.error(message);
    }
}
