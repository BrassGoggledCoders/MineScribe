package xyz.brassgoggledcoders.minescribe.fxweaver;

import javafx.stage.Stage;

import java.util.Optional;

public record FxControllerAndStage<C, S extends Stage>(
        C controller,
        Optional<S> stage
) {
}
