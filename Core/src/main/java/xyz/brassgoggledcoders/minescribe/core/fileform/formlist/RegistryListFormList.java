package xyz.brassgoggledcoders.minescribe.core.fileform.formlist;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.registry.Registry;
import xyz.brassgoggledcoders.minescribe.core.service.IRegistryProviderService;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;

import java.util.ArrayList;
import java.util.List;

public class RegistryListFormList implements IFormList<Registry<?, ?>>  {
    public static Codec<RegistryListFormList> CODEC = Codec.unit(RegistryListFormList::new);

    @Override
    public List<Registry<?, ?>> getValues() throws Exception {
        List<Registry<?, ?>> values = new ArrayList<>();
        for (IRegistryProviderService service : Registries.REGISTRY_PROVIDER_SERVICE_LOADER) {
            values.addAll(service.getRegistries());
        }
        return values;
    }

    @Override
    public @NotNull FancyText getLabel(Registry<?, ?> value) {
        return value.getName();
    }

    @Override
    public @NotNull String getKey(Registry<?, ?> value) {
        return value.getId();
    }

    @Override
    public @NotNull Codec<? extends IFormList<?>> getCodec() {
        return CODEC;
    }
}
