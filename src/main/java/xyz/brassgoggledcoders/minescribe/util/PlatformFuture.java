package xyz.brassgoggledcoders.minescribe.util;

import javafx.application.Platform;

import java.util.concurrent.CompletableFuture;

public class PlatformFuture {
    public static CompletableFuture<Void> getFuture(Runnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            runnable.run();
            future.complete(null);
        });
        return future;
    }
}
