package xyz.brassgoggledcoders.minescribe.data;

import com.google.common.base.Suppliers;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.*;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathPackResources;
import net.minecraftforge.resource.ResourcePackLoader;
import xyz.brassgoggledcoders.minescribe.api.MineScribeAPI;
import xyz.brassgoggledcoders.minescribe.api.event.RegisterMineScribeReloadListenerEvent;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MineScribeResourceManager {
    private static final Supplier<MineScribeResourceManager> INSTANCE = Suppliers.memoize(MineScribeResourceManager::new);

    private final ReloadableResourceManager resourceManager;
    private PackRepository packRepository;
    private WeakReference<MinecraftServer> serverWeakReference;

    public MineScribeResourceManager() {
        this.resourceManager = new ReloadableResourceManager(MineScribeAPI.PACK_TYPE);
        MinecraftForge.EVENT_BUS.post(new RegisterMineScribeReloadListenerEvent(this.resourceManager));
    }

    private void buildPackRepository() {
        MinecraftServer currentServer = Minecraft.getInstance().getSingleplayerServer();
        if (this.serverWeakReference == null || this.serverWeakReference.get() != currentServer) {
            if (currentServer != null) {
                this.packRepository = new PackRepository(
                        MineScribeAPI.PACK_TYPE,
                        new FolderRepositorySource(
                                Minecraft.getInstance().getResourcePackDirectory(),
                                PackSource.DEFAULT
                        ),
                        new FolderRepositorySource(
                                currentServer.getFile(LevelResource.DATAPACK_DIR.getId()),
                                PackSource.WORLD
                        )
                );
                ResourcePackLoader.loadResourcePacks(
                        this.packRepository,
                        MineScribeResourceManager::buildPackFinder
                );
                this.serverWeakReference = new WeakReference<>(currentServer);
            } else {
                this.packRepository = null;
            }
        }
    }

    public ReloadInstance reloadResources() {
        buildPackRepository();
        if (this.packRepository != null) {
            packRepository.reload();
            packRepository.setSelected(packRepository.getAvailableIds());
            return this.resourceManager.createReload(
                    Util.backgroundExecutor(),
                    Minecraft.getInstance(),
                    CompletableFuture.completedFuture(Unit.INSTANCE),
                    packRepository.openAllSelected()
            );
        } else {
            return null;
        }
    }

    private static RepositorySource buildPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks) {
        return (packList, factory) -> serverPackFinder(modResourcePacks, packList, factory);
    }

    private static void serverPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks, Consumer<Pack> consumer, Pack.PackConstructor factory) {
        for (Map.Entry<IModFile, ? extends PathPackResources> e : modResourcePacks.entrySet()) {
            IModInfo mod = e.getKey().getModInfos().get(0);
            if (Objects.equals(mod.getModId(), "minecraft")) continue; // skip the minecraft "mod"
            final String name = "mod:" + mod.getModId();
            final Pack packInfo = Pack.create(name, false, e::getValue, factory, Pack.Position.BOTTOM, PackSource.DEFAULT);
            if (packInfo == null) {
                continue;
            }
            consumer.accept(packInfo);
        }
    }

    public static MineScribeResourceManager getInstance() {
        return INSTANCE.get();
    }
}
