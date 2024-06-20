package xyz.brassgoggledcoders.minescribe.data;

import com.google.common.base.Suppliers;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.world.level.validation.DirectoryValidator;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.resource.ResourcePackLoader;
import xyz.brassgoggledcoders.minescribe.api.MineScribeAPI;
import xyz.brassgoggledcoders.minescribe.api.event.RegisterMineScribeReloadListenerEvent;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class MineScribeResourceManager {
    private static final Supplier<MineScribeResourceManager> INSTANCE = Suppliers.memoize(MineScribeResourceManager::new);

    private final ReloadableResourceManager resourceManager;
    private PackRepository packRepository;
    private WeakReference<MinecraftServer> serverWeakReference;
    private final MineScribeFileManager fileManager;

    public MineScribeResourceManager() {
        this.resourceManager = new ReloadableResourceManager(MineScribeAPI.PACK_TYPE);
        NeoForge.EVENT_BUS.post(new RegisterMineScribeReloadListenerEvent(this.resourceManager));
        this.fileManager = new MineScribeFileManager(FMLPaths.GAMEDIR.get());
    }

    private void buildPackRepository() {
        MinecraftServer currentServer = Minecraft.getInstance().getSingleplayerServer();
        if (this.serverWeakReference == null || this.serverWeakReference.get() != currentServer) {
            if (currentServer != null) {
                this.packRepository = new PackRepository(
                        new FolderRepositorySource(
                                Minecraft.getInstance().getResourcePackDirectory(),
                                PackType.CLIENT_RESOURCES,
                                PackSource.DEFAULT,
                                new DirectoryValidator(path -> true)
                        ),
                        new ServerPacksSource(new DirectoryValidator(path -> true))
                );

                ResourcePackLoader.populatePackRepository(
                        this.packRepository,
                        MineScribeAPI.PACK_TYPE
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

    public MineScribeFileManager getFileManager() {
        return this.fileManager;
    }

    public static MineScribeResourceManager getInstance() {
        return INSTANCE.get();
    }
}
