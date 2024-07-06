package xyz.brassgoggledcoders.minescribe.fxweaver;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.rgielen.fxweaver.core.FxLoadException;
import net.rgielen.fxweaver.core.FxWeaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class FxStageWeaver extends FxWeaver {
    private final Logger LOGGER = LoggerFactory.getLogger(FxStageWeaver.class);

    private final ObjectProvider<ResourceBundle> bundleProvider;

    public FxStageWeaver(Callback<Class<?>, Object> beanFactory, Runnable closeCommand, ObjectProvider<ResourceBundle> bundleProvider) {
        super(beanFactory, closeCommand);
        this.bundleProvider = bundleProvider;
    }

    public <C, S extends Stage> FxControllerAndStage<C, S> loadStage(Class<C> controllerClass) {
        return this.loadByViewUsingFxmlLoader(
                new FXMLLoader(),
                controllerClass.getResource(this.buildFxmlReference(controllerClass))
        );
    }

    protected <C, V extends Stage> FxControllerAndStage<C, V> loadByViewUsingFxmlLoader(FXMLLoader loader, URL url) {
        try (InputStream fxmlStream = url.openStream()) {
            LOGGER.debug("Loading FXML resource at {}", url);
            loader.setLocation(url);
            loader.setControllerFactory(this::getBean);
            this.bundleProvider.ifAvailable(loader::setResources);
            V view = loader.load(fxmlStream);
            return new FxControllerAndStage<>(loader.getController(), Optional.ofNullable(view));
        } catch (IOException e) {
            throw new FxLoadException("Unable to load FXML file " + url, e);
        }
    }
}
