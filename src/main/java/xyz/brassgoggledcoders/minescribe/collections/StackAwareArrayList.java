package xyz.brassgoggledcoders.minescribe.collections;

import xyz.brassgoggledcoders.minescribe.MineScribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class StackAwareArrayList<T> extends ArrayList<T> {
    private final Consumer<String> output;

    public StackAwareArrayList(Consumer<String> output) {
        this.output = output;
    }

    @Override
    public boolean add(T t) {
        print();

        return super.add(t);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        print();

        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        print();

        return super.addAll(index, c);
    }

    @Override
    public void add(int index, T element) {
        print();

        super.add(index, element);
    }

    private void print() {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int maxSize = Math.min(stackTrace.length, MineScribe.SERVER_CONFIG.maxStack.get());
        for (int i = 0; i < maxSize; i++) {
            output.accept(stackTrace[i].toString());
        }
    }
}
