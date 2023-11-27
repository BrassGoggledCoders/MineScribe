package xyz.brassgoggledcoders.minescribe.core.service;

import java.util.List;

public interface IPackFileWatcherService {
    List<String> getFileNamesForFolderMatch(String pathMatcher);
}
