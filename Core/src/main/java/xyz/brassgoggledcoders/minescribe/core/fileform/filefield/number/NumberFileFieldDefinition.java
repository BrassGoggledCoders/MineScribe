package xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number;

import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.util.Range;

public abstract class NumberFileFieldDefinition<T extends Number> implements IFileFieldDefinition {
    private final Range<T> range;

    public NumberFileFieldDefinition(Range<T> range) {
        this.range = range;
    }

    public Range<T> getRange() {
        return this.range;
    }

    public abstract T convertNumber(Number number);
}
