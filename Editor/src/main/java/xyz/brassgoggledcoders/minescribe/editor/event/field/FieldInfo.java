package xyz.brassgoggledcoders.minescribe.editor.event.field;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import xyz.brassgoggledcoders.minescribe.editor.message.FieldMessage;

import java.util.UUID;

public record FieldInfo(
        UUID uniqueId,
        ObservableValue<String> name
) {

}
