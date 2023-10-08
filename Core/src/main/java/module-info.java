module xyz.brassgoggledcoders.minescribe.core {
    requires io.netty.handler;
    requires io.netty.codec;
    requires io.netty.transport;
    requires io.netty.buffer;

    requires org.jetbrains.annotations;

    exports xyz.brassgoggledcoders.minescribe.core;
    exports xyz.brassgoggledcoders.minescribe.core.info;
    exports xyz.brassgoggledcoders.minescribe.core.netty;
    exports xyz.brassgoggledcoders.minescribe.core.netty.packet;
    exports xyz.brassgoggledcoders.minescribe.core.packinfo;

}