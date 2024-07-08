package xyz.brassgoggledcoders.minescribe.initializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import xyz.brassgoggledcoders.minescribe.event.ProjectOpenedEvent;
import xyz.brassgoggledcoders.minescribe.registry.Registry;
import xyz.brassgoggledcoders.minescribe.registry.RegistryRoot;

import java.util.List;

@Component
public class RepositoryInitializer {
    private final List<? extends Registry<?>> registryList;

    @Autowired
    public RepositoryInitializer(List<? extends Registry<?>> registryList) {
        this.registryList = registryList;
    }

    @EventListener(ProjectOpenedEvent.class)
    public void projectOpened(ProjectOpenedEvent event) {
        RegistryRoot builtInFileRoot = new RegistryRoot(
                event.getPath()
                        .resolve(".minescribe")
                        .resolve("builtin"),
                100
        );

        for (Registry<?> registry : this.registryList) {
            registry.clear();

            registry.addRegistryRoot(builtInFileRoot);
        }
    }
}
