package xyz.brassgoggledcoders.minescribe.controller.dialog;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.model.Pack;
import xyz.brassgoggledcoders.minescribe.model.PackType;
import xyz.brassgoggledcoders.minescribe.model.view.PackImportView;
import xyz.brassgoggledcoders.minescribe.registry.Registry;
import xyz.brassgoggledcoders.minescribe.scene.control.cell.PackViewListCell;
import xyz.brassgoggledcoders.minescribe.service.PackService;
import xyz.brassgoggledcoders.minescribe.service.ProjectService;

import java.util.List;

@Component
@FxmlView("/xyz/brassgoggledcoders/minescribe/dialog/pack_import.fxml")
public class PackImportController implements IDialogController<Void> {
    private final PackService packService;
    private final ProjectService projectService;
    private final Registry<PackType> packTypeRegistry;

    private final StringProperty titleProperty;

    private final ObservableList<Pack> importablePacks;

    @FXML
    private ListView<PackImportView> packListPane;

    public PackImportController(PackService packService, ProjectService projectService, Registry<PackType> packTypeRegistry) {
        this.packService = packService;
        this.projectService = projectService;
        this.packTypeRegistry = packTypeRegistry;

        this.titleProperty = new SimpleStringProperty(this, "title", "Import Packs");
        this.importablePacks = FXCollections.observableArrayList();
        this.importablePacks.addListener(this::importablePacksChanged);
    }

    @FXML
    public void initialize() {
        this.packListPane.setCellFactory(packImportViewListView -> new PackViewListCell(
                this.projectService.getProjectPath(),
                this.packTypeRegistry.getValues()
        ));

        this.importablePacks.addAll(this.packService.getPacksForImport());
    }

    private void importablePacksChanged(Change<? extends Pack> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                for (Pack pack : change.getAddedSubList()) {
                    this.packListPane.getItems()
                            .add(new PackImportView(pack));
                }
            } else if (change.wasRemoved()) {
                this.packListPane.getItems()
                        .removeIf(packImportView -> change.getRemoved()
                                .contains(packImportView.packProperty()
                                        .getValue()
                                )
                        );
            }
        }
    }

    @Override
    public ReadOnlyStringProperty titleProperty() {
        return this.titleProperty;
    }

    @Override
    public ReadOnlyBooleanProperty closingProperty() {
        return null;
    }

    @Override
    public Void convert(ButtonType buttonType) {


        return null;
    }

    @Override
    public List<ButtonType> getButtonTypes() {
        return List.of(ButtonType.APPLY, ButtonType.CANCEL);
    }
}
