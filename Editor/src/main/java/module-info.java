module xyz.brassgoggledcoders.minescribe.editor {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.prefs;

    requires org.slf4j;
    requires jul.to.slf4j;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.dlsc.preferencesfx;
    requires org.kordamp.bootstrapfx.core;
    requires xyz.brassgoggledcoders.minescribe.core;
    requires org.jetbrains.annotations;
    requires com.google.gson;
    requires datafixerupper;
    requires com.google.common;
    requires org.graalvm.polyglot;

    exports xyz.brassgoggledcoders.minescribe.editor;
    opens xyz.brassgoggledcoders.minescribe.editor to javafx.fxml;
    exports xyz.brassgoggledcoders.minescribe.editor.controller;
    opens xyz.brassgoggledcoders.minescribe.editor.controller to javafx.fxml;
    exports xyz.brassgoggledcoders.minescribe.editor.controller.element;
    opens xyz.brassgoggledcoders.minescribe.editor.controller.element to javafx.fxml;
    exports xyz.brassgoggledcoders.minescribe.editor.controller.tab;
    opens xyz.brassgoggledcoders.minescribe.editor.controller.tab to javafx.fxml;

    exports xyz.brassgoggledcoders.minescribe.editor.exception;
    exports xyz.brassgoggledcoders.minescribe.editor.message;
    exports xyz.brassgoggledcoders.minescribe.editor.registry;
    exports xyz.brassgoggledcoders.minescribe.editor.javascript;

    exports xyz.brassgoggledcoders.minescribe.editor.scene.editortree;
    exports xyz.brassgoggledcoders.minescribe.editor.scene;
    opens xyz.brassgoggledcoders.minescribe.editor.scene to javafx.fxml;
}