package xyz.brassgoggledcoders.minescribe.editor.scene.dialog;

import javafx.application.Platform;
import javafx.scene.control.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class ExceptionDialog extends Dialog<ButtonType> {
    public ExceptionDialog(String header, String text) {
        TextArea errorField = new TextArea(text);
        this.setTitle(header);
        this.getDialogPane().setContent(errorField);
        this.getDialogPane()
                .getButtonTypes()
                .add(ButtonType.OK);
    }

    public static void showDialog(String header, Throwable e) {
        try (ByteArrayOutputStream bs = new ByteArrayOutputStream(); PrintStream ps = new PrintStream(bs, true, StandardCharsets.UTF_8)) {
            e.printStackTrace(ps);
            Platform.runLater(() -> {
                new ExceptionDialog(header, bs.toString(StandardCharsets.UTF_8))
                        .showAndWait();
            });

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        Platform.runLater(() -> {
            new Alert(Alert.AlertType.ERROR, "Failed to show dialog for Error: %s".formatted(header))
                    .showAndWait();
        });

    }
}
