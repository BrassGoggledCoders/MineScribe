package xyz.brassgoggledcoders.minescribe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import xyz.brassgoggledcoders.minescribe.event.StageReadyEvent;

public class MineScribeApplication extends Application {
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        this.applicationContext = new SpringApplicationBuilder()
                .sources(MineScribeSpringApplication.class)
                .run(this.getParameters()
                        .getRaw()
                        .toArray(new String[0])
                );
    }

    @Override
    public void start(Stage stage) {
        this.applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void stop() {
        this.applicationContext.close();
        Platform.exit();
    }
}
