package xyz.brassgoggledcoders.minescribe.core.remote;

import xyz.brassgoggledcoders.minescribe.MineScribe;

public class GatherRemoteImpl implements GatherRemote {
    @Override
    public void log(String message) {
        MineScribe.LOGGER.error(message);
    }
}
