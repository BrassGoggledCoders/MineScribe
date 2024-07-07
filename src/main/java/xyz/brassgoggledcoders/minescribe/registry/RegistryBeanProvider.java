package xyz.brassgoggledcoders.minescribe.registry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import xyz.brassgoggledcoders.minescribe.model.PackRepository;
import xyz.brassgoggledcoders.minescribe.model.PackType;
import xyz.brassgoggledcoders.minescribe.service.JsonService;

@Configuration
public class RegistryBeanProvider {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Registry<PackType> packTypeRegistry(JsonService jsonService) {
        return new Registry<>(
                "packType",
                "pack_type",
                PackType.class,
                jsonService
        );
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Registry<PackRepository> packRepositoryRegistry(JsonService jsonService) {
        return new Registry<>(
                "packRepository",
                "pack_repository",
                PackRepository.class,
                jsonService
        );
    }
}
