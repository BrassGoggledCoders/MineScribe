package xyz.brassgoggledcoders.minescribe.editor.file;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public record FileUpdate(
        WatchEvent.Kind<Path> eventKind,
        Path path
) {
}
