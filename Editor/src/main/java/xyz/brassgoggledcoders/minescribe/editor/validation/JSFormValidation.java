package xyz.brassgoggledcoders.minescribe.editor.validation;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.codec.JsonCodec;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.FormValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;
import xyz.brassgoggledcoders.minescribe.core.validation.ValidationResult;
import xyz.brassgoggledcoders.minescribe.editor.javascript.IJSObject;
import xyz.brassgoggledcoders.minescribe.editor.javascript.MineScribeJSField;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JSFormValidation extends FormValidation implements IJSObject {
    private final Function<Object[], Object> jsMethod;
    private final MineScribeJSField<?>[] fields;
    private final Codec<FormValidation> codec;
    private final Map<String, Object> values;

    public JSFormValidation(Function<Object[], Object> jsMethod, MineScribeJSField<?>[] fields,
                            Codec<FormValidation> codec, Map<String, Object> values) {
        this.jsMethod = jsMethod;
        this.fields = fields;
        this.codec = codec;
        this.values = values;
    }

    @Override
    public ValidationResult validate(Map<String, Object> value) {
        Object object = this.jsMethod.apply(
                new Object[]{
                        value,
                        values
                }
        );

        if (object instanceof ValidationResult validationResult) {
            return validationResult;
        } else {
            return ValidationResult.error("Found JS Validation Method returning incorrect types");
        }
    }

    @Override
    public Codec<? extends Validation<Map<String, Object>>> getCodec() {
        return this.codec;
    }

    @Override
    public Map<String, Object> getValues() {
        return this.values;
    }

    @Override
    public MineScribeJSField<?>[] getFields() {
        return this.fields;
    }
}
