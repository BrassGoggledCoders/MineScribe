package xyz.brassgoggledcoders.minescribe.core.fileform.formlist;

import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

import java.util.Optional;

public record FormListValue(
        String id,
        FancyText label,
        Optional<String> alias
) implements Comparable<FormListValue> {
    @Override
    public int compareTo(@NotNull FormListValue o) {
        return this.label().compareTo(o.label());
    }

    public boolean matches(String id) {
        return this.id().equals(id) || this.alias().map(id::equals).orElse(false);
    }
}
