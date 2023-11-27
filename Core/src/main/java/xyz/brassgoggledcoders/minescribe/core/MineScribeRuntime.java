package xyz.brassgoggledcoders.minescribe.core;

import java.util.Objects;

public enum MineScribeRuntime {
    MINECRAFT,
    APPLICATION;

    private static MineScribeRuntime RUNTIME;

    public static void setRuntime(MineScribeRuntime mineScribeRuntime) {
        RUNTIME = mineScribeRuntime;
    }

    public static MineScribeRuntime getRuntime() {
        return Objects.requireNonNull(RUNTIME, "Runtime type not set");
    }
}
