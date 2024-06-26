package xyz.brassgoggledcoders.minescribe.service;

import atlantafx.base.theme.*;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public final class ThemeService {

    public static final Theme DEFAULT_THEME = new PrimerLight();
    public static final ObservableList<Theme> PROJECT_THEMES = FXCollections.observableList(List.of(
            DEFAULT_THEME,
            new PrimerDark(),
            new NordLight(),
            new NordDark(),
            new CupertinoLight(),
            new CupertinoDark(),
            new Dracula()
    ));

    private static final PseudoClass DARK = PseudoClass.getPseudoClass("dark");


    private final List<WeakReference<Scene>> scenes = new ArrayList<>();

    private Theme currentTheme = null;

    public Stream<Scene> getScenes() {
        return this.scenes.stream()
                .map(WeakReference::get)
                .filter(Objects::nonNull);
    }

    public void setup(Scene scene) {
        this.scenes.add(new WeakReference<>(Objects.requireNonNull(scene)));

        this.setTheme(
                scene,
                this.currentTheme
        );
    }

    private Theme getTheme(String themeName) {
        return this.getThemes()
                .stream()
                .filter(theme -> theme.getName()
                        .equals(themeName)
                )
                .findFirst()
                .orElse(DEFAULT_THEME);
    }

    public ObservableList<Theme> getThemes() {
        return PROJECT_THEMES;
    }

    public Theme getTheme() {
        return currentTheme;
    }

    public String getThemeName() {
        Theme theme = getTheme();
        return Objects.requireNonNullElse(theme, DEFAULT_THEME).getName();
    }

    public void setTheme(String themeName) {
        this.getThemes()
                .stream()
                .filter(theme -> theme.getName()
                        .equals(themeName)
                )
                .findFirst()
                .ifPresent(this::setTheme);
    }

    public void setTheme(Theme theme) {
        this.getScenes()
                .forEach(scene -> this.setTheme(scene, theme));
        this.currentTheme = theme;
    }

    public void setTheme(Scene scene, Theme theme) {
        if (theme == null) {
            theme = DEFAULT_THEME;
        }

        if (currentTheme != null && currentTheme != theme) {
            animateThemeChange(scene, Duration.millis(750));
        }

        Application.setUserAgentStylesheet(Objects.requireNonNull(theme.getUserAgentStylesheet()));
        scene.getStylesheets()
                .setAll(
                        theme.getUserAgentStylesheet(),
                        "/minescribe/css/moderna-bridge.css"
                );
        scene.getRoot()
                .pseudoClassStateChanged(DARK, theme.isDarkMode());
    }

    private void animateThemeChange(Scene scene, Duration duration) {
        WritableImage snapshot = scene.snapshot(null);
        Pane root = (Pane) scene.getRoot();

        ImageView imageView = new ImageView(snapshot);
        root.getChildren().add(imageView);

        var transition = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(imageView.opacityProperty(), 1, Interpolator.EASE_OUT)),
                new KeyFrame(duration, new KeyValue(imageView.opacityProperty(), 0, Interpolator.EASE_OUT))
        );
        transition.setOnFinished(e -> root.getChildren().remove(imageView));
        transition.play();
    }
}
