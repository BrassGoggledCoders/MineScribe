package xyz.brassgoggledcoders.minescribe.core.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GatherRemote extends Remote {
    void log(String message) throws RemoteException;
}
