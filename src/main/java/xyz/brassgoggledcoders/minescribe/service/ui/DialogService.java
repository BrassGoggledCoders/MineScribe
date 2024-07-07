package xyz.brassgoggledcoders.minescribe.service.ui;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Window;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.brassgoggledcoders.minescribe.controller.dialog.IDialogController;
import xyz.brassgoggledcoders.minescribe.service.LocalizationService;

@Service
public class DialogService {
    private final FxWeaver fxWeaver;
    private final LocalizationService localizationService;

    @Autowired
    public DialogService(FxWeaver fxWeaver, LocalizationService localizationService) {
        this.fxWeaver = fxWeaver;
        this.localizationService = localizationService;
    }

    public <C extends IDialogController<V>, N extends Node, V> void showDialogAndWait(Class<C> clazz, @Nullable Window window) {
        Dialog<V> dialog = new Dialog<>();

        FxControllerAndView<C, N> controllerAndView = this.fxWeaver.load(
                clazz,
                this.localizationService.getResourceBundle()
        );

        DialogPane dialogPane = dialog.getDialogPane();
        controllerAndView.getView()
                .ifPresent(dialogPane::setContent);

        if (controllerAndView.getController() instanceof IDialogController<V> dialogController) {
            dialog.titleProperty().bind(dialogController.titleProperty());
            if (dialogController.closingProperty() != null) {
                dialogController.closingProperty()
                        .addListener((obs, oldValue, newValue) -> {
                            if (newValue) {
                                dialog.close();
                            }
                        });
            }


            dialogPane.getButtonTypes()
                    .addAll(dialogController.getButtonTypes());

            if (dialogPane.getButtonTypes().isEmpty()) {
                dialogPane.getButtonTypes()
                        .add(ButtonType.CLOSE);
                dialogPane.lookupButton(ButtonType.CLOSE)
                        .setVisible(false);
            }

            dialog.setResultConverter(dialogController::convert);
        }

        dialog.initOwner(window);
        dialog.setDialogPane(dialogPane);
        dialog.showAndWait();
    }
}
