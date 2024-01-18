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

    requires org.jetbrains.annotations;

    requires datafixerupper;
    requires org.graalvm.polyglot;

    requires com.google.common;
    requires com.google.gson;

    requires jakarta.inject;
    requires com.google.guice;

    requires atlantafx.base;
    requires org.kordamp.ikonli.feather;
    requires org.kordamp.ikonli.javafx;

    requires xyz.brassgoggledcoders.minescribe.core;

    opens xyz.brassgoggledcoders.minescribe.editor to javafx.fxml;
    opens xyz.brassgoggledcoders.minescribe.editor.controller to javafx.fxml;
    opens xyz.brassgoggledcoders.minescribe.editor.controller.element to javafx.fxml;
    opens xyz.brassgoggledcoders.minescribe.editor.controller.tab to javafx.fxml;
    opens xyz.brassgoggledcoders.minescribe.editor.scene to javafx.fxml;

    opens xyz.brassgoggledcoders.minescribe.editor.service to com.google.guice;

    exports xyz.brassgoggledcoders.minescribe.editor;
    exports xyz.brassgoggledcoders.minescribe.editor.controller;
    exports xyz.brassgoggledcoders.minescribe.editor.controller.element;
    exports xyz.brassgoggledcoders.minescribe.editor.controller.tab;
    exports xyz.brassgoggledcoders.minescribe.editor.exception;
    exports xyz.brassgoggledcoders.minescribe.editor.event.field;
    exports xyz.brassgoggledcoders.minescribe.editor.file;
    exports xyz.brassgoggledcoders.minescribe.editor.message;
    exports xyz.brassgoggledcoders.minescribe.editor.javascript;
    exports xyz.brassgoggledcoders.minescribe.editor.registry;
    exports xyz.brassgoggledcoders.minescribe.editor.registry.hierarchy;
    exports xyz.brassgoggledcoders.minescribe.editor.project;
    exports xyz.brassgoggledcoders.minescribe.editor.scene;
    exports xyz.brassgoggledcoders.minescribe.editor.scene.editortree;
    exports xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content;
    exports xyz.brassgoggledcoders.minescribe.editor.scene.tab;
    exports xyz.brassgoggledcoders.minescribe.editor.service;
    exports xyz.brassgoggledcoders.minescribe.editor.service.editoritem;
    exports xyz.brassgoggledcoders.minescribe.editor.service.fxml;
    exports xyz.brassgoggledcoders.minescribe.editor.service.page;
    exports xyz.brassgoggledcoders.minescribe.editor.service.project;
    exports xyz.brassgoggledcoders.minescribe.editor.service.registry;
    exports xyz.brassgoggledcoders.minescribe.editor.service.tab;
}