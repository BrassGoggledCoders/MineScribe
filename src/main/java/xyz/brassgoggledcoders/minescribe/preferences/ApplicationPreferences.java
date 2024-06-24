package xyz.brassgoggledcoders.minescribe.preferences;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import javafx.beans.property.*;
import javafx.stage.Stage;
import xyz.brassgoggledcoders.minescribe.util.PreferenceHelper;

import java.nio.file.Path;

public class ApplicationPreferences {
    private final ObjectProperty<Path> lastProject;
    private final DoubleProperty xPos;
    private final DoubleProperty yPos;
    private final DoubleProperty width;
    private final DoubleProperty height;

    public ApplicationPreferences() {
        this.lastProject = new SimpleObjectProperty<>();
        this.lastProject.addListener((observable, oldValue, newValue) -> this.save());
        this.xPos = new SimpleDoubleProperty(Double.MIN_VALUE);
        this.xPos.addListener((observable, oldValue, newValue) -> this.save());
        this.yPos = new SimpleDoubleProperty(Double.MIN_VALUE);
        this.yPos.addListener((observable, oldValue, newValue) -> this.save());
        this.width = new SimpleDoubleProperty(600);
        this.width.addListener((observable, oldValue, newValue) -> this.save());
        this.height = new SimpleDoubleProperty(400);
        this.height.addListener((observable, oldValue, newValue) -> this.save());
    }

    @JsonGetter
    public Path getLastProject() {
        return lastProject.get();
    }

    @JsonSetter
    public void setLastProject(Path lastProject) {
        this.lastProject.set(lastProject);
    }

    @JsonGetter
    public double getXPos() {
        return xPos.get();
    }

    @JsonSetter
    public void setXPos(double xPos) {
        this.xPos.set(xPos);
    }

    @JsonGetter
    public double getYPos() {
        return yPos.get();
    }

    @JsonSetter
    public void setYPos(double yPos) {
        this.yPos.set(yPos);
    }

    @JsonGetter
    public double getWidth() {
        return width.get();
    }

    @JsonSetter
    public void setWidth(double width) {
        this.width.set(width);
    }

    @JsonGetter
    public double getHeight() {
        return height.get();
    }

    @JsonSetter
    public void setHeight(double height) {
        this.height.set(height);
    }

    private void save() {
        PreferenceHelper.savePreferences(this, "application");
    }

    public void subscribeTo(Stage stage) {
        stage.xProperty()
                .subscribe(xPoint -> this.setXPos(xPoint.doubleValue()));
        stage.yProperty()
                .subscribe(yPos -> this.setYPos(yPos.doubleValue()));
        stage.widthProperty()
                .subscribe(width -> this.setWidth(width.doubleValue()));
        stage.heightProperty()
                .subscribe(height -> this.setHeight(height.doubleValue()));
    }

    public static ApplicationPreferences load() {
        return PreferenceHelper.loadOrCreate(ApplicationPreferences.class, "application", ApplicationPreferences::new);
    }
}
