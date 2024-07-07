package xyz.brassgoggledcoders.minescribe.controller.dialog;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/xyz/brassgoggledcoders/minescribe/dialog/pack_import.fxml")
public class PackImportController {
    @FXML
    private HBox packList;


}
