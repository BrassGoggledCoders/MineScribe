package xyz.brassgoggledcoders.minescribe.registry;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import xyz.brassgoggledcoders.minescribe.model.content.ContentCategory;
import xyz.brassgoggledcoders.minescribe.model.content.ContentType;
import xyz.brassgoggledcoders.minescribe.model.pack.PackRepository;
import xyz.brassgoggledcoders.minescribe.model.pack.PackType;
import xyz.brassgoggledcoders.minescribe.service.JsonService;

@Configuration
public class RegistryBeanProvider {

    @Bean
    @Order(5)
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
    @Order(10)
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Registry<PackRepository> packRepositoryRegistry(JsonService jsonService) {
        return new Registry<>(
                "packRepository",
                "pack_repository",
                PackRepository.class,
                jsonService
        );
    }

    @Bean
    @Order(15)
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Registry<ContentCategory> categoryRegistry(JsonService jsonService) {
        return new Registry<>(
                "contentCategory",
                "content_category",
                ContentCategory.class,
                jsonService
        );
    }

    @Bean
    @Order(20)
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Registry<ContentType> contentTypeRegistry(JsonService jsonService) {
        return new Registry<>(
                "contentType",
                "content_type",
                ContentType.class,
                jsonService
        );
    }
}
