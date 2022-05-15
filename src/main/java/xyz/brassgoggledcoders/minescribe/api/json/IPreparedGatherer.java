package xyz.brassgoggledcoders.minescribe.api.json;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface IPreparedGatherer<PREP, OUTPUT> {
    default CompletableFuture<OUTPUT> gather(
            PreparableReloadListener.PreparationBarrier pPreparationBarrier,
            ResourceManager pResourceManager,
            ProfilerFiller pPreparationsProfiler,
            ProfilerFiller pReloadProfiler,
            Executor pBackgroundExecutor,
            Executor pGameExecutor
    ) {
        return CompletableFuture.supplyAsync(
                        () -> this.prepare(pResourceManager, pPreparationsProfiler),
                        pBackgroundExecutor
                )
                .thenCompose(pPreparationBarrier::wait)
                .thenApplyAsync(
                        (prep) -> this.apply(prep, pResourceManager, pReloadProfiler),
                        pGameExecutor
                );
    }

    PREP prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler);

    OUTPUT apply(PREP pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler);
}
