package xyz.brassgoggledcoders.minescribe.editor.scene.form.control;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.view.controls.SimpleControl;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import xyz.brassgoggledcoders.minescribe.editor.scene.form.field.ListOfFields;

import java.util.ArrayList;

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
        newField.setOnMouseClicked(event -> this.field.requestNewField());
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
    @SuppressWarnings("unchecked")
    public void setupEventHandlers() {
        super.setupEventHandlers();
        this.field.getValue().addListener((ListChangeListener<Field<?>>) c -> {
            if (c.next()) {
                if (c.wasAdded()) {
                    ArrayList<Node> simpleControls = new ArrayList<>();
                    for (int x = c.getFrom(); x < c.getTo(); x++) {
                        var newField = c.getList().get(x);
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
                        GridPane gridPane = new GridPane();
                        ColumnConstraints bigPercent = new ColumnConstraints();
                        bigPercent.setPercentWidth(98D);
                        ColumnConstraints littlePercent = new ColumnConstraints();
                        littlePercent.setPercentWidth(2D);
                        gridPane.getColumnConstraints().setAll(
                                bigPercent,
                                littlePercent
                        );
                        Label remove = glyphFont.create(FontAwesome.Glyph.MINUS_SQUARE);
                        StackPane removePane = new StackPane();
                        removePane.setAlignment(Pos.CENTER_RIGHT);
                        removePane.getChildren().add(remove);
                        gridPane.addRow(0, simpleControl, removePane);
                        simpleControls.add(gridPane);
                    }
                    this.fieldPane.getChildren().addAll(c.getFrom(), simpleControls);
                } else if (c.wasRemoved()) {
                    this.fieldPane.getChildren().remove(c.getFrom(), c.getTo());
                } else {
                    System.out.println(c);
                }
            }
        });
    }
}
