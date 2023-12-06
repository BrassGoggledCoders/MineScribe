package xyz.brassgoggledcoders.minescribe.editor.scene.dialog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.controlsfx.dialog.ExceptionDialog;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane.EditorFormPane;

import java.util.Collections;
import java.util.Optional;

public class EditorFormDialog<T> extends Dialog<T> {
    private final Codec<T> converter;
    private final EditorFormPane editorFormPane;

    private EditorFormDialog(EditorFormPane editorFormPane, Codec<T> converter) {
        this.converter = converter;
        this.editorFormPane = editorFormPane;

        this.setResizable(true);
        this.getDialogPane()
                .getButtonTypes()
                .addAll(ButtonType.CANCEL, ButtonTypes.CREATE);

        if (editorFormPane != null) {
            this.getDialogPane().setContent(editorFormPane);
            Button createButton = (Button) this.getDialogPane().lookupButton(ButtonTypes.CREATE);
            createButton.disableProperty().bind(Bindings.not(editorFormPane.validProperty()));
        }

        this.setResultConverter(this::convert);
    }

    private T convert(ButtonType buttonType) {
        if (buttonType == ButtonTypes.CREATE && this.editorFormPane != null) {
            this.editorFormPane.persist();
            JsonObject resultObject = this.editorFormPane.persistedObjectProperty()
                    .get();

            if (resultObject != null) {
                Optional<Pair<T, JsonElement>> value = this.converter.decode(JsonOps.INSTANCE, resultObject)
                        .result();

                if (value.isPresent()) {
                    return value.get()
                            .getFirst();
                }
            }
        }

        return null;
    }

    public static <U> EditorFormDialog<U> of(Codec<U> codec, FileForm fileForm) {
        try {
            return new EditorFormDialog<>(EditorFormPane.of(fileForm, Collections::emptyList, null), codec);
        } catch (FormException formException) {
            new ExceptionDialog(formException)
                    .showAndWait();
            return new EditorFormDialog<>(null, codec);
        }
    }
}
