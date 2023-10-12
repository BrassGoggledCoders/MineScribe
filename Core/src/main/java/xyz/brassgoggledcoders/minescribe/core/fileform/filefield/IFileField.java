package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

public interface IFileField extends Comparable<IFileField> {
    String getLabel();

    String getField();

    int getSortOrder();

    IFileFieldParser<?> getParser();
}
