package xyz.brassgoggledcoders.minescribe.controller.dialog;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.util.Collections;
import java.util.List;

public interface IDialogController<V> {

    ReadOnlyStringProperty titleProperty();

    ReadOnlyBooleanProperty closingProperty();

    default V convert(ButtonType ignoredType) {
        return null;
    }

    default List<ButtonType> getButtonTypes() {
        return Collections.emptyList();
    }

    default void dialogInitialized(Dialog<V> dialog) {

    }
}
