package xyz.brassgoggledcoders.minescribe.form.test

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

class FormTestApplication extends Application {

    static void main(String[] args) {
        launch(FormTestApplication.class, args)
    }

    @Override
    void start(Stage primaryStage) {
        URL url = FormTestApplication.class.getResource("/xyz/brassgoggledcoders/minescribe/form/test/form_test.fxml")
        FXMLLoader loader = new FXMLLoader(url)

        primaryStage.setScene(new Scene(loader.<AnchorPane>load()))
        primaryStage.show()
    }
}
