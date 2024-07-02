package xyz.brassgoggledcoders.minescribe.preferences;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import xyz.brassgoggledcoders.minescribe.scene.control.toolwindow.ToolWindowLocation;
import xyz.brassgoggledcoders.minescribe.util.PreferenceHelper;

import java.util.Map;

public class ToolWindowPreferences {
    private final ObservableMap<String, ToolWindowLocation> toolWindowLocations;

    private final SimpleDoubleProperty verticalWindowDivider;
    private final SimpleDoubleProperty leftHorizontalWindowDivider;
    private final SimpleDoubleProperty rightHorizontalWindowDivider;
    private final SimpleDoubleProperty leftToolBarDivider;
    private final SimpleDoubleProperty rightToolBarDivider;
    private final SimpleDoubleProperty bottomToolBarDivider;

    private boolean dirty;

    public ToolWindowPreferences() {
        this.toolWindowLocations = FXCollections.observableHashMap();
        this.toolWindowLocations.subscribe(this::markDirty);
        this.verticalWindowDivider = new SimpleDoubleProperty(0.2D);
        this.verticalWindowDivider.subscribe(this::markDirty);
        this.leftHorizontalWindowDivider = new SimpleDoubleProperty(0.2D);
        this.leftHorizontalWindowDivider.subscribe(this::markDirty);
        this.rightHorizontalWindowDivider = new SimpleDoubleProperty(0.2D);
        this.rightHorizontalWindowDivider.subscribe(this::markDirty);
        this.leftToolBarDivider = new SimpleDoubleProperty(0.5D);
        this.leftToolBarDivider.subscribe(this::markDirty);
        this.rightToolBarDivider = new SimpleDoubleProperty(0.5D);
        this.rightToolBarDivider.subscribe(this::markDirty);
        this.bottomToolBarDivider = new SimpleDoubleProperty(0.5D);
        this.bottomToolBarDivider.subscribe(this::markDirty);

        this.dirty = false;
    }

    private void markDirty() {
        this.dirty = true;
    }

    @JsonGetter
    public Map<String, ToolWindowLocation> getToolWindowLocations() {
        return toolWindowLocations;
    }

    @JsonGetter
    public void setToolWindowLocations(Map<String, ToolWindowLocation> toolWindowLocations) {
        this.toolWindowLocations.clear();
        this.toolWindowLocations.putAll(toolWindowLocations);
    }

    @JsonGetter
    public double getBottomToolBarDivider() {
        return bottomToolBarDivider.get();
    }

    @JsonSetter
    public void setBottomToolBarDivider(double value) {
        this.bottomToolBarDivider.set(value);
    }

    @JsonGetter
    public double getLeftToolBarDivider() {
        return leftToolBarDivider.get();
    }

    @JsonSetter
    public void setLeftToolBarDivider(double value) {
        this.leftToolBarDivider.set(value);
    }

    @JsonGetter
    public double getRightToolBarDivider() {
        return rightToolBarDivider.get();
    }

    @JsonSetter
    public void setRightToolBarDivider(double value) {
        this.rightToolBarDivider.set(value);
    }

    @JsonGetter
    public double getVerticalWindowDivider() {
        return verticalWindowDivider.get();
    }

    @JsonSetter
    public void setVerticalWindowDivider(double value) {
        this.verticalWindowDivider.set(value);
    }

    @JsonGetter
    public double getLeftHorizontalWindowDivider() {
        return leftHorizontalWindowDivider.get();
    }

    @JsonSetter
    public void setLeftHorizontalWindowDivider(double value) {
        this.leftHorizontalWindowDivider.set(value);
    }

    @JsonGetter
    public double getRightHorizontalWindowDivider() {
        return rightHorizontalWindowDivider.get();
    }

    @JsonSetter
    public void setRightHorizontalWindowDivider(double value) {
        this.rightHorizontalWindowDivider.set(value);
    }

    public void trySave() {
        if (this.dirty) {
            PreferenceHelper.savePreferences(this, "toolWindows");
            this.dirty = false;
        }
    }

    public static ToolWindowPreferences load() {
        return PreferenceHelper.loadOrCreate(ToolWindowPreferences.class, "toolWindows", ToolWindowPreferences::new);
    }
}
