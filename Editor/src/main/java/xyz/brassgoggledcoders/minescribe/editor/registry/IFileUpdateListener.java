package xyz.brassgoggledcoders.minescribe.editor.registry;

import xyz.brassgoggledcoders.minescribe.editor.file.FileUpdate;

public interface IFileUpdateListener {
    void fileUpdated(FileUpdate fileUpdate);
}
