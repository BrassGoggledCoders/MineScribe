package xyz.brassgoggledcoders.minescribe.api.json;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.Util;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.InactiveProfiler;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleGathererInstance<GATHER extends IPreparedGatherer<PREP, OUTPUT>, PREP, OUTPUT> {
    protected final CompletableFuture<Unit> allPreparations = new CompletableFuture<>();
    protected final CompletableFuture<List<OUTPUT>> allDone;
    final Set<IPreparedGatherer<?, OUTPUT>> preparingListeners;
    private final int listenerCount;
    private int startedReloads;
    private int finishedReloads;
    private final AtomicInteger startedTaskCounter = new AtomicInteger();
    private final AtomicInteger doneTaskCounter = new AtomicInteger();

    public static <T extends IPreparedGatherer<U, V>, U, V> SimpleGathererInstance<T, U, V> of(
            ResourceManager pResourceManager,
            Collection<T> pListeners,
            Executor pBackgroundExecutor,
            Executor pGameExecutor) {
        return new SimpleGathererInstance<>(
                pBackgroundExecutor,
                pGameExecutor,
                pResourceManager,
                pListeners,
                (barrier, resourceManager, preparedGatherer, backgroundExecutor, gameExecutor) ->
                        preparedGatherer.gather(
                                barrier,
                                resourceManager,
                                InactiveProfiler.INSTANCE,
                                InactiveProfiler.INSTANCE,
                                pBackgroundExecutor,
                                gameExecutor
                        ),
                CompletableFuture.completedFuture(Unit.INSTANCE)
        );
    }

    protected SimpleGathererInstance(
            final Executor backgroundExecutor,
            final Executor gameExecutor,
            ResourceManager resourceManager,
            Collection<GATHER> preparedGatherers,
            StateFactory<OUTPUT> stateFactory,
            CompletableFuture<Unit> alsoAwait
    ) {
        this.listenerCount = preparedGatherers.size();
        this.startedTaskCounter.incrementAndGet();
        alsoAwait.thenRun(this.doneTaskCounter::incrementAndGet);
        List<CompletableFuture<OUTPUT>> list = Lists.newArrayList();
        CompletableFuture<?> currentlyProcessingFuture = alsoAwait;
        this.preparingListeners = Sets.newHashSet(preparedGatherers);

        for (final IPreparedGatherer<?, OUTPUT> preparedGatherer : preparedGatherers) {
            final CompletableFuture<?> finalCompletableFuture = currentlyProcessingFuture;
            CompletableFuture<OUTPUT> stateFactoryResult = stateFactory.create(
                    new PreparableReloadListener.PreparationBarrier() {
                        @NotNull
                        public <T> CompletableFuture<T> wait(@NotNull T backgroundResult) {
                            gameExecutor.execute(() -> {
                                SimpleGathererInstance.this.preparingListeners.remove(preparedGatherer);
                                if (SimpleGathererInstance.this.preparingListeners.isEmpty()) {
                                    SimpleGathererInstance.this.allPreparations.complete(Unit.INSTANCE);
                                }

                            });
                            return SimpleGathererInstance.this.allPreparations.thenCombine(
                                    finalCompletableFuture,
                                    (a, b) -> backgroundResult
                            );
                        }
                    },
                    resourceManager,
                    preparedGatherer,
                    (backGroundRunnable) -> {
                        this.startedTaskCounter.incrementAndGet();
                        backgroundExecutor.execute(() -> {
                            backGroundRunnable.run();
                            this.doneTaskCounter.incrementAndGet();
                        });
                    },
                    (gameRunnable) -> {
                        ++this.startedReloads;
                        gameExecutor.execute(() -> {
                            gameRunnable.run();
                            ++this.finishedReloads;
                        });
                    }
            );
            list.add(stateFactoryResult);
            currentlyProcessingFuture = stateFactoryResult;
        }

        this.allDone = Util.sequenceFailFast(list);
    }

    public CompletableFuture<List<OUTPUT>> done() {
        return this.allDone;
    }

    public float getActualProgress() {
        int i = this.listenerCount - this.preparingListeners.size();
        float f = (float) (this.doneTaskCounter.get() * 2 + this.finishedReloads * 2 + i);
        float f1 = (float) (this.startedTaskCounter.get() * 2 + this.startedReloads * 2 + this.listenerCount);
        return f / f1;
    }

    protected interface StateFactory<S> {
        CompletableFuture<S> create(
                PreparableReloadListener.PreparationBarrier preparationBarrier,
                ResourceManager resourceManager,
                IPreparedGatherer<?, S> preparedGatherer,
                Executor backgroundExecutor,
                Executor gameExecutor
        );
    }
}