package xyz.brassgoggledcoders.minescribe.editor.theme;

import atlantafx.base.theme.*;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import xyz.brassgoggledcoders.minescribe.editor.Application;
import xyz.brassgoggledcoders.minescribe.editor.util.EditorSettings;

import java.util.List;
import java.util.Objects;

public final class ThemeManager {
    private static final ThemeManager INSTANCE = new ThemeManager();
    private static final List<Theme> PROJECT_THEMES = List.of(
            new PrimerLight(),
            new PrimerDark(),
            new NordLight(),
            new NordDark(),
            new CupertinoLight(),
            new CupertinoDark(),
            new Dracula()
    );

    private static final PseudoClass DARK = PseudoClass.getPseudoClass("dark");
    //private static final EventBus EVENT_BUS = DefaultEventBus.getInstance();

    private Scene scene;

    private Theme currentTheme = null;


    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = Objects.requireNonNull(scene);
    }

    public Theme getTheme() {
        if (this.currentTheme == null) {
            String savedThemeName = EditorSettings.getThemeName();
            if (savedThemeName != null) {
                PROJECT_THEMES.stream()
                        .filter(theme -> theme.getName().equals(savedThemeName))
                        .findFirst()
                        .ifPresent(this::setTheme);
            }
        }
        return currentTheme;
    }

    public List<Theme> getThemes() {
        return PROJECT_THEMES;
    }

    public Theme getDefaultTheme() {
        return PROJECT_THEMES.get(0);
    }

    public void setTheme(Theme theme) {
        Objects.requireNonNull(theme);

        EditorSettings.setThemeName(theme.getName());

        if (this.currentTheme != null) {
            animateThemeChange(Duration.millis(750));
        }

        Application.setUserAgentStylesheet(Objects.requireNonNull(theme.getUserAgentStylesheet()));
        if (this.currentTheme != null) {
            getScene()
                    .getStylesheets()
                    .remove(this.getTheme().getUserAgentStylesheet());
        }

        getScene()
                .getStylesheets()
                .add(theme.getUserAgentStylesheet());
        getScene()
                .getRoot()
                .pseudoClassStateChanged(DARK, theme.isDarkMode());

        getScene()
                .getRoot()
                .getStyleClass()
                .add(Styles.DENSE);

        currentTheme = theme;
        //EVENT_BUS.publish(new ThemeEvent(EventType.THEME_CHANGE));
    }

    private void animateThemeChange(Duration duration) {
        Image snapshot = scene.snapshot(null);
        Pane root = (Pane) scene.getRoot();

        ImageView imageView = new ImageView(snapshot);
        root.getChildren().add(imageView); // add snapshot on top

        var transition = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(imageView.opacityProperty(), 1, Interpolator.EASE_OUT)),
                new KeyFrame(duration, new KeyValue(imageView.opacityProperty(), 0, Interpolator.EASE_OUT))
        );
        transition.setOnFinished(e -> root.getChildren().remove(imageView));
        transition.play();
    }


    public static ThemeManager getInstance() {
        return INSTANCE;
    }
}
