package xyz.brassgoggledcoders.minescribe.schema;

import java.beans.Transient;

public interface IFileMatch {
    @Transient
    String[] getFileMatch();
}
