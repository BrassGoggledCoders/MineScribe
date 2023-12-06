package xyz.brassgoggledcoders.minescribe.editor.validation;

import com.mojang.serialization.Codec;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;
import xyz.brassgoggledcoders.minescribe.core.validation.ValidationResult;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class RegexFieldValidation extends FieldValidation {
    private final String regexString;
    private final Predicate<String> regex;
    private final String errorMessage;

    public RegexFieldValidation(String regex, String errorMessage) {
        this.regexString = regex;
        this.regex = Pattern.compile(regex)
                .asMatchPredicate();
        this.errorMessage = errorMessage;
    }

    @Override
    public ValidationResult validate(Object value) {
        if (value != null) {
            String string = value.toString();
            if (regex.test(string)) {
                return ValidationResult.valid();
            } else {
                if (errorMessage == null) {
                    return ValidationResult.error("%s did not match %s".formatted(string, regexString));
                } else {
                    return ValidationResult.error(this.errorMessage.formatted(string));
                }
            }
        } else {
            return ValidationResult.valid();
        }
    }

    @Override
    public Codec<? extends Validation<?>> getCodec() {
        return null;
    }
}
