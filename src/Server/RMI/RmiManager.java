package Server.RMI;

import java.io.File;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Naming;
import java.util.concurrent.atomic.AtomicBoolean;

public class RmiManager {
    private final RMI RmiService;
    private final String registry;
    private final String rmiServiceName;
    private Thread rmiHeartBeatThread;

    public RmiManager(String newServiceName, File databaseLocation, int RegistryPort, AtomicBoolean newServerVariable) throws java.rmi.RemoteException, SocketException {
        LocateRegistry.createRegistry(RegistryPort);
        rmiServiceName = newServiceName;
        registry = "rmi://localhost/" + rmiServiceName;
        RmiService = new RMI(rmiServiceName, RegistryPort, databaseLocation, newServerVariable);
    }

    public Thread getRmiHeartBeatThread() {return rmiHeartBeatThread;}

    public boolean registerService(){
        try{
            Naming.rebind(registry, RmiService);
            rmiHeartBeatThread = new Thread(RmiService);
            rmiHeartBeatThread.start();
        }
        catch (MalformedURLException | java.rmi.RemoteException e) {

            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}
