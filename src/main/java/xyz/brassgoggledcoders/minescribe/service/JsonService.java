package xyz.brassgoggledcoders.minescribe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.brassgoggledcoders.minescribe.json.ProjectPathDeserializer;
import xyz.brassgoggledcoders.minescribe.json.RegistryHolderDeserializer;
import xyz.brassgoggledcoders.minescribe.json.TextComponentDeserializer;
import xyz.brassgoggledcoders.minescribe.model.ProjectPath;
import xyz.brassgoggledcoders.minescribe.model.component.TextComponent;
import xyz.brassgoggledcoders.minescribe.registry.Registry;
import xyz.brassgoggledcoders.minescribe.registry.RegistryHolder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class JsonService {
    private final ObjectMapper objectMapper;

    private final RegistryHolderDeserializer registryHolderDeserializer;

    @Autowired
    public JsonService(LocalizationService localizationService) {
        this.objectMapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();
        module.addDeserializer(TextComponent.class, new TextComponentDeserializer(localizationService));
        module.addDeserializer(ProjectPath.class, new ProjectPathDeserializer());

        this.registryHolderDeserializer = new RegistryHolderDeserializer();
        module.addDeserializer(RegistryHolder.class, this.registryHolderDeserializer);
        this.objectMapper.registerModule(module);
    }

    public <T> T readValue(InputStream inputStream, Class<T> clazz) throws IOException {
        return objectMapper.readValue(inputStream, clazz);
    }

    @Autowired
    public void setRegistries(ObjectProvider<List<Registry<?>>> registries) {
        this.registryHolderDeserializer.setRegistries(registries);
    }

}
