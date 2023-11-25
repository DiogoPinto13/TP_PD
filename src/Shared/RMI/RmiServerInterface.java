package Shared.RMI;

public interface RmiServerInterface extends java.rmi.Remote {
    //functions
    void getFile(RmiClientInterface cliRef) throws java.io.IOException, java.rmi.RemoteException;
}
