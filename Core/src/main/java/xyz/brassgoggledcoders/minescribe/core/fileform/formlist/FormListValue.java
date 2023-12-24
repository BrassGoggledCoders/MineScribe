package xyz.brassgoggledcoders.minescribe.core.fileform.formlist;

import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

public record FormListValue(
        String id,
        FancyText label
) implements Comparable<FormListValue> {
    @Override
    public int compareTo(@NotNull FormListValue o) {
        return this.label().compareTo(o.label());
    }
}
