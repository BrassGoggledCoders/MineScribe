module xyz.brassgoggledcoders.minescribe {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    requires java.prefs;

    requires org.slf4j;

    requires com.fasterxml.jackson.databind;

    opens xyz.brassgoggledcoders.minescribe to javafx.graphics;

    exports xyz.brassgoggledcoders.minescribe;
}