package xyz.brassgoggledcoders.minescribe.editor.exception;

import javafx.scene.control.Alert;

public class FormException extends Exception {
    public FormException(String message) {
        super(message);
    }

    public void showErrorDialog() {
        new Alert(Alert.AlertType.ERROR, this.getMessage()).show();
    }
}
