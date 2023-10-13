package xyz.brassgoggledcoders.minescribe.editor.controller.tab;

import com.dlsc.formsfx.model.structure.*;
import com.dlsc.formsfx.model.validators.SelectionLengthValidator;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.info.InfoKeys;
import xyz.brassgoggledcoders.minescribe.core.info.InfoRepository;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackTypeInfo;
import xyz.brassgoggledcoders.minescribe.editor.event.tab.CloseTabEvent;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;
import xyz.brassgoggledcoders.minescribe.editor.model.editortree.EditorItem;
import xyz.brassgoggledcoders.minescribe.editor.model.form.SmallerSimpleListViewControl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NewPackController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewPackController.class);

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @FXML
    public AnchorPane formContainer;
    @FXML
    public Button submit;

    private EditorItem parentItem;
    private Form form;
    private String tabId;

    @FXML
    public void initialize() {
        form = Form.of(Group.of(
                Field.ofMultiSelectionType(
                                new ArrayList<>(InfoRepository.getInstance()
                                        .getValue(InfoKeys.PACK_TYPES)
                                        .values()
                                ),
                                List.of(0, 1)
                        )
                        .label("Pack Type")
                        .render(SmallerSimpleListViewControl::new)
                        .id("packTypes")
                        .validate(SelectionLengthValidator.atLeast(1, "Must pick at least 1 Pack Type")),
                Field.ofStringType("")
                        .label("Name")
                        .id("name")
                        .validate(StringLengthValidator.atLeast(1, "Name Cannot Be Empty")),
                Field.ofStringType("")
                        .label("Description")
                        .id("description")
                        .multiline(true)
                        .validate(StringLengthValidator.atLeast(1, "Description Cannot be Empty"))
        ));
        FormRenderer formRenderer = new FormRenderer(form);

        AnchorPane.setTopAnchor(formRenderer, 0D);
        AnchorPane.setBottomAnchor(formRenderer, 0D);
        AnchorPane.setLeftAnchor(formRenderer, 0D);
        AnchorPane.setRightAnchor(formRenderer, 0D);

        formContainer.getChildren().add(formRenderer);
        formRenderer.requestLayout();

        submit.disableProperty().bind(Bindings.not(form.validProperty()));
    }

    public void setParentItem(EditorItem parentItem) {
        this.parentItem = parentItem;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public void onSubmit() {
        if (this.parentItem != null) {

            Map<String, Field<?>> fieldMap = this.form.getGroups()
                    .stream()
                    .map(Group::getElements)
                    .flatMap(List::stream)
                    .filter(e -> e instanceof Field)
                    .map(e -> (Field<?>) e)
                    .collect(Collectors.toMap(Element::getID, field -> field));

            Optional<String> name = getValue("name", String.class, fieldMap)
                    .filter(Predicate.not(String::isEmpty))
                    .findAny();
            String description = getValue("description", String.class, fieldMap)
                    .findAny()
                    .orElse("");
            List<PackTypeInfo> packTypes = getValue("packTypes", PackTypeInfo.class, fieldMap)
                    .toList();
            if (name.isPresent()) {
                Path packPath = this.parentItem.getPath().resolve(Path.of(name.get()));
                File packFolder = packPath.toFile();
                if (!packFolder.exists()) {
                    if (packFolder.mkdirs()) {
                        boolean createdAllPackTypeFolder = true;
                        for (PackTypeInfo packType : packTypes) {
                            createdAllPackTypeFolder &= packPath.resolve(packType.folder()).toFile().mkdir();
                        }
                        if (createdAllPackTypeFolder) {
                            int packVersion = packTypes.stream()
                                    .mapToInt(PackTypeInfo::version)
                                    .min()
                                    .orElse(0);

                            JsonObject packMeta = new JsonObject();
                            JsonObject packObject = new JsonObject();
                            packMeta.add("pack", packObject);

                            packObject.addProperty("pack_format", packVersion);
                            packObject.addProperty("description", description);
                            for (PackTypeInfo packType : packTypes) {
                                packObject.addProperty(packType.versionKey(), packType.version());
                            }

                            try {
                                Files.write(
                                        this.parentItem.getPath().resolve(name.get() + "/pack.mcmeta"),
                                        GSON.toJson(packMeta).getBytes()
                                );
                                this.formContainer.fireEvent(new CloseTabEvent(this.tabId));
                                FileHandler.getInstance().reloadDirectory(this.parentItem);
                            } catch (IOException e) {
                                LOGGER.error("Failed to write pack.mcmeta", e);
                            }
                        } else {
                            LOGGER.error("Failed to create all pack type folder");
                        }
                    } else {
                        LOGGER.error("Failed to create folder {}", packFolder);
                    }
                }
            }
        }
    }

    private <T> Stream<T> getValue(String fieldName, Class<T> tClass, Map<String, Field<?>> fieldMap) {
        Field<?> field = fieldMap.get(fieldName);
        if (field instanceof DataField<?, ?, ?> dataField) {
            Object o = dataField.getValue();
            if (tClass.isInstance(o)) {
                return Stream.of(tClass.cast(o));
            } else {
                throw new IllegalStateException("Value for " + fieldName + " was not the right type, was " + o);
            }
        } else if (field instanceof SingleSelectionField<?> selectionField) {
            return Stream.of(selectionField.getSelection())
                    .filter(tClass::isInstance)
                    .map(tClass::cast);
        } else if (field instanceof MultiSelectionField<?> selectionField) {
            return selectionField.getSelection()
                    .stream()
                    .filter(tClass::isInstance)
                    .map(tClass::cast);
        }
        throw new IllegalStateException("Couldn't find Value for " + fieldName);
    }
}
