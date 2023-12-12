package xyz.brassgoggledcoders.minescribe.editor.message;

import xyz.brassgoggledcoders.minescribe.editor.event.field.FieldInfo;

public record FieldMessage(
        FieldInfo fieldInfo,
        MessageType type,
        String message
) {
}
