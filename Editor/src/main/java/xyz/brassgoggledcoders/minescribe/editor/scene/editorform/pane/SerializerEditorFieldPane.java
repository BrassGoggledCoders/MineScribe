package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.pane;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Label;
import javafx.util.Pair;
import xyz.brassgoggledcoders.minescribe.core.fileform.FileForm;
import xyz.brassgoggledcoders.minescribe.core.fileform.SerializerInfo;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileFieldInfo;
import xyz.brassgoggledcoders.minescribe.core.packinfo.IFullName;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.packinfo.SerializerType;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;
import xyz.brassgoggledcoders.minescribe.editor.registry.EditorRegistries;
import xyz.brassgoggledcoders.minescribe.editor.scene.SceneUtils;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control.SingleSelectionFieldControl;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SerializerEditorFieldPane extends EditorFieldPane<SingleSelectionFieldControl<SerializerType>> {
    private final SingleSelectionFieldControl<SerializerType> fieldControl;
    private final SerializerInfo serializerInfo;
    private final List<ResourceId> parents;

    private SerializerEditorFieldPane(FileForm fileForm, SerializerInfo serializerInfo, List<IFullName> parents,
                                      SingleSelectionFieldControl<SerializerType> fieldControl) {
        super(fileForm);
        this.serializerInfo = serializerInfo;
        this.fieldControl = fieldControl;
        this.parents = parents.stream()
                .map(IFullName::getFullName)
                .toList();
        this.labelProperty().set(new Label(serializerInfo.label()));

        setup();
    }

    private void setup() {
        this.fieldControl.valueProperty()
                .addListener((observable, oldValue, newValue) -> reloadSerializerType(newValue));

        this.changedProperty().bind(this.fieldControl.changedProperty());
        this.validProperty().bind(this.fieldControl.validProperty());

        SceneUtils.setAnchors(this.fieldControl.getNode());
        this.getChildren().add(this.fieldControl.getNode());
    }

    private void reloadSerializerType(SerializerType newValue) {
        EditorFormPane formPane = this.getFormPane();
        if (formPane != null) {
            FileForm serializerForm = null;
            if (newValue != null) {
                serializerForm = newValue.fileForm();
            }

            formPane.serializerFormProperty()
                    .set(serializerForm);
        }
    }

    @Override
    public SingleSelectionFieldControl<SerializerType> getContent() {
        return this.fieldControl;
    }

    @Override
    public void setValue(JsonElement jsonElement, JsonObject parentElement) {
        ObjectProperty<SerializerType> selected = this.getContent()
                .valueProperty();

        if (jsonElement == null && this.serializerInfo.fieldLess()) {
            this.findType(
                    parentElement,
                    Registries.getSerializerTypes()
                            .getValues()
                            .stream()
                            .filter(serializerType -> parents.contains(serializerType.parentId()))
            ).ifPresent(selected::set);
        }

        if (jsonElement != null && jsonElement.isJsonPrimitive()) {
            ResourceId.fromString(jsonElement.getAsString())
                    .result()
                    .map(EditorRegistries.getSerializerTypes()::getValue)
                    .ifPresent(selected::set);
        }

        if (selected.get() == null && jsonElement != null && jsonElement.isJsonPrimitive()) {
            String serializerId = jsonElement.getAsString();
            Registries.getSerializerTypes()
                    .getValues()
                    .stream()
                    .filter(serializerType -> parents.contains(serializerType.parentId()))
                    .filter(serializerType -> serializerType.serializerId().equals(serializerId))
                    .min((typeA, typeB) -> {
                        int posA = parents.indexOf(typeA.parentId());
                        int posB = parents.indexOf(typeB.parentId());

                        return Integer.compare(posA, posB);
                    })
                    .ifPresent(selected::set);
        }

        if (selected.get() == null) {
            serializerInfo.defaultType()
                    .map(EditorRegistries.getSerializerTypes()::getValue)
                    .ifPresent(selected::set);
        }

        List<SerializerType> items = this.getContent()
                .itemsProperty()
                .get();

        if (selected.get() == null) {
            items.stream()
                    .filter(Objects::nonNull)
                    .filter(serializerType -> Registries.getSerializerTypes().getKey(serializerType) == null)
                    .findFirst()
                    .ifPresent(selected::set);
        }

        if (selected.get() == null) {
            selected.set(items.get(0));
        }
    }

    private Optional<SerializerType> findType(JsonObject parentObject, Stream<SerializerType> potentials) {
        Set<String> jsonKeys = parentObject.keySet();
        return potentials.map(potential -> {
                    Map<String, Boolean> fieldKeys = potential.fileForm()
                            .getFields()
                            .stream()
                            .map(FileField::info)
                            .collect(Collectors.toMap(FileFieldInfo::field, FileFieldInfo::required));

                    boolean matchesRequiredFields = true;
                    for (Entry<String, Boolean> field : fieldKeys.entrySet()) {
                        if (field.getValue() && !jsonKeys.contains(field.getKey())) {
                            matchesRequiredFields = false;
                            break;
                        }
                    }

                    float percentMatched = getPercentMatched(jsonKeys, fieldKeys);

                    return new Pair<>(
                            potential,
                            new Pair<>(
                                    matchesRequiredFields,
                                    percentMatched
                            )
                    );
                })
                .min((pair1, pair2) -> {
                    Pair<Boolean, Float> value1 = pair1.getValue();
                    Pair<Boolean, Float> value2 = pair2.getValue();

                    int compare = Boolean.compare(value1.getKey(), value2.getKey());
                    if (compare == 0) {
                        return Float.compare(value1.getValue(), value2.getValue());
                    } else {
                        return compare;
                    }
                })
                .map(Pair::getKey);
    }

    private static float getPercentMatched(Set<String> jsonKeys, Map<String, Boolean> fieldKeys) {
        float percentMatched = 0;

        if (!jsonKeys.isEmpty()) {
            Set<String> matchingKeys = new HashSet<>();
            for (String key : jsonKeys) {
                if (fieldKeys.containsKey(key)) {
                    matchingKeys.add(key);
                }
            }
            percentMatched = (float) matchingKeys.size() / (float) jsonKeys.size();
        }
        return percentMatched;
    }

    @Override
    public JsonElement getValue() {
        SerializerType serializerType = this.getContent()
                .valueProperty()
                .get();
        if (!this.serializerInfo.fieldLess() && serializerType != null && Registries.getSerializerTypes().getKey(serializerType) != null) {
            return new JsonPrimitive(serializerType.serializerId());
        } else {
            return JsonNull.INSTANCE;
        }
    }

    @Override
    public String getFieldName() {
        return this.serializerInfo.fieldName();
    }

    @Override
    public int getSortOrder() {
        return 10000;
    }

    @Override
    public String toString() {
        return "SerializerEditorFieldPane{" +
                "serializerInfo=" + serializerInfo +
                '}';
    }

    public static Optional<SerializerEditorFieldPane> of(FileForm fileForm, List<IFullName> parents) {
        return fileForm.getSerializer()
                .map(serializerInfo -> {

                    List<SerializerType> serializerTypes = new ArrayList<>();
                    for (IFullName fullName : parents) {
                        serializerTypes.addAll(EditorRegistries.getSerializerTypes()
                                .getFor(fullName.getFullName())
                        );
                    }
                    if (serializerInfo.defaultForm().isPresent()) {
                        SerializerType defaultFieldsType = new SerializerType(
                                ResourceId.NULL,
                                ResourceId.NULL.toString(),
                                FancyText.literal("Default"),
                                serializerInfo.defaultForm()
                                        .get()
                        );
                        serializerTypes.add(0, defaultFieldsType);
                    }

                    SingleSelectionFieldControl<SerializerType> field = SingleSelectionFieldControl.of(
                                    serializerTypes,
                                    SerializerType::serializerId,
                                    SerializerType::label,
                                    (serializerType, string) -> serializerType.serializerId()
                                            .matches(string)
                            )
                            .withId(serializerInfo.fieldName())
                            .withLabel(serializerInfo.label())
                            .withRequired(true);

                    return new SerializerEditorFieldPane(fileForm, serializerInfo, parents, field);
                });
    }
}
