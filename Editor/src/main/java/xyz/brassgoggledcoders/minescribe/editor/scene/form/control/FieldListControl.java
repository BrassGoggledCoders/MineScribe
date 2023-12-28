package xyz.brassgoggledcoders.minescribe.editor.scene.form.control;

import atlantafx.base.layout.InputGroup;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.dialog.ExceptionDialog;
import org.kordamp.ikonli.feather.Feather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;
import xyz.brassgoggledcoders.minescribe.editor.event.field.FieldMessagesEvent;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.registry.EditorRegistries;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.FieldContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.IValueContent;
import xyz.brassgoggledcoders.minescribe.editor.util.ButtonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class FieldListControl extends TitledPane {
    private final Logger LOGGER = LoggerFactory.getLogger(FieldListControl.class);
    private final IntegerProperty minimumFields = new SimpleIntegerProperty(0);
    private final IntegerProperty maximumFields = new SimpleIntegerProperty(Integer.MAX_VALUE);
    private final LongProperty invalidChildren;
    private final ListProperty<FieldContent<?>> contents = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<Property<?>> values;
    private final IFileFieldDefinition fieldDefinition;
    private final Supplier<List<FieldValidation>> fieldValidationSupplier;
    private final VBox fieldPane;

    public FieldListControl(IFileFieldDefinition fieldDefinition, Supplier<List<FieldValidation>> fieldValidationSupplier) {
        this.fieldDefinition = fieldDefinition;
        this.fieldValidationSupplier = fieldValidationSupplier;
        this.fieldPane = new VBox();
        this.values = new SimpleListProperty<>(FXCollections.observableArrayList(p -> new Observable[]{p}));
        this.invalidChildren = new SimpleLongProperty(0);
        this.getStyleClass()
                .addAll("paned-field");

        this.setup();
    }

    public void setup() {
        this.minimumFields.addListener((observable, oldValue, newValue) -> {
            if (this.contents.size() < newValue.intValue()) {
                FieldContent<?> fieldContent;
                do {
                    fieldContent = this.addNewContent();
                } while (fieldContent != null && this.contents.size() < newValue.intValue());
            }
        });

        this.contents.addListener((ListChangeListener<FieldContent<?>>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (FieldContent<?> fieldContent : c.getAddedSubList()) {
                        if (fieldContent instanceof IValueContent<?, ?, ?> valueContent) {
                            this.values.add((Property<?>) valueContent.valueProperty());
                        }
                    }
                } else if (c.wasRemoved()) {
                    for (FieldContent<?> fieldContent : c.getRemoved()) {
                        if (fieldContent instanceof IValueContent<?, ?, ?> valueContent) {
                            this.values.remove((Property<?>) valueContent.valueProperty());
                            if (!valueContent.messagesProperty().isEmpty()) {
                                fieldContent.getNode()
                                        .fireEvent(new FieldMessagesEvent(
                                                fieldContent.getFieldInfo(),
                                                Collections.emptySet(),
                                                valueContent.messagesProperty(),
                                                valueContent.messagesProperty()
                                        ));
                            }

                        }
                        fieldPane.getChildren()
                                .removeIf(node -> {
                                    if (node instanceof FieldNode fieldNode) {
                                        return fieldNode.field == fieldContent;
                                    }
                                    return false;
                                });
                    }
                }
            }
        });

        this.fieldPane.setPadding(new Insets(10, 10, 10, 10));
        this.fieldPane.setSpacing(10);
        this.setContent(this.fieldPane);
        HBox newElementBox = new HBox();
        newElementBox.alignmentProperty().set(Pos.BOTTOM_RIGHT);

        Button newFieldButton = ButtonUtils.createIconButton(Feather.PLUS_SQUARE, "Add Field");
        InputGroup newFieldGroup = new InputGroup(newFieldButton);
        newFieldButton.setOnMouseClicked(event -> this.addNewContent());
        newFieldButton.disableProperty()
                .bind(Bindings.greaterThanOrEqual(
                        Bindings.size(this.getChildren()),
                        this.maximumFields
                ));
        newElementBox.setMinHeight(newFieldGroup.getMinHeight());
        newElementBox.getChildren().add(newFieldGroup);
        this.fieldPane.getChildren().add(newElementBox);
    }

    public ReadOnlyIntegerProperty minimumFieldsProperty() {
        return this.minimumFields;
    }

    public ReadOnlyIntegerProperty maxFieldsProperty() {
        return this.maximumFields;
    }

    public FieldContent<?> addNewContent() {
        try {
            FieldContent<?> fieldContent = EditorRegistries.getEditorFormFieldRegistry()
                    .createEditorFieldFor(this.fieldDefinition);
            if (fieldContent instanceof IValueContent<?, ?, ?> valueContent) {
                valueContent.withValidations(new ArrayList<>(this.fieldValidationSupplier.get()))
                        .withRequired(true);
                //valueContent.validate();
            }

            StringProperty fieldName = fieldContent.getFieldInfo()
                    .name();
            if (fieldName.isBound()) {
                fieldName.unbind();
            }

            fieldContent.getFieldInfo()
                    .name()
                    .bind(Bindings.concat(
                            this.textProperty(),
                            "[",
                            Bindings.createIntegerBinding(
                                    () -> this.contents.indexOf(fieldContent),
                                    this.contents
                            ),
                            "]"
                    ));
            int size = this.fieldPane.getChildren()
                    .size();
            this.fieldPane.getChildren()
                    .add(size - 1, createFieldNode(fieldContent));
            this.contents.add(fieldContent);
            return fieldContent;
        } catch (FormException formException) {
            LOGGER.error("Failed to create new Field Pane for Field List Control", formException);
            new ExceptionDialog(formException)
                    .showAndWait();
        }
        return null;
    }

    private void recountInvalidChildren() {
        this.invalidChildren.set(this.fieldPane.getChildren()
                .stream()
                .filter(FieldNode.class::isInstance)
                .map(FieldNode.class::cast)
                .filter(fieldNode -> !fieldNode.validProperty().get())
                .count()
        );
    }

    public ReadOnlyLongProperty invalidChildren() {
        return invalidChildren;
    }

    private FieldNode createFieldNode(FieldContent<?> newField) {
        FieldNode fieldNode = new FieldNode(
                newField,
                this.contents
        );
        this.contents.sizeProperty()
                .addListener(new WeakChangeListener<Number>(fieldNode::changed));
        fieldNode.valid.addListener((observable, old, newValue) -> recountInvalidChildren());
        fieldNode.minusButtonVisible.set(true);
        return fieldNode;
    }

    public ListProperty<FieldContent<?>> contentsProperty() {
        return this.contents;
    }

    public ListProperty<Property<?>> valueProperty() {
        return this.values;
    }

    private class FieldNode extends HBox {

        private final BooleanProperty minusButtonVisible;
        private final ObservableList<FieldContent<?>> listOfFields;
        private final FieldContent<?> field;
        private final BooleanProperty valid;

        public FieldNode(FieldContent<?> field, ObservableList<FieldContent<?>> listOfFields) {
            this.listOfFields = listOfFields;
            this.field = field;

            this.valid = new SimpleBooleanProperty(true);
            if (field instanceof IValueContent<?, ?, ?> valueContent) {
                valid.bind(valueContent.validProperty());
            }

            HBox.setHgrow(field.getNode(), Priority.ALWAYS);
            this.minusButtonVisible = new SimpleBooleanProperty();
            if (field.getNode() instanceof InputGroup inputGroup) {
                Button minusButton = ButtonUtils.createIconButton(Feather.X_SQUARE, "Remove Field");
                minusButton.setOnMouseClicked(this::handleClick);
                inputGroup.getChildren()
                        .add(minusButton);
                this.getChildren().add(inputGroup);
            } else {
                Button minusButton = ButtonUtils.createIconButton(Feather.X_SQUARE, "Remove Field");
                minusButton.setOnMouseClicked(this::handleClick);
                InputGroup minusInputGroup = new InputGroup(minusButton);
                minusButton.setOnMouseClicked(this::handleClick);
                this.getChildren().addAll(field.getNode(), minusInputGroup);
            }

        }

        private void handleClick(MouseEvent mouseEvent) {
            if (this.minusButtonVisible.get() || mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                this.listOfFields.remove(this.field);
            }
        }

        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            this.minusButtonVisible.set(newValue.intValue() > FieldListControl.this.minimumFields.get());
        }

        public BooleanProperty validProperty() {
            return this.valid;
        }
    }
}
