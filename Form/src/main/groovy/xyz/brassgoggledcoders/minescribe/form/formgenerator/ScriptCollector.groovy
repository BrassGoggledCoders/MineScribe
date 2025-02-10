package xyz.brassgoggledcoders.minescribe.form.formgenerator

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.brassgoggledcoders.minescribe.form.ModData
import xyz.brassgoggledcoders.minescribe.form.util.MineScribePath

import java.nio.file.Path
import java.util.function.Consumer

class ScriptCollector {
    private static Logger LOGGER = LoggerFactory.getLogger(ScriptCollector.class)

    static void collectFromModData(Consumer<InputStream> scriptConsumer, List<ModData> modDataList) {
        Set<Path>
        for (ModData modData in modDataList) {
            for (MineScribePath mineScribePath in modData.scriptLocations()) {
                try {
                    InputStream inputStream = mineScribePath.getInputStream()
                    if (inputStream != null) {
                        scriptConsumer.accept(inputStream)
                    } else {
                        LOGGER.warn("Failed to find file for path: {}", mineScribePath)
                    }
                } catch (IOException ioException) {
                    LOGGER.error("Failed to read file for path: {}", mineScribePath, ioException)
                }
            }
        }
    }
}
