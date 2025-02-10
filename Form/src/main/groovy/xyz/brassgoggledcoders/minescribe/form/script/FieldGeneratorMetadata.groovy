package xyz.brassgoggledcoders.minescribe.form.script


import xyz.brassgoggledcoders.minescribe.form.Id
import xyz.brassgoggledcoders.minescribe.form.fieldgenerator.FieldContainer

import java.util.function.Function

class FieldGeneratorMetadata {
    Id id
    String description
    boolean hideLabel = false
    Function<FieldInfo, FieldContainer> fieldCreator

    boolean isValid() {
        return (id?.isValid() ?: false) && (fieldCreator != null)
    }
}
