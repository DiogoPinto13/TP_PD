package Shared.RMI;

public interface RmiClientInterface extends java.rmi.Remote {
    void writeFileChunk(byte [] fileChunk, int nbytes) throws java.rmi.RemoteException, java.io.IOException;
}
