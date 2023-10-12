package xyz.brassgoggledcoders.minescribe.core.functional;

public interface ThrowingFunction<T, R, E extends Exception> {
    R apply(T value) throws E;
}
