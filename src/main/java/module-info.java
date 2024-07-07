module xyz.brassgoggledcoders.minescribe {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    requires atlantafx.base;
    requires com.dlsc.preferencesfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;
    requires org.controlsfx.controls;

    requires java.prefs;
    requires java.desktop;

    requires org.slf4j;
    requires org.jetbrains.annotations;
    requires com.fasterxml.jackson.databind;
    requires io.vavr;

    requires net.rgielen.fxweaver.core;
    requires net.rgielen.fxweaver.spring;

    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;
    requires java.rmi;

    exports xyz.brassgoggledcoders.minescribe;
    exports xyz.brassgoggledcoders.minescribe.controller.dialog;
    exports xyz.brassgoggledcoders.minescribe.event;
    exports xyz.brassgoggledcoders.minescribe.initializer;
    exports xyz.brassgoggledcoders.minescribe.model;
    exports xyz.brassgoggledcoders.minescribe.model.view;
    exports xyz.brassgoggledcoders.minescribe.preferences;
    exports xyz.brassgoggledcoders.minescribe.project;
    exports xyz.brassgoggledcoders.minescribe.registry;
    exports xyz.brassgoggledcoders.minescribe.scene.control.toolwindow;
    exports xyz.brassgoggledcoders.minescribe.service;
    exports xyz.brassgoggledcoders.minescribe.service.preferences;
    exports xyz.brassgoggledcoders.minescribe.service.ui;

    opens xyz.brassgoggledcoders.minescribe to javafx.graphics;
    opens xyz.brassgoggledcoders.minescribe.initializer to javafx.graphics;
}