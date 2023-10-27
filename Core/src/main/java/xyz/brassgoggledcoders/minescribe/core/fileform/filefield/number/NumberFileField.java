package xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number;

import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.util.Range;

public abstract class NumberFileField<T extends Number> extends FileField {
    private final Range<T> range;

    public NumberFileField(String label, String field, int sortOrder, Range<T> range) {
        super(label, field, sortOrder);
        this.range = range;
    }

    public Range<T> getRange() {
        return this.range;
    }

    public abstract T convertNumber(Number number);
}
