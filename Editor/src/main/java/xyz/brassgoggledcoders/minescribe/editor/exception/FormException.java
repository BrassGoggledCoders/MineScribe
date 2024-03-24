package xyz.brassgoggledcoders.minescribe.editor.exception;

import org.controlsfx.dialog.ExceptionDialog;

public class FormException extends Exception {
    public FormException(String message) {
        super(message);
    }

    public FormException(String message, Exception exception) {
        super(message, exception);
    }

    public void showErrorDialog() {
        new ExceptionDialog(this)
                .showAndWait();
    }
}
