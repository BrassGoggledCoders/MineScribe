package xyz.brassgoggledcoders.minescribe.scene.control.cell;

import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;
import xyz.brassgoggledcoders.minescribe.model.component.TextComponent;
import xyz.brassgoggledcoders.minescribe.model.pack.Pack;
import xyz.brassgoggledcoders.minescribe.model.pack.PackType;
import xyz.brassgoggledcoders.minescribe.model.view.PackImportView;
import xyz.brassgoggledcoders.minescribe.registry.RegistryHolder;

import java.nio.file.Path;

public class PackViewListCell extends ListCell<PackImportView> {
    private final HBox fullNode;
    private final CheckBox importCheckBox;
    private final VBox packBox;
    private final Label packNameLabel;
    private final Label packPathLabel;
    private final CheckComboBox<RegistryHolder<PackType>> packTypeComboBox;

    private final Path projectPath;

    public PackViewListCell(Path projectPath, ObservableList<RegistryHolder<PackType>> packTypes) {
        this.projectPath = projectPath;

        this.fullNode = new HBox();
        this.fullNode.getStyleClass()
                .add("borders");
        this.importCheckBox = new CheckBox();
        VBox checkBoxBox = new VBox();
        checkBoxBox.setAlignment(Pos.CENTER);
        checkBoxBox.getChildren()
                .add(this.importCheckBox);
        checkBoxBox.setPadding(new Insets(5, 5, 5, 5));
        this.fullNode.getChildren()
                .add(checkBoxBox);

        this.packBox = new VBox();
        this.packBox.setPadding(new Insets(5, 5, 5, 5));
        this.packNameLabel = new Label();
        this.packPathLabel = new Label();
        this.packBox.getChildren()
                .addAll(packNameLabel, packPathLabel);
        HBox.setHgrow(this.packBox, Priority.ALWAYS);
        this.fullNode.getChildren()
                .add(packBox);

        this.packTypeComboBox = new CheckComboBox<>(packTypes);
        this.packTypeComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(RegistryHolder<PackType> packTypeRegistryHolder) {
                return packTypeRegistryHolder.getValue()
                        .label()
                        .getText();
            }

            @Override
            public RegistryHolder<PackType> fromString(String s) {
                return null;
            }
        });

        this.packTypeComboBox.getCheckModel()
                .getCheckedItems()
                .addListener(this::onChecksChanged);
        this.fullNode.getChildren()
                .add(packTypeComboBox);
    }

    public void updateItem(PackImportView item, boolean empty) {
        if (empty) {
            this.setGraphic(null);
            this.importCheckBox.selectedProperty()
                    .unbind();
            this.packNameLabel.textProperty().unbind();
            this.packPathLabel.textProperty().unbind();

            if (this.getItem() != null) {
                this.getItem()
                        .typesToImportProperty()
                        .unbind();
            }
        } else {
            this.setGraphic(fullNode);
            this.importCheckBox.selectedProperty()
                    .bindBidirectional(item.toImportProperty());
            this.packNameLabel.textProperty()
                    .bind(item.packProperty()
                            .map(Pack::getDescription)
                            .map(TextComponent::getText)
                    );
            this.packPathLabel.textProperty()
                    .bind(item.packProperty()
                            .map(Pack::path)
                            .map(this.projectPath::relativize)
                            .map(Path::toString)
                    );

            this.packTypeComboBox.getCheckModel()
                    .clearChecks();
            item.typesToImportProperty()
                    .forEach(this.packTypeComboBox.getCheckModel()::check);
        }
        super.updateItem(item, empty);
    }

    private void onChecksChanged(Observable ignoredObservable) {
        if (this.getItem() != null) {
            this.getItem()
                    .typesToImportProperty()
                    .getValue()
                    .setAll(this.packTypeComboBox.getCheckModel()
                            .getCheckedItems()
                    );
        }
    }
}
