import xyz.brassgoggledcoders.minescribe.editor.service.EditorRegistryProviderService;

module xyz.brassgoggledcoders.minescribe.editor {
    uses xyz.brassgoggledcoders.minescribe.core.service.IRegistryProviderService;
    provides xyz.brassgoggledcoders.minescribe.core.service.IRegistryProviderService with EditorRegistryProviderService;

    requires javafx.controls;
    requires javafx.fxml;

    requires java.prefs;

    requires org.slf4j;
    requires jul.to.slf4j;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires xyz.brassgoggledcoders.minescribe.core;
    requires org.jetbrains.annotations;
    requires com.google.gson;
    requires datafixerupper;
    requires com.google.common;
    requires org.graalvm.polyglot;

    requires atlantafx.base;
    requires org.kordamp.ikonli.feather;
    requires org.kordamp.ikonli.javafx;

    exports xyz.brassgoggledcoders.minescribe.editor;
    opens xyz.brassgoggledcoders.minescribe.editor to javafx.fxml;
    exports xyz.brassgoggledcoders.minescribe.editor.controller;
    opens xyz.brassgoggledcoders.minescribe.editor.controller to javafx.fxml;
    exports xyz.brassgoggledcoders.minescribe.editor.controller.element;
    opens xyz.brassgoggledcoders.minescribe.editor.controller.element to javafx.fxml;
    exports xyz.brassgoggledcoders.minescribe.editor.controller.tab;
    opens xyz.brassgoggledcoders.minescribe.editor.controller.tab to javafx.fxml;

    exports xyz.brassgoggledcoders.minescribe.editor.exception;
    exports xyz.brassgoggledcoders.minescribe.editor.event.field;
    exports xyz.brassgoggledcoders.minescribe.editor.message;
    exports xyz.brassgoggledcoders.minescribe.editor.registry;
    exports xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy;
    exports xyz.brassgoggledcoders.minescribe.editor.javascript;

    exports xyz.brassgoggledcoders.minescribe.editor.scene.editortree;
    exports xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content;
    exports xyz.brassgoggledcoders.minescribe.editor.scene;
    opens xyz.brassgoggledcoders.minescribe.editor.scene to javafx.fxml;

    exports xyz.brassgoggledcoders.minescribe.editor.file;
    exports xyz.brassgoggledcoders.minescribe.editor.service;
}