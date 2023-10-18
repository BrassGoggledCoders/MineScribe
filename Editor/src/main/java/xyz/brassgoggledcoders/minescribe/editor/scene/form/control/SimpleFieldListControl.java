package xyz.brassgoggledcoders.minescribe.editor.scene.form.control;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.view.controls.SimpleControl;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.field.ListOfFields;

import java.util.ArrayList;
import java.util.List;

public class SimpleFieldListControl extends MineScribeSimpleControl<ListOfFields> {
    private GlyphFont glyphFont;
    private TitledPane titledPane;
    private VBox fieldPane;

    @Override
    public void initializeParts() {
        glyphFont = GlyphFontRegistry.font("FontAwesome");
        super.initializeParts();

        this.fieldPane = new VBox();
        this.fieldPane.setPadding(new Insets(10, 10, 10, 10));
        this.fieldPane.setSpacing(10);
        this.titledPane = new TitledPane("", this.fieldPane);
        HBox newElementBox = new HBox();
        newElementBox.alignmentProperty().set(Pos.BOTTOM_RIGHT);
        newElementBox.setMinHeight(10);
        Label newField = glyphFont.create(FontAwesome.Glyph.PLUS_SQUARE);
        newField.setOnMouseClicked(event -> {
            if (newField.isVisible()) {
                this.field.requestNewField();
            }
        });
        this.field.valueProperty()
                .sizeProperty()
                .addListener((observable, oldValue, newValue) -> newField.visibleProperty()
                        .set(newValue.intValue() < this.field.getMaximumFields())
                );

        for (Field<?> childrenFields: this.field.getValue()) {
            this.fieldPane.getChildren().add(this.createFieldNode(childrenFields));
        }
        newElementBox.getChildren().add(newField);
        this.fieldPane.getChildren().add(newElementBox);
    }

    @Override
    public void layoutParts() {
        super.layoutParts();

        this.layoutForNode(this.titledPane);
    }

    @Override
    public void setupBindings() {
        super.setupBindings();

        this.titledPane.textProperty().bind(this.field.labelProperty());
    }


    @Override
    public void setupEventHandlers() {
        super.setupEventHandlers();
        this.field.getValue().addListener((ListChangeListener<Field<?>>) c -> {
            if (c.next()) {
                if (c.wasAdded()) {
                    ArrayList<Node> simpleControls = new ArrayList<>();
                    for (int x = c.getFrom(); x < c.getTo(); x++) {
                        var newField = c.getList().get(x);
                        simpleControls.add(createFieldNode(newField));
                    }
                    int listStart = c.getFrom();
                    if (listStart >= this.fieldPane.getChildren().size()) {
                        listStart = this.fieldPane.getChildren().size() - 1;
                    }
                    this.fieldPane.getChildren()
                            .addAll(listStart, simpleControls);
                } else if (c.wasRemoved()) {
                    List<? extends Field<?>> removed = c.getRemoved();
                    this.fieldPane.getChildren()
                            .removeIf(node -> {
                                if (node instanceof FieldNode fieldNode) {
                                    boolean remove = removed.contains(fieldNode.field);
                                    if (remove) {
                                        this.field.valueProperty()
                                                .sizeProperty()
                                                .removeListener(fieldNode::changed);
                                    }
                                    return remove;
                                }
                                return false;
                            });
                } else {
                    System.out.println(c);
                }
            }
        });
    }

    private FieldNode createFieldNode(Field<?> newField) {
        FieldNode fieldNode = new FieldNode(
                newField,
                this.glyphFont.create(FontAwesome.Glyph.MINUS_SQUARE),
                this.field.valueProperty()
        );
        this.field.valueProperty()
                .sizeProperty()
                .addListener(fieldNode::changed);
        fieldNode.minusButtonVisible.set(true);
        return fieldNode;
    }

    private class FieldNode extends GridPane {
        private static final ColumnConstraints BIG = createForPercent(98D);
        private static final ColumnConstraints LITTLE = createForPercent(2D);

        private final BooleanProperty minusButtonVisible;
        private final ObservableList<Field<?>> listOfFields;
        private final Field<?> field;

        public FieldNode(Field<?> field, Label label, ObservableList<Field<?>> listOfFields) {
            this.listOfFields = listOfFields;
            this.field = field;
            this.getColumnConstraints().setAll(
                    BIG,
                    LITTLE
            );
            label.setOnMouseClicked(this::handleClick);

            StackPane removePane = new StackPane();
            removePane.setAlignment(Pos.CENTER_RIGHT);
            removePane.getChildren().add(label);

            this.minusButtonVisible = new SimpleBooleanProperty();
            label.visibleProperty().bind(this.minusButtonVisible);
            this.addRow(0, this.setUpField(this.field), removePane);
        }

        private void handleClick(MouseEvent mouseEvent) {
            if (this.minusButtonVisible.get() || mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                this.listOfFields.remove(this.field);
            }
        }


        @SuppressWarnings("unchecked")
        private SimpleControl<Field<?>> setUpField(Field<?> newField) {
            SimpleControl<Field<?>> simpleControl = (SimpleControl<Field<?>>) newField.getRenderer();
            simpleControl.setField(newField);
            simpleControl.setMaxWidth(Double.MAX_VALUE);
            int columns = newField.getSpan();
            if (newField.getLabel() == null || newField.getLabel().isEmpty()) {
                if (columns > 2) {
                    simpleControl.getColumnConstraints().clear();
                    for (int i = 0; i < columns; i++) {
                        ColumnConstraints colConst = new ColumnConstraints();
                        if (i < 2) {
                            colConst.setPercentWidth(0);
                        } else {
                            colConst.setPercentWidth(100.0 / columns + 2);
                        }
                        simpleControl.getColumnConstraints().add(colConst);
                    }
                }
            }
            return simpleControl;
        }

        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            this.minusButtonVisible.set(newValue.intValue() > SimpleFieldListControl.this.field.getMinimumFields());
        }

        private static ColumnConstraints createForPercent(double percent) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(percent);
            return columnConstraints;
        }
    }
}
