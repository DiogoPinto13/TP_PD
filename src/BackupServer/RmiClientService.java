package BackupServer;

import Shared.RMI.RmiClientInterface;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiClientService extends UnicastRemoteObject implements RmiClientInterface {
    FileOutputStream fout = null;
    String localFilePath;
    public RmiClientService(String newLocalfilepath) throws java.rmi.RemoteException{
        localFilePath = newLocalfilepath;
    }
    @Override
    public synchronized void setFout() throws FileNotFoundException {
        this.fout = new FileOutputStream(localFilePath);
    }
    @Override
    public void writeFileChunk(byte[] fileChunk, int nbytes) throws RemoteException, IOException {
        fout.write(fileChunk, 0, nbytes);
        System.out.println("Backup Database Updated.");
    }
    @Override
    public boolean checkDatabaseVersion(int databaseVersion) throws RemoteException, IOException {
        if(databaseVersion - 1 == BackupServer.currentDatabaseVersion){
            BackupServer.currentDatabaseVersion = databaseVersion;
            return true;
        }
        return false;
    }
}
