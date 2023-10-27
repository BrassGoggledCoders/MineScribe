package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

public abstract class FileField implements IFileField {
    private final String label;
    private final String field;
    private final int sortOrder;

    public FileField(String label, String field, int sortOrder) {
        this.label = label;
        this.field = field;
        this.sortOrder = sortOrder;
    }

    public String getLabel() {
        return label;
    }

    public String getField() {
        return field;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
