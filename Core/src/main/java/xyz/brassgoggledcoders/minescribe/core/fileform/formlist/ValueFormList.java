package xyz.brassgoggledcoders.minescribe.core.fileform.formlist;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.FormList;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.util.CachedValue;

import java.util.List;
import java.util.MissingResourceException;
import java.util.function.Supplier;

public class ValueFormList implements IFormList<String> {
    public static final Codec<ValueFormList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceId.CODEC.fieldOf(JsonFieldNames.ID).forGetter(ValueFormList::getId)
    ).apply(instance, ValueFormList::new));

    private final ResourceId id;
    private final CachedValue<List<String>, Exception> values;

    public ValueFormList(ResourceId id) {
        this.id = id;
        this.values = new CachedValue<>(() -> {
            FormList formList = Registries.getFormListValues()
                    .getValue(this.getId());

            if (formList != null) {
                return formList.values();
            } else {
                throw new Exception("Failed to get Values for %s".formatted(this.getId()));
            }
        });
    }

    public ResourceId getId() {
        return id;
    }

    @Override
    public List<String> getValues() throws Exception {
        return this.values.get();
    }

    @Override
    public @NotNull Codec<? extends IFormList<?>> getCodec() {
        return CODEC;
    }
}
