package xyz.brassgoggledcoders.minescribe.editor.controller.tab;

import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentChildType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentParentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackContentType;
import xyz.brassgoggledcoders.minescribe.core.packinfo.SerializerType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.ZeroPaddedFormRenderer;
import xyz.brassgoggledcoders.minescribe.editor.util.FormUtils;
import xyz.brassgoggledcoders.minescribe.editor.util.FormUtils.FormSetup;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FormController implements IFileEditorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormController.class);
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    @FXML
    public VBox formPane;

    private FormSetup formSetup;
    private FormSetup serializerFormSetup;

    private Path filePath;
    private JsonObject existingObject;

    public void setFormInfo(Path filePath, PackContentParentType parentType, @Nullable PackContentChildType childType) {
        FileForm fileForm = parentType.getForm()
                .or(() -> Optional.ofNullable(childType)
                        .flatMap(PackContentType::getForm)
                )
                .orElseThrow();

        this.formSetup = FormUtils.setupForm(fileForm, () -> {
            List<SerializerType> serializerTypes = new ArrayList<>(Registries.getSerializerTypes().getFor(parentType));
            if (childType != null) {
                serializerTypes.addAll(Registries.getSerializerTypes().getFor(childType));
            }

            return serializerTypes;
        });
        this.formSetup.serializerFieldOpt()
                .ifPresent(serializerField -> serializerField.selectionProperty()
                        .addListener(this::handleNewSerializer)
                );

        this.filePath = filePath;

        FormRenderer renderer = new ZeroPaddedFormRenderer(this.formSetup.form());
        this.formPane.getChildren().add(renderer);
        this.formSetup.serializerFieldOpt()
                .ifPresent(serializerField -> this.reloadForm(serializerField.getSelection()));
        this.formPane.autosize();
        if (Files.exists(this.filePath)) {
            try {
                String jsonString = Files.readString(this.filePath);
                JsonElement jsonElement = GSON.fromJson(jsonString, JsonElement.class);
                if (jsonElement.isJsonObject()) {
                    this.existingObject = jsonElement.getAsJsonObject();
                    FormUtils.tryLoadForm(this.formSetup, this.existingObject);
                    if (this.serializerFormSetup != null) {
                        FormUtils.tryLoadForm(this.serializerFormSetup, this.existingObject);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Failed to read File {}", this.filePath, e);
            }
        }

    }

    public void saveForm(MouseEvent ignoredMouseEvent) {
        if (this.formSetup != null) {
            Form form = this.formSetup.form();
            Optional<Form> serializerFormOpt = Optional.ofNullable(this.serializerFormSetup)
                    .map(FormSetup::form);

            if (form.isValid() && serializerFormOpt.map(Form::isValid).orElse(false)) {
                JsonObject jsonObject = new JsonObject();
                FormUtils.trySaveForm(formSetup, jsonObject);
                serializerFormOpt.ifPresent(serializerForm -> FormUtils.tryLoadForm(formSetup, jsonObject));

                if (!jsonObject.isEmpty()) {
                    try {
                        Files.createDirectories(filePath.getParent());
                        Files.writeString(
                                filePath,
                                GSON.toJson(jsonObject),
                                StandardCharsets.UTF_8,
                                StandardOpenOption.WRITE,
                                StandardOpenOption.CREATE,
                                StandardOpenOption.TRUNCATE_EXISTING
                        );
                        this.existingObject = jsonObject;
                        FileHandler.getInstance().reloadClosestNode(filePath);
                    } catch (IOException e) {
                        LOGGER.error("Failed to write file {}", this.filePath, e);
                    }
                }
            }
        }
    }

    public void resetForm(MouseEvent ignoredMouseEvent) {
        if (this.formSetup != null) {
            this.formSetup.reset();
        }
        if (this.serializerFormSetup != null) {
            this.serializerFormSetup.reset();
        }
    }

    private void handleNewSerializer(ObservableValue<? extends SerializerType> observable, SerializerType oldValue, SerializerType newValue) {
        reloadForm(newValue);
    }

    private void reloadForm(SerializerType serializerType) {
        if (serializerType != null) {
            this.serializerFormSetup = FormUtils.setupForm(serializerType.fileForm(), () -> new ArrayList<>(
                    Registries.getSerializerTypes()
                            .getFor(serializerType)
            ));

            FormUtils.tryLoadForm(this.serializerFormSetup, this.existingObject);

            this.formPane.getChildren().add(new ZeroPaddedFormRenderer(this.serializerFormSetup.form()));
        } else if (this.serializerFormSetup != null) {
            this.formPane.getChildren().removeIf(node -> {
                if (node instanceof ZeroPaddedFormRenderer zeroPaddedFormRenderer) {
                    return zeroPaddedFormRenderer.getForm() == this.serializerFormSetup.form();
                }
                return false;
            });
            this.serializerFormSetup = null;
        }
    }

    @Override
    public @Nullable Path getPath() {
        return this.filePath;
    }
}
