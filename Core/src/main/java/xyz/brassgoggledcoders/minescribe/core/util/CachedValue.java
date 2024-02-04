package xyz.brassgoggledcoders.minescribe.core.util;

import xyz.brassgoggledcoders.minescribe.core.functional.ThrowingSupplier;

import java.util.concurrent.atomic.AtomicReference;

public class CachedValue<T, E extends Exception> implements ThrowingSupplier<T, E> {
    private final ThrowingSupplier<T, E> supplier;

    private final AtomicReference<T> value;

    public CachedValue(ThrowingSupplier<T, E> supplier) {
        this.supplier = supplier;
        this.value = new AtomicReference<>();
    }

    @Override
    public T get() throws E {
        T theValue = this.value.get();
        if (theValue == null) {
            theValue = this.supplier.get();
            this.value.set(theValue);
        }
        return theValue;
    }
}
