package xyz.brassgoggledcoders.minescribe.editor.controller.tab;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.structure.SingleSelectionField;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.SerializerInfo;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;
import xyz.brassgoggledcoders.minescribe.core.packinfo.*;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.registries.EditorRegistries;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.IEditorFormField;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.control.CellFactoryComboBoxControl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FormController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormController.class);
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    @FXML
    public TitledPane formPane;
    @FXML
    public TitledPane serializerFormPane;

    private Form currentForm;
    private List<IEditorFormField<?>> editorFormFieldList;

    private Form serializerForm;
    private List<IEditorFormField<?>> serializeEditorFormFieldList;

    private Path filePath;
    private JsonObject existingObject;

    @FXML
    public void initialize() {
        this.serializerFormPane.visibleProperty().bind(Bindings.isNotEmpty(this.serializerFormPane.getChildrenUnmodifiable()));
    }

    public void setFormInfo(Path filePath, PackContentParentType parentType, @Nullable PackContentChildType childType) {
        FileForm fileForm = parentType.getForm()
                .or(() -> Optional.ofNullable(childType)
                        .flatMap(PackContentType::getForm)
                )
                .orElseThrow();
        this.filePath = filePath;
        this.editorFormFieldList = new ArrayList<>();
        for (IFileField field : fileForm.getFields()) {
            IEditorFormField<?> editorFormField = EditorRegistries.getEditorFormFieldRegistry()
                    .createEditorFieldFor(field);

            if (editorFormField != null) {
                editorFormFieldList.add(editorFormField);
            }
        }

        Optional<SerializerInfo> serializerInfoOpt = fileForm.getSerializer();
        List<Field<?>> fields = editorFormFieldList.stream()
                .map(editorFormField -> editorFormField.asField()
                        .label(editorFormField.getFileField().getLabel())
                        .id(editorFormField.getFileField().getField())
                )
                .collect(Collectors.toList());

        Consumer<SerializerType> setType;
        if (serializerInfoOpt.isPresent()) {
            ObservableList<SerializerType> serializerTypes = FXCollections.observableArrayList();
            serializerTypes.addAll(Registries.getSerializerTypes().getFor(parentType));
            if (childType != null) {
                serializerTypes.addAll(Registries.getSerializerTypes().getFor(childType));
            }
            SingleSelectionField<SerializerType> serializerSelectionField = Field.ofSingleSelectionType(
                            new SimpleListProperty<>(serializerTypes)
                    )
                    .render(() -> new CellFactoryComboBoxControl<>(SerializerType::label))
                    .label(serializerInfoOpt.get().label());
            serializerSelectionField.selectionProperty()
                    .addListener(this::handleNewSerializer);
            serializerInfoOpt.flatMap(SerializerInfo::defaultType)
                    .map(Registries.getSerializerTypes()::getValue)
                    .ifPresent(serializerSelectionField.selectionProperty()::set);
            setType = serializerSelectionField.selectionProperty()::set;
            fields.add(serializerSelectionField);
        } else {
            setType = serializerType -> {
            };
        }

        this.currentForm = Form.of(Group.of(fields.toArray(Field[]::new)));

        FormRenderer renderer = new FormRenderer(this.currentForm);
        this.formPane.setContent(renderer);
        this.formPane.autosize();
        if (Files.exists(this.filePath)) {
            try {
                String jsonString = Files.readString(this.filePath);
                JsonElement jsonElement = GSON.fromJson(jsonString, JsonElement.class);
                if (jsonElement.isJsonObject()) {
                    this.existingObject = jsonElement.getAsJsonObject();
                    tryLoadForm(this.currentForm, this.editorFormFieldList, this.existingObject);
                    serializerInfoOpt.ifPresent(serializerInfo -> {
                        JsonElement typeInfo = this.existingObject.get(serializerInfo.fieldName());
                        if (typeInfo.isJsonPrimitive()) {
                            ResourceId.fromString(typeInfo.getAsString())
                                    .result()
                                    .map(Registries.getSerializerTypes()::getValue)
                                    .ifPresent(setType);
                        }
                    });
                    tryLoadForm(this.serializerForm, this.serializeEditorFormFieldList, this.existingObject);
                }
            } catch (IOException e) {
                LOGGER.error("Failed to read File {}", this.filePath, e);
            }
        }
    }

    public void saveForm(MouseEvent ignoredMouseEvent) {
        if (this.currentForm != null && this.currentForm.isValid() && (this.serializerForm == null || this.serializerForm.isValid())) {
            this.currentForm.persist();
            JsonObject result = new JsonObject();
            for (IEditorFormField<?> editorFormField : this.editorFormFieldList) {
                JsonElement savedJson = editorFormField.saveAsJson();
                if (savedJson != null && !savedJson.isJsonNull()) {
                    result.add(
                            editorFormField.getFileField()
                                    .getField(),
                            savedJson
                    );
                }
            }

            if (this.serializerForm != null && this.serializeEditorFormFieldList != null) {
                this.serializerForm.persist();
                for (IEditorFormField<?> editorFormField : this.serializeEditorFormFieldList) {
                    JsonElement savedJson = editorFormField.saveAsJson();
                    if (savedJson != null && !savedJson.isJsonNull()) {
                        result.add(
                                editorFormField.getFileField()
                                        .getField(),
                                savedJson
                        );
                    }
                }
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
                    this.existingObject = result;
                    FileHandler.getInstance().reloadClosestNode(filePath);
                } catch (IOException e) {
                    LOGGER.error("Failed to write file {}", this.filePath, e);
                }
            }
        }
    }

    public void resetForm(MouseEvent ignoredMouseEvent) {
        if (this.currentForm != null) {
            this.currentForm.reset();
        }
        if (this.serializerForm != null) {
            this.serializerForm.reset();
        }
    }

    private void handleNewSerializer(ObservableValue<? extends SerializerType> observable, SerializerType oldValue, SerializerType newValue) {
        this.serializeEditorFormFieldList = new ArrayList<>();
        for (IFileField field : newValue.fileForm().getFields()) {
            IEditorFormField<?> editorFormField = EditorRegistries.getEditorFormFieldRegistry()
                    .createEditorFieldFor(field);

            if (editorFormField != null) {
                this.serializeEditorFormFieldList.add(editorFormField);
            }
        }
        this.serializerForm = Form.of(Group.of(this.serializeEditorFormFieldList.stream()
                .map(editorFormField -> editorFormField.asField()
                        .label(editorFormField.getFileField().getLabel())
                        .id(editorFormField.getFileField().getField())
                )
                .toArray(Field[]::new)
        ));

        tryLoadForm(this.serializerForm, this.serializeEditorFormFieldList, this.existingObject);

        this.serializerFormPane.setContent(new FormRenderer(this.serializerForm));
    }

    private void tryLoadForm(Form form, List<IEditorFormField<?>> editorFormFields, JsonObject jsonObject) {
        if (form != null && editorFormFields != null && jsonObject != null) {
            for (IEditorFormField<?> editorFormField : editorFormFields) {
                if (jsonObject.has(editorFormField.getFileField().getField())) {
                    editorFormField.loadFromJson(jsonObject.get(editorFormField.getFileField().getField()));
                }
            }
            form.persist();
            form.reset();
        }
    }
}
