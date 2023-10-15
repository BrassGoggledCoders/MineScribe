package xyz.brassgoggledcoders.minescribe.data;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public abstract class MineScribeReloadListener<T, U> implements PreparableReloadListener {
    @Override
    @ParametersAreNonnullByDefault
    public final @NotNull CompletableFuture<Void> reload(
            PreparableReloadListener.PreparationBarrier pStage,
            ResourceManager pResourceManager,
            ProfilerFiller pPreparationsProfiler,
            ProfilerFiller pReloadProfiler,
            Executor pBackgroundExecutor,
            Executor pGameExecutor
    ) {
        return CompletableFuture.supplyAsync(() -> this.prepare(pResourceManager, pPreparationsProfiler), pBackgroundExecutor)
                .thenCompose(pStage::wait)
                .thenApplyAsync(result -> this.apply(result, pResourceManager, pReloadProfiler), pGameExecutor)
                .thenAcceptAsync(result -> this.finalize(result, MineScribeResourceManager.getInstance().getFileManager(), pReloadProfiler));
    }

    /**
     * Performs any reloading that can be done off-thread, such as file IO
     */
    protected abstract T prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler);

    protected abstract U apply(T pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler);

    protected abstract void finalize(U pObject, MineScribeFileManager fileManager, ProfilerFiller profilerFiller);
}
