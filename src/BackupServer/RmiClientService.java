package BackupServer;

import Shared.RMI.RmiClientInterface;

import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiClientService extends UnicastRemoteObject implements RmiClientInterface {

    FileOutputStream fout = null;

    //...
    public RmiClientService() throws java.rmi.RemoteException{

    }

    public synchronized void setFout(FileOutputStream fout) {
        this.fout = fout;
    }

    @Override
    public void writeFileChunk(byte[] fileChunk, int nbytes) throws RemoteException, IOException {
        fout.write(fileChunk, 0, nbytes);
    }
}
