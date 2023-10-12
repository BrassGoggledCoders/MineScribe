package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

public class CheckBoxFileField extends FileField {
    public static final IFileFieldParser<CheckBoxFileField> PARSER = new BasicFileFieldParser<>(CheckBoxFileField::new);
    public CheckBoxFileField(String name, String field, int sortOrder) {
        super(name, field, sortOrder);
    }

    @Override
    public IFileFieldParser<?> getParser() {
        return PARSER;
    }
}
