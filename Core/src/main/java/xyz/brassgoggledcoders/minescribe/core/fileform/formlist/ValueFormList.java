package xyz.brassgoggledcoders.minescribe.core.fileform.formlist;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;

import java.util.List;
import java.util.function.Supplier;

public class ValueFormList implements IFormList<String> {
    public static final Codec<ValueFormList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceId.CODEC.fieldOf(JsonFieldNames.ID).forGetter(ValueFormList::getId)
    ).apply(instance, ValueFormList::new));

    private final ResourceId id;
    private final Supplier<List<String>> values;

    public ValueFormList(ResourceId id) {
        this.id = id;
        this.values = Suppliers.memoize(() -> Registries.getFormListValues()
                .getValue(this.getId())
                .values()
        );
    }

    public ResourceId getId() {
        return id;
    }

    @Override
    public List<String> getValues() {
        return this.values.get();
    }

    @Override
    public @NotNull Codec<? extends IFormList<?>> getCodec() {
        return CODEC;
    }
}
