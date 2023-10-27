package xyz.brassgoggledcoders.minescribe.core.fileform.filefield.object;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.FileField;
import xyz.brassgoggledcoders.minescribe.core.fileform.filefield.IFileField;
import xyz.brassgoggledcoders.minescribe.core.packinfo.ResourceId;

public class ReferencedObjectFileField extends FileField {
    public static final Codec<ReferencedObjectFileField> CODEC = IFileField.createCodec((instance, start) ->
            start.and(ResourceId.CODEC.fieldOf("objectId").forGetter(ReferencedObjectFileField::getObjectId))
                    .apply(instance, ReferencedObjectFileField::new)
    );

    private final ResourceId objectId;

    public ReferencedObjectFileField(String label, String field, int sortOrder, ResourceId objectId) {
        super(label, field, sortOrder);
        this.objectId = objectId;
    }

    public ResourceId getObjectId() {
        return objectId;
    }

    @Override
    public @NotNull Codec<? extends IFileField> getCodec() {
        return CODEC;
    }


}
