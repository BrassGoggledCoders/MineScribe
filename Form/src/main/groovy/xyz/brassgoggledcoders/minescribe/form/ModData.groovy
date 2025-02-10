package xyz.brassgoggledcoders.minescribe.form

import xyz.brassgoggledcoders.minescribe.form.util.MineScribePath
import xyz.brassgoggledcoders.minescribe.form.util.MineScribePathType

record ModData(
        List<MineScribePath> scriptLocations
) {


    static ModData minescribeData() {
        return new ModData(List.of(
                new MineScribePath(
                        MineScribePathType.RESOURCE,
                        "/xyz/brassgoggledcoders/minescribe/form/scripts/field_generator/SeparatorFieldGenerator.groovy"
                ),
                new MineScribePath(
                        MineScribePathType.RESOURCE,
                        "/xyz/brassgoggledcoders/minescribe/form/scripts/field_generator/TextFieldGenerator.groovy"
                )
        ))
    }
}