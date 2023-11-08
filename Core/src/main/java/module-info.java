module xyz.brassgoggledcoders.minescribe.core {
    requires org.jetbrains.annotations;
    requires com.google.gson;
    requires com.google.common;
    requires datafixerupper;
    requires org.graalvm.polyglot;

    requires org.slf4j;
    requires jul.to.slf4j;

    exports xyz.brassgoggledcoders.minescribe.core.fileform;
    exports xyz.brassgoggledcoders.minescribe.core.fileform.filefield;
    exports xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number;
    exports xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object;
    exports xyz.brassgoggledcoders.minescribe.core.functional;
    exports xyz.brassgoggledcoders.minescribe.core.info;
    exports xyz.brassgoggledcoders.minescribe.core.packinfo;
    exports xyz.brassgoggledcoders.minescribe.core.registry;
    exports xyz.brassgoggledcoders.minescribe.core.registry.packcontenttype;
    exports xyz.brassgoggledcoders.minescribe.core.util;
    exports xyz.brassgoggledcoders.minescribe.core.validation;

    opens xyz.brassgoggledcoders.minescribe.core.registry to org.graalvm.polyglot;
    opens xyz.brassgoggledcoders.minescribe.core.validation to org.graalvm.polyglot;
}