package xyz.brassgoggledcoders.minescribe.core.fileform.formlist;

import org.jetbrains.annotations.NotNull;

public record FormListValue(
        String id,
        String label
) implements Comparable<FormListValue> {
    @Override
    public int compareTo(@NotNull FormListValue o) {
        return this.label().compareTo(o.label());
    }
}
