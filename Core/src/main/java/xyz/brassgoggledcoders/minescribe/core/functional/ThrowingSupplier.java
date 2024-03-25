package xyz.brassgoggledcoders.minescribe.core.functional;

public interface ThrowingSupplier<T, E extends Exception> {
    T get() throws E;
}
