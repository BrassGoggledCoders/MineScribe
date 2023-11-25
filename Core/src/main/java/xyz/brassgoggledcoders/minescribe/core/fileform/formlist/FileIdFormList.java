package xyz.brassgoggledcoders.minescribe.core.fileform.formlist;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.service.IPackFileWatcherService;

import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public class FileIdFormList implements IFormList {
    private static final ServiceLoader<IPackFileWatcherService> SERVICE = ServiceLoader.load(IPackFileWatcherService.class);

    public static final Codec<FileIdFormList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("folderMatcher").forGetter(FileIdFormList::getFolderMatcher)
    ).apply(instance, FileIdFormList::new));

    private final String folderMatcher;
    private final Supplier<List<String>> matchedFiles;

    public FileIdFormList(String folderMatcher) {
        this.folderMatcher = folderMatcher;
        this.matchedFiles = Suppliers.memoize(() -> SERVICE.findFirst()
                .map(service -> service.getFileNamesForFolderMatch(this.folderMatcher))
                .orElse(Collections.emptyList()));
    }

    public String getFolderMatcher() {
        return folderMatcher;
    }

    @Override
    public List<String> getValues() {
        return this.matchedFiles.get();
    }

    @Override
    public @NotNull Codec<? extends IFormList> getCodec() {
        return CODEC;
    }
}
