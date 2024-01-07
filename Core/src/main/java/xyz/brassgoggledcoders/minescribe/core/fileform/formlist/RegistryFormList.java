package xyz.brassgoggledcoders.minescribe.core.fileform.formlist;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.codec.RegistryCodec;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.packinfo.IFullName;
import xyz.brassgoggledcoders.minescribe.core.registry.ILabeledValue;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

import java.util.List;
import java.util.Optional;

public record RegistryFormList<V>(
        Registry<?, V> registry,
        boolean fullNameId
) implements IFormList<V> {
    public static final Codec<RegistryFormList<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            new RegistryCodec().fieldOf(JsonFieldNames.REGISTRY).forGetter(RegistryFormList::registry),
            Codec.BOOL.optionalFieldOf(JsonFieldNames.FULL_NAME_ID, false).forGetter(RegistryFormList::fullNameId)
    ).apply(instance, RegistryFormList::new));

    public RegistryFormList(Registry<?, V> registry) {
        this(registry, false);
    }

    @Override
    public @NotNull String getKey(V value) {
        if (this.fullNameId() && value instanceof IFullName fullName) {
            return fullName.getFullName()
                    .toString();
        } else {
            String alias = registry.getAlias(value);
            if (alias != null) {
                return alias;
            } else {
                return registry.getKey(value)
                        .toString();
            }
        }
    }

    @Override
    public Optional<String> getAlias(V value) {
        if (this.registry.getAlias(value) != null) {
            return Optional.ofNullable(this.registry()
                    .getKey(value)
                    .toString()
            );
        } else {
            return Optional.empty();
        }
    }

    @Override
    public @NotNull FancyText getLabel(V value) {
        String label = "";
        if (value instanceof ILabeledValue labeledValue) {
            label += labeledValue.getLabel();
        }

        if (this.fullNameId() && value instanceof IFullName fullName) {
            if (!label.isEmpty()) {
                label += " (";
            }
            label += fullName.getFullName()
                    .toString();

            if (label.contains(" (") && !label.contains(")")) {
                label += ")";
            }
        }

        if (label.isEmpty()) {
            label = value.toString();
        }

        return FancyText.literal(label);
    }

    @Override
    public List<V> getValues() {
        return registry.getValues();
    }

    @Override
    @NotNull
    public Codec<? extends IFormList<?>> getCodec() {
        return CODEC;
    }
}
