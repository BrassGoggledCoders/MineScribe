package xyz.brassgoggledcoders.minescribe.data;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;

import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class GameGatheredMineScribeReloadListener<T> extends MineScribeReloadListener<Unit, Stream<T>> {
    private final String name;
    private final Codec<T> encoder;
    private final Function<T, Path> getPathForFile;
    private final Supplier<Stream<T>> supplyStream;

    public GameGatheredMineScribeReloadListener(String name, Codec<T> encoder, Function<T, Path> getPathForFile, Supplier<Stream<T>> supplyStream) {
        this.name = name;
        this.encoder = encoder;
        this.getPathForFile = getPathForFile;
        this.supplyStream = supplyStream;
    }

    @Override
    protected Unit prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return Unit.INSTANCE;
    }

    @Override
    protected Stream<T> apply(Unit pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return this.supplyStream.get();
    }

    @Override
    protected void finalize(Stream<T> pObject, MineScribeFileManager fileManager, ProfilerFiller profilerFiller) {
        pObject.map(value -> Pair.of(this.getPathForFile.apply(value), value))
                .forEach(value -> fileManager.writeFile(Path.of(name).resolve(value.first()), this.encoder, value.value()));
    }
}
