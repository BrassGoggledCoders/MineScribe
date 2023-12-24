package xyz.brassgoggledcoders.minescribe.core.fileform.formlist;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.JsonFieldNames;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;
import xyz.brassgoggledcoders.minescribe.core.registry.Registries;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;
import xyz.brassgoggledcoders.minescribe.core.util.FolderCollection;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class FileIdFormList implements IFormList<ResourceId> {

    public static final Codec<FileIdFormList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf(JsonFieldNames.FOLDER_MATCHER).forGetter(FileIdFormList::getFolderMatcher),
            Codec.STRING.fieldOf(JsonFieldNames.LEADING_CHARACTER).forGetter(FileIdFormList::getLeadingCharacter)
    ).apply(instance, FileIdFormList::new));

    private final String folderMatcher;
    private final String leadingCharacter;
    private final Supplier<FolderCollection> matchedFiles;

    public FileIdFormList(String folderMatcher, String leadingCharacter) {
        this.folderMatcher = folderMatcher;
        this.leadingCharacter = leadingCharacter;
        this.matchedFiles = Suppliers.memoize(() -> Registries.getFolderCollectionRegistry()
                .getOptionalValue(this.getFolderMatcher())
                .orElse(new FolderCollection(Collections.emptyList()))
        );
    }

    public String getLeadingCharacter() {
        return leadingCharacter;
    }

    public String getFolderMatcher() {
        return folderMatcher;
    }

    @Override
    public @NotNull String getKey(ResourceId value) {
        return this.leadingCharacter + value.toString();
    }

    @Override
    public @NotNull FancyText getLabel(ResourceId value) {
        return FancyText.literal(this.leadingCharacter + value.toString());
    }

    @Override
    public List<ResourceId> getValues() throws IOException {
        return this.matchedFiles.get()
                .getFileResourceIds();
    }

    @Override
    public @NotNull Codec<? extends IFormList<?>> getCodec() {
        return CODEC;
    }
}
