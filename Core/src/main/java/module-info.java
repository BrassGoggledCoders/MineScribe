module xyz.brassgoggledcoders.minescribe.core {
    requires org.jetbrains.annotations;
    requires com.google.gson;
    requires com.google.common;
    requires datafixerupper;
    requires org.slf4j;

    exports xyz.brassgoggledcoders.minescribe.core.fileform;
    exports xyz.brassgoggledcoders.minescribe.core.fileform.filefield;
    exports xyz.brassgoggledcoders.minescribe.core.functional;
    exports xyz.brassgoggledcoders.minescribe.core.info;
    exports xyz.brassgoggledcoders.minescribe.core.packinfo;
    exports xyz.brassgoggledcoders.minescribe.core.registry;
    exports xyz.brassgoggledcoders.minescribe.core.registry.packcontenttype;
}