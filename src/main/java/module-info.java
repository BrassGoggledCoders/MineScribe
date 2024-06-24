module xyz.brassgoggledcoders.minescribe {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    requires atlantafx.base;
    requires com.dlsc.preferencesfx;

    requires java.prefs;

    requires org.slf4j;
    requires java.desktop;
    requires org.jetbrains.annotations;
    requires com.fasterxml.jackson.databind;
    requires io.vavr;

    opens xyz.brassgoggledcoders.minescribe to javafx.graphics;

    exports xyz.brassgoggledcoders.minescribe;
}