package xyz.brassgoggledcoders.minescribe.editor.scene.form.control;

import javafx.util.StringConverter;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

import java.util.function.Function;

public class LabeledCellConverter<T> extends StringConverter<T> {
    private final Function<T, FancyText> labelMaker;

    public LabeledCellConverter(Function<T, FancyText> labelMaker) {
        this.labelMaker = labelMaker;
    }

    @Override
    public String toString(T object) {
        return object != null ? labelMaker.apply(object).getText() : "";
    }

    @Override
    public T fromString(String string) {
        return null;
    }
}
