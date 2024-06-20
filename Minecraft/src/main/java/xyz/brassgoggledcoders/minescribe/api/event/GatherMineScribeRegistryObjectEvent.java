package xyz.brassgoggledcoders.minescribe.api.event;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GatherMineScribeRegistryObjectEvent<T, U> extends Event {
    private final List<T> values;
    private final Function<T, ResourceLocation> getId;
    private final Function<T, U> converter;

    public GatherMineScribeRegistryObjectEvent(Function<T, ResourceLocation> getId, Function<T, U> converter) {
        this.values = new ArrayList<>();
        this.getId = getId;
        this.converter = converter;
    }

    public void register(T value) {
        this.values.add(value);
    }

    public Map<ResourceLocation, U> getValues() {
        return values.stream()
                .collect(Collectors.toMap(
                        getId,
                        converter
                ));
    }
}
