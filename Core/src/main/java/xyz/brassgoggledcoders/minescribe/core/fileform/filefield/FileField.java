package xyz.brassgoggledcoders.minescribe.core.fileform.filefield;

import org.jetbrains.annotations.NotNull;

public abstract class FileField implements IFileField {
    private final String label;
    private final String field;
    private final int sortOrder;

    public FileField(String name, String field, int sortOrder) {
        this.label = name;
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

    @Override
    public int compareTo(@NotNull IFileField o) {
        int comparedSortOrder = Integer.compare(this.sortOrder, o.getSortOrder());
        if (comparedSortOrder == 0) {
            return String.CASE_INSENSITIVE_ORDER.compare(this.label, o.getLabel());
        } else {
            return 0;
        }
    }
}
