package xyz.brassgoggledcoders.minescribe;

import javafx.application.Application;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import xyz.brassgoggledcoders.minescribe.fxweaver.FxStageWeaver;

import java.util.ResourceBundle;

@SpringBootApplication
public class MineScribeSpringApplication {
    public static void main(String[] args) {
        Application.launch(MineScribeApplication.class, args);
    }

    @Bean
    public FxStageWeaver fxWeaver(ConfigurableApplicationContext applicationContext, ObjectProvider<ResourceBundle> bundleProvider) {
        return new FxStageWeaver(applicationContext::getBean, applicationContext::close, bundleProvider);
    }
}
