package xyz.brassgoggledcoders.minescribe.editor.service.fxml;

import org.jetbrains.annotations.Nullable;

import java.net.URL;

public interface IFXMLService {

    <NODE, CONTROLLER> LoadResult<NODE, CONTROLLER> load(URL url);

    record LoadResult<NODE, CONTROLLER>(NODE node, @Nullable CONTROLLER controller) {

    }
}
