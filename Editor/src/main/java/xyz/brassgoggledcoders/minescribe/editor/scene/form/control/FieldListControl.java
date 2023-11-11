package xyz.brassgoggledcoders.minescribe.editor.scene.form.control;

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
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileFieldDefinition;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;
import xyz.brassgoggledcoders.minescribe.editor.exception.FormException;
import xyz.brassgoggledcoders.minescribe.editor.registry.EditorRegistries;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.FieldContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.IValueContent;

import java.util.ArrayList;
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

    private final GlyphFont glyphFont;
    private final VBox fieldPane;

    public FieldListControl(IFileFieldDefinition fieldDefinition, Supplier<List<FieldValidation>> fieldValidationSupplier) {
        this.fieldDefinition = fieldDefinition;
        this.fieldValidationSupplier = fieldValidationSupplier;
        this.glyphFont = GlyphFontRegistry.font("FontAwesome");
        this.fieldPane = new VBox();
        this.values = new SimpleListProperty<>(FXCollections.observableArrayList(p -> new Observable[]{p}));
        this.invalidChildren = new SimpleLongProperty(0);

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
                        }
                    }
                }
            }
        });

        this.fieldPane.setPadding(new Insets(10, 10, 10, 10));
        this.fieldPane.setSpacing(10);
        this.setContent(this.fieldPane);
        HBox newElementBox = new HBox();
        newElementBox.alignmentProperty().set(Pos.BOTTOM_RIGHT);
        newElementBox.setMinHeight(10);
        Label newFieldLabel = glyphFont.create(FontAwesome.Glyph.PLUS_SQUARE);
        newFieldLabel.setOnMouseClicked(event -> {
            if (newFieldLabel.isVisible()) {
                this.addNewContent();
            }
        });
        Bindings.size(this.getChildren())
                .addListener((observable, oldValue, newValue) -> newFieldLabel.visibleProperty()
                        .set(newValue.intValue() <= this.maximumFields.get())
                );

        newElementBox.getChildren().add(newFieldLabel);
        this.fieldPane.getChildren().add(newElementBox);
    }

    public FieldContent<?> addNewContent() {
        try {
            FieldContent<?> fieldContent = EditorRegistries.getEditorFormFieldRegistry()
                    .createEditorFieldFor(this.fieldDefinition);
            if (fieldContent instanceof IValueContent<?, ?, ?> valueContent) {
                valueContent.withValidations(new ArrayList<>(this.fieldValidationSupplier.get()))
                        .withRequired(true);
                valueContent.validate();
            }
            int size = this.fieldPane.getChildren()
                    .size();
            this.fieldPane.getChildren()
                    .add(size - 1, createFieldNode(fieldContent));
            this.contents.add(fieldContent);
            return fieldContent;
        } catch (FormException formException) {
            LOGGER.error("Failed to create new Field Pane for Field List Control", formException);
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

    public LongProperty invalidChildren() {
        return invalidChildren;
    }

    private FieldNode createFieldNode(FieldContent<?> newField) {
        FieldNode fieldNode = new FieldNode(
                newField,
                this.glyphFont.create(FontAwesome.Glyph.MINUS_SQUARE),
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

        public FieldNode(FieldContent<?> field, Label label, ObservableList<FieldContent<?>> listOfFields) {
            this.listOfFields = listOfFields;
            this.field = field;

            label.setOnMouseClicked(this::handleClick);

            StackPane removePane = new StackPane();
            removePane.setAlignment(Pos.CENTER_RIGHT);
            removePane.getChildren().add(label);
            removePane.setPadding(new Insets(5));

            this.valid = new SimpleBooleanProperty(true);
            if (field instanceof IValueContent<?, ?, ?> valueContent) {
                valid.bind(valueContent.validProperty());
            }

            this.minusButtonVisible = new SimpleBooleanProperty();
            label.visibleProperty().bind(this.minusButtonVisible);
            HBox.setHgrow(field.getNode(), Priority.ALWAYS);
            HBox.setHgrow(label, Priority.NEVER);
            this.getChildren().addAll(field.getNode(), label);
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
