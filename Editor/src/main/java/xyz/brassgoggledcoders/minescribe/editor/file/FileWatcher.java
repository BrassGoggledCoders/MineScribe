package xyz.brassgoggledcoders.minescribe.editor.file;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class FileWatcher extends Thread implements AutoCloseable {
    private final AtomicBoolean shutdown;
    private final WatchService watchService;
    private final Consumer<FileUpdate> handleUpdates;
    private final Consumer<Throwable> exceptionHandler;
    private final Map<WatchKey, Path> watchKeys;

    public FileWatcher(WatchService watchService, Consumer<FileUpdate> handleUpdates, Consumer<Throwable> exceptionHandler) {
        this.shutdown = new AtomicBoolean(false);
        this.watchService = watchService;
        this.handleUpdates = handleUpdates;
        this.exceptionHandler = exceptionHandler;
        this.watchKeys = new HashMap<>();
    }

    public void watchDirectory(Path path) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    register(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            this.exceptionHandler.accept(e);
        }
    }

    private void register(Path path) {
        try {
            this.watchKeys.put(
                    path.register(
                            this.watchService,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_MODIFY,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.OVERFLOW
                    ),
                    path
            );
        } catch (IOException e) {
            this.exceptionHandler.accept(e);
        }
    }


    public void run() {
        while (!this.shutdown.get()) {
            WatchKey key;
            try {
                key = this.watchService.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = watchKeys.get(key);
            if (dir == null) {
                this.exceptionHandler.accept(new IllegalStateException("WatchKey not recognized!!"));
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind != StandardWatchEventKinds.OVERFLOW) {
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = ((WatchEvent<Path>) event);
                    Path name = ev.context();
                    Path child = dir.resolve(name);

                    this.handleUpdates.accept(new FileUpdate(
                            ev.kind(),
                            child
                    ));
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                            watchDirectory(child);
                        }
                    }
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                watchKeys.remove(key);

                if (watchKeys.isEmpty()) {
                    break;
                }
            }
        }
    }

    public void shutdown() {
        this.shutdown.set(true);
    }

    @Override
    public void close() throws Exception {
        this.shutdown();
        this.join();
        this.watchService.close();
    }

    public static FileWatcher of(Consumer<FileUpdate> handleUpdates, Consumer<Throwable> exceptionHandler) throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();

        FileWatcher watcher = new FileWatcher(watchService, handleUpdates, exceptionHandler);
        watcher.start();
        return watcher;
    }
}
