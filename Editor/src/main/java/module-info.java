module xyz.brassgoggledcoders.minescribe.editor {
    requires javafx.controls;
    requires javafx.fxml;

    requires io.netty.buffer;
    requires io.netty.codec;
    requires io.netty.common;
    requires io.netty.handler;
    requires io.netty.transport;

    requires org.slf4j;

    requires livedirsfx;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.dlsc.preferencesfx;
    requires org.kordamp.bootstrapfx.core;
    requires xyz.brassgoggledcoders.minescribe.core;
    requires org.jetbrains.annotations;

    opens xyz.brassgoggledcoders.minescribe.editor to javafx.fxml;
    exports xyz.brassgoggledcoders.minescribe.editor;
    exports xyz.brassgoggledcoders.minescribe.editor.controller;
    opens xyz.brassgoggledcoders.minescribe.editor.controller to javafx.fxml;
}