package xyz.brassgoggledcoders.minescribe.editor.service.fxml;

import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import org.controlsfx.dialog.ExceptionDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class FXMLService implements IFXMLService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FXMLService.class);
    private final Injector injector;

    @Inject
    public FXMLService(Injector injector) {
        this.injector = injector;
    }

    @Override
    public <NODE> NODE load(URL url) {
        try {
            FXMLLoader loader = new FXMLLoader(url);
            loader.setControllerFactory(injector::getInstance);
            return loader.load();
        } catch (IOException e) {
            LOGGER.error("Failed to load node {}", url, e);
            new ExceptionDialog(e)
                    .showAndWait();
            return null;
        }
    }
}
