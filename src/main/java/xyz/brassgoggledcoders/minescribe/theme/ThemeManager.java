package xyz.brassgoggledcoders.minescribe.theme;

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
import xyz.brassgoggledcoders.minescribe.preferences.MineScribePreferences;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public final class ThemeManager {

    private static final Theme DEFAULT_THEME = new PrimerLight();
    private static final ObservableList<Theme> PROJECT_THEMES = FXCollections.observableList(List.of(
            DEFAULT_THEME,
            new PrimerDark(),
            new NordLight(),
            new NordDark(),
            new CupertinoLight(),
            new CupertinoDark(),
            new Dracula()
    ));

    private static final PseudoClass DARK = PseudoClass.getPseudoClass("dark");
    private static final PseudoClass USER_CUSTOM = PseudoClass.getPseudoClass("user-custom");

    public static final String DEFAULT_FONT_FAMILY_NAME = "Inter";
    public static final int DEFAULT_FONT_SIZE = 14;
    public static final int DEFAULT_ZOOM = 100;
    public static final List<Integer> SUPPORTED_FONT_SIZE = IntStream.range(8, 29).boxed().collect(Collectors.toList());
    public static final List<Integer> SUPPORTED_ZOOM = List.of(50, 75, 80, 90, 100, 110, 125, 150, 175, 200);

    private final Map<String, String> customCSSDeclarations = new LinkedHashMap<>();
    private final Map<String, String> customCSSRules = new LinkedHashMap<>();

    private Scene scene;

    private Theme currentTheme = null;
    private String fontFamily = DEFAULT_FONT_FAMILY_NAME;
    private int fontSize = DEFAULT_FONT_SIZE;
    private int zoom = DEFAULT_ZOOM;

    public Scene getScene() {
        return scene;
    }

    public void setup(Scene scene) {
        this.scene = Objects.requireNonNull(scene);

        this.setTheme(MineScribePreferences.getThemePreferences()
                .theme()
                .getValue()
        );
        MineScribePreferences.getThemePreferences()
                .theme()
                .subscribe(this::setTheme);
    }

    public ObservableList<Theme> getThemes() {
        return PROJECT_THEMES;
    }

    public Theme getTheme() {
        return currentTheme;
    }

    public String getThemeName() {
        Theme theme = getTheme();
        if (theme == null) {
            return DEFAULT_THEME.getName();
        } else {
            return theme.getName();
        }
    }

    public void setTheme(String themeName) {
        this.getThemes()
                .stream()
                .filter(theme -> theme.getName().equals(themeName))
                .findFirst()
                .ifPresent(this::setTheme);
    }

    public void setTheme(Theme theme) {
        if (theme == null) {
            theme = DEFAULT_THEME;
        }

        if (currentTheme != null) {
            animateThemeChange(Duration.millis(750));
        }

        Application.setUserAgentStylesheet(Objects.requireNonNull(theme.getUserAgentStylesheet()));
        getScene().getStylesheets()
                .setAll(
                        theme.getUserAgentStylesheet(),
                        "/minescribe/css/moderna-bridge.css"
                );
        getScene().getRoot().pseudoClassStateChanged(DARK, theme.isDarkMode());

        resetCustomCSS();

        currentTheme = theme;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        Objects.requireNonNull(fontFamily);
        setCustomDeclaration("-fx-font-family", "\"" + fontFamily + "\"");

        this.fontFamily = fontFamily;

        reloadCustomCSS();
    }

    public boolean isDefaultFontFamily() {
        return Objects.equals(DEFAULT_FONT_FAMILY_NAME, getFontFamily());
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int size) {
        if (!SUPPORTED_FONT_SIZE.contains(size)) {
            throw new IllegalArgumentException(
                    String.format("Font size must in the range %d-%dpx. Actual value is %d.",
                            SUPPORTED_FONT_SIZE.getFirst(),
                            SUPPORTED_FONT_SIZE.getLast(),
                            size
                    ));
        }

        setCustomDeclaration("-fx-font-size", size + "px");
        setCustomRule(".ikonli-font-icon", String.format("-fx-icon-size: %dpx;", size + 2));

        this.fontSize = size;

        var rawZoom = (int) Math.ceil((size * 1.0 / DEFAULT_FONT_SIZE) * 100);
        this.zoom = SUPPORTED_ZOOM.stream()
                .min(Comparator.comparingInt(i -> Math.abs(i - rawZoom)))
                .orElseThrow(NoSuchElementException::new);

        reloadCustomCSS();
    }

    public boolean isDefaultSize() {
        return DEFAULT_FONT_SIZE == fontSize;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        if (!SUPPORTED_ZOOM.contains(zoom)) {
            throw new IllegalArgumentException(
                    String.format("Zoom value must one of %s. Actual value is %d.", SUPPORTED_ZOOM, zoom)
            );
        }

        setFontSize((int) Math.ceil(zoom != 100 ? (DEFAULT_FONT_SIZE * zoom) / 100.0f : DEFAULT_FONT_SIZE));
        this.zoom = zoom;
    }

    private void setCustomDeclaration(String property, String value) {
        customCSSDeclarations.put(property, value);
    }

    @SuppressWarnings("SameParameterValue")
    private void setCustomRule(String selector, String rule) {
        customCSSRules.put(selector, rule);
    }

    @SuppressWarnings("unused")
    private void removeCustomRule(String selector) {
        customCSSRules.remove(selector);
    }

    private void animateThemeChange(Duration duration) {
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

    private void reloadCustomCSS() {
        Objects.requireNonNull(scene);
        StringBuilder css = new StringBuilder();

        css.append(".root:");
        css.append(USER_CUSTOM.getPseudoClassName());
        css.append(" {\n");
        customCSSDeclarations.forEach((k, v) -> {
            css.append("\t");
            css.append(k);
            css.append(": ");
            css.append(v);
            css.append(";\n");
        });
        css.append("}\n");

        customCSSRules.forEach((k, v) -> {
            // custom CSS is applied to the body,
            // thus it has a preference over accent color
            css.append(".body:");
            css.append(USER_CUSTOM.getPseudoClassName());
            css.append(" ");
            css.append(k);
            css.append(" {");
            css.append(v);
            css.append("}\n");
        });

        getScene().getRoot().getStylesheets().removeIf(uri -> uri.startsWith("data:text/css"));
        getScene().getRoot().getStylesheets().add(
                "data:text/css;base64," + Base64.getEncoder().encodeToString(css.toString().getBytes(StandardCharsets.UTF_8))
        );
        getScene().getRoot().pseudoClassStateChanged(USER_CUSTOM, true);
    }

    public void resetCustomCSS() {
        customCSSDeclarations.clear();
        customCSSRules.clear();
        getScene().getRoot().pseudoClassStateChanged(USER_CUSTOM, false);
    }

    private static class InstanceHolder {

        private static final ThemeManager INSTANCE = new ThemeManager();
    }

    public static ThemeManager getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
