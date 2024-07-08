package xyz.brassgoggledcoders.minescribe.util;

import java.util.function.Consumer;

public class SetupHelper {
    public static <T> T setup(T start, Consumer<T> setup) {
        setup.accept(start);
        return start;
    }
}
