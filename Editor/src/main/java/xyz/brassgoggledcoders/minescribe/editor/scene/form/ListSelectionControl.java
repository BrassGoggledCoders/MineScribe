package xyz.brassgoggledcoders.minescribe.editor.scene.form;

import com.dlsc.formsfx.model.structure.MultiSelectionField;
import com.dlsc.formsfx.view.controls.SimpleControl;
import javafx.collections.ListChangeListener;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.ListSelectionView;

import java.util.ArrayList;
import java.util.List;

public class ListSelectionControl<T> extends SimpleControl<MultiSelectionField<T>> {
    protected Label fieldLabel;
    protected ListSelectionView<T> listView = new ListSelectionView<>();

    protected boolean preventUpdate;

    @Override
    public void initializeParts() {
        super.initializeParts();

        getStyleClass().add("simple-listview-control");

        fieldLabel = new Label(field.labelProperty().getValue());
        resetLists();
    }

    private void resetLists() {
        List<T> sourceItems = new ArrayList<>();
        List<T> selectedItems = new ArrayList<>();
        for (int i = 0; i < field.getItems().size(); i++) {
            T value = field.itemsProperty().get(i);
            if (field.getSelection().contains(value)) {
                selectedItems.add(value);
            } else {
                sourceItems.add(value);
            }
        }
        this.listView.getSourceItems().clear();
        this.listView.getSourceItems().addAll(sourceItems);
        this.listView.getTargetItems().clear();
        this.listView.getTargetItems().addAll(selectedItems);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void layoutParts() {
        super.layoutParts();

        int columns = field.getSpan();

        listView.setPrefHeight(200);

        Node labelDescription = field.getLabelDescription();
        Node valueDescription = field.getValueDescription();

        add(fieldLabel, 0, 0, 2, 1);
        if (labelDescription != null) {
            GridPane.setValignment(labelDescription, VPos.TOP);
            add(labelDescription, 0, 1, 2, 1);
        }
        add(listView, 2, 0, columns - 2, 1);
        if (valueDescription != null) {
            GridPane.setValignment(valueDescription, VPos.TOP);
            add(valueDescription, 2, 1, columns - 2, 1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupBindings() {
        super.setupBindings();

        fieldLabel.textProperty().bind(field.labelProperty());
        listView.disableProperty().bind(field.editableProperty().not());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupValueChangedListeners() {
        super.setupValueChangedListeners();

        field.getSelection().addListener((ListChangeListener<T>) c -> {
            if (preventUpdate) {
                return;
            }

            preventUpdate = true;

            resetLists();

            preventUpdate = false;
        });

        field.errorMessagesProperty().addListener((observable, oldValue, newValue) -> toggleTooltip(listView));
        field.tooltipProperty().addListener((observable, oldValue, newValue) -> toggleTooltip(listView));
        listView.focusedProperty().addListener((observable, oldValue, newValue) -> toggleTooltip(listView));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupEventHandlers() {
        listView.setOnMouseEntered(event -> toggleTooltip(listView));
        listView.setOnMouseExited(event -> toggleTooltip(listView));

        listView.getTargetItems().addListener((ListChangeListener<T>) c -> {
            if (preventUpdate) {
                return;
            }

            preventUpdate = true;

            for (int i = 0; i < field.itemsProperty().size(); i++) {
                if (listView.targetItemsProperty().get().contains(field.itemsProperty().get(i))) {
                    field.select(i);
                } else {
                    field.deselect(i);
                }
            }

            preventUpdate = false;
        });
    }

}
