import xyz.brassgoggledcoders.minescribe.core.service.CoreRegistryProviderService;

module xyz.brassgoggledcoders.minescribe.core {
    uses xyz.brassgoggledcoders.minescribe.core.service.IPackFileWatcherService;
    uses xyz.brassgoggledcoders.minescribe.core.service.IRegistryProviderService;

    provides xyz.brassgoggledcoders.minescribe.core.service.IRegistryProviderService with CoreRegistryProviderService;

    requires org.jetbrains.annotations;
    requires com.google.gson;
    requires com.google.common;
    requires datafixerupper;

    requires org.slf4j;

    exports xyz.brassgoggledcoders.minescribe.core;
    exports xyz.brassgoggledcoders.minescribe.core.codec;
    exports xyz.brassgoggledcoders.minescribe.core.fileform;
    exports xyz.brassgoggledcoders.minescribe.core.fileform.filefield;
    exports xyz.brassgoggledcoders.minescribe.core.fileform.filefield.number;
    exports xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object;
    exports xyz.brassgoggledcoders.minescribe.core.fileform.formlist;
    exports xyz.brassgoggledcoders.minescribe.core.functional;
    exports xyz.brassgoggledcoders.minescribe.core.packinfo;
    exports xyz.brassgoggledcoders.minescribe.core.registry;
    exports xyz.brassgoggledcoders.minescribe.core.service;
    exports xyz.brassgoggledcoders.minescribe.core.text;
    exports xyz.brassgoggledcoders.minescribe.core.util;
    exports xyz.brassgoggledcoders.minescribe.core.validation;
}