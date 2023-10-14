package xyz.brassgoggledcoders.minescribe.core.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class DataResultCollector<T, C extends Collection<T>> implements Collector<DataResult<T>, List<Either<T, String>>, DataResult<C>> {
    private final Set<Characteristics> CHARACTERISTICS = Set.of(
            Characteristics.UNORDERED
    );

    private final Function<List<T>, C> collectionSupplier;

    public DataResultCollector(Function<List<T>, C> collectionSupplier) {
        this.collectionSupplier = collectionSupplier;
    }

    @Override
    public Supplier<List<Either<T, String>>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<Either<T, String>>, DataResult<T>> accumulator() {
        return (resultList, newResult) -> resultList.add(newResult.get()
                .mapRight(DataResult.PartialResult::message)
        );
    }

    @Override
    public BinaryOperator<List<Either<T, String>>> combiner() {
        return (listA, listB) -> {
            listA.addAll(listB);
            return listA;
        };
    }

    @Override
    public Function<List<Either<T, String>>, DataResult<C>> finisher() {
        return list -> {
            List<T> values = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (Either<T, String> either : list) {
                either.ifLeft(values::add);
                either.ifRight(errors::add);
            }

            if (errors.isEmpty()) {
                return DataResult.success(this.collectionSupplier.apply(values));
            } else {
                return DataResult.error("Errors in Collection: [" + String.join(", ", errors) + "]");
            }
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return this.CHARACTERISTICS;
    }
}
