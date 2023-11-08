package xyz.brassgoggledcoders.minescribe.editor.scene.form.control;

import javafx.util.StringConverter;

import java.util.function.Function;

public class LabeledCellConverter<T> extends StringConverter<T> {
    private final Function<T, String> labelMaker;

    public LabeledCellConverter(Function<T, String> labelMaker) {
        this.labelMaker = labelMaker;
    }

    @Override
    public String toString(T object) {
        return object != null ? labelMaker.apply(object) : "";
    }

    @Override
    public T fromString(String string) {
        return null;
    }
}
