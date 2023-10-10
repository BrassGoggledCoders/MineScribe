package xyz.brassgoggledcoders.minescribe.api.event;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus.Internal;

public class RegisterMineScribeReloadListenerEvent extends Event {
    private final ReloadableResourceManager resourceManager;

    @Internal
    public RegisterMineScribeReloadListenerEvent(ReloadableResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public void registerReloadListener(PreparableReloadListener reloadListener) {
        resourceManager.registerReloadListener(reloadListener);
    }
}
