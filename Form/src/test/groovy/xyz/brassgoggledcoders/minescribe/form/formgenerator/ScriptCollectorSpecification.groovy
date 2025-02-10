package xyz.brassgoggledcoders.minescribe.form.formgenerator

import spock.lang.Specification
import xyz.brassgoggledcoders.minescribe.form.ModData
import xyz.brassgoggledcoders.minescribe.form.util.MineScribePath
import xyz.brassgoggledcoders.minescribe.form.util.MineScribePathType

class ScriptCollectorSpecification extends Specification {
    def "loading scripts from classpath resources"() {
        given:
        def scriptList = new ArrayList<InputStream>()
        def modInfo = List.of(new ModData(List.of(new MineScribePath(
                MineScribePathType.RESOURCE,
                "/xyz/brassgoggledcoders/minescribe/form/scripts/field_generator/SeparatorFieldGenerator.groovy"
        ))))

        when:
        ScriptCollector.collectFromModData(scriptList::add, modInfo)

        then:
        !scriptList.empty

        cleanup:
        scriptList.forEach {
            it.close()
        }
    }
}
