package xyz.brassgoggledcoders.minescribe.editor.validation;

import com.dlsc.formsfx.model.validators.CustomValidator;

import java.util.regex.Pattern;

public class StringRegexValidator extends CustomValidator<String> {

    public StringRegexValidator(Pattern regex, String errorMessage) {
        super(value -> regex.matcher(value).matches(), errorMessage);
    }

    public static StringRegexValidator forRegex(String regex, String errorMessage) {
        return new StringRegexValidator(Pattern.compile(regex), errorMessage);
    }
}
