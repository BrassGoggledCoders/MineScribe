module xyz.brassgoggledcoders.minescribe.core {
    requires io.netty.handler;
    requires io.netty.codec;
    requires io.netty.transport;
    requires io.netty.buffer;

    requires org.jetbrains.annotations;
    requires com.google.gson;
    requires com.google.common;
    requires datafixerupper;

    exports xyz.brassgoggledcoders.minescribe.core;
    exports xyz.brassgoggledcoders.minescribe.core.fileform;
    exports xyz.brassgoggledcoders.minescribe.core.fileform.filefield;
    exports xyz.brassgoggledcoders.minescribe.core.fileform.listhandler;
    exports xyz.brassgoggledcoders.minescribe.core.functional;
    exports xyz.brassgoggledcoders.minescribe.core.info;
    exports xyz.brassgoggledcoders.minescribe.core.netty;
    exports xyz.brassgoggledcoders.minescribe.core.netty.packet;
    exports xyz.brassgoggledcoders.minescribe.core.packinfo;

}