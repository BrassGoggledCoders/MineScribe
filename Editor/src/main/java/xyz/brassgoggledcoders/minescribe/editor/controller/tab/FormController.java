package xyz.brassgoggledcoders.minescribe.editor.controller.tab;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;
import xyz.brassgoggledcoders.minescribe.editor.SceneUtils;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.registries.EditorRegistries;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.IEditorFormField;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FormController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormController.class);
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    @FXML
    public AnchorPane formPane;

    private Form form;
    private List<IEditorFormField<?>> editorFormFieldList;
    private Path filePath;

    public void setFormInfo(Path filePath, FileForm form) {
        this.filePath = filePath;
        this.editorFormFieldList = new ArrayList<>();
        for (IFileField field : form.getFields()) {
            IEditorFormField<?> editorFormField = EditorRegistries.getEditorFormFieldRegistry()
                    .createEditorFieldFor(field);

            if (editorFormField != null) {
                editorFormFieldList.add(editorFormField);
            }
        }
        this.form = Form.of(Group.of(
                editorFormFieldList.stream()
                        .map(IEditorFormField::asField)
                        .toArray(Field[]::new)
        ));

        FormRenderer renderer = new FormRenderer(this.form);
        SceneUtils.setAnchors(renderer);
        this.formPane.getChildren()
                .add(renderer);
        this.formPane.autosize();
        if (Files.exists(this.filePath)) {
            try {
                String jsonString = Files.readString(this.filePath);
                JsonElement jsonElement = GSON.fromJson(jsonString, JsonElement.class);
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    for (IEditorFormField<?> editorFormField : editorFormFieldList) {
                        editorFormField.loadFromJson(jsonObject);
                    }
                    this.form.persist();
                }
            } catch (IOException e) {
                LOGGER.error("Failed to read File {}", this.filePath, e);
            }
        }
    }

    public void saveForm(MouseEvent ignoredMouseEvent) {
        if (this.form != null) {
            this.form.persist();
            JsonObject result = new JsonObject();
            for (IEditorFormField<?> editorFormField : this.editorFormFieldList) {
                editorFormField.saveToJson(result);
            }
            if (!result.isEmpty()) {
                try {
                    Files.createDirectories(filePath.getParent());
                    Files.writeString(
                            filePath,
                            GSON.toJson(result),
                            StandardCharsets.UTF_8,
                            StandardOpenOption.WRITE,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING
                    );
                    FileHandler.getInstance().reloadClosestNode(filePath);
                } catch (IOException e) {
                    LOGGER.error("Failed to write file {}", this.filePath, e);
                }
            }
        }
    }

    public void resetForm(MouseEvent ignoredMouseEvent) {
        if (this.form != null) {
            this.form.reset();
        }
    }
}
