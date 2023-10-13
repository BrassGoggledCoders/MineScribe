package xyz.brassgoggledcoders.minescribe;


import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.api.list.IListProvider;
import xyz.brassgoggledcoders.minescribe.list.EmptyListProvider;
import xyz.brassgoggledcoders.minescribe.list.ListListProvider;
import xyz.brassgoggledcoders.minescribe.list.RegistryListProvider;
import xyz.brassgoggledcoders.minescribe.list.TagListProvider;

@Mod(MineScribe.ID)
public class MineScribe {
    public static final String ID = "minescribe";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    public static final ResourceKey<Registry<Codec<? extends IListProvider>>> KEY = ResourceKey.createRegistryKey(rl("list_providers"));

    public MineScribe() {
        IEventBus modBusEvent = FMLJavaModLoadingContext.get()
                        .getModEventBus();

        modBusEvent.addListener(this::newRegistry);
        modBusEvent.addListener(this::registerListProviders);
    }

    public void newRegistry(NewRegistryEvent newRegistryEvent) {
        newRegistryEvent.create(new RegistryBuilder<>()
                .setName(KEY.location())
                .disableSync()
                .disableSaving()
        );
    }

    public void registerListProviders(RegisterEvent registerEvent) {
        registerEvent.register(KEY, helper -> {
            helper.register(EmptyListProvider.ID, EmptyListProvider.CODEC);
            helper.register(TagListProvider.ID, TagListProvider.CODEC);
            helper.register(RegistryListProvider.ID, RegistryListProvider.CODEC);
            helper.register(ListListProvider.ID, ListListProvider.CODEC);
        });
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(ID, path);
    }
}
