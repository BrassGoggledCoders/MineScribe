package xyz.brassgoggledcoders.minescribe.editor.controller.tab;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public interface IFileEditorController {

    @Nullable
    Path getPath();
}
