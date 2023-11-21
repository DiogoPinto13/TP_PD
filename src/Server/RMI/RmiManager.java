package Server.RMI;

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

    public RmiManager(String newServiceName, AtomicBoolean newServerVariable) throws java.rmi.RemoteException, SocketException {
        LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        rmiServiceName = newServiceName;
        registry = "rmi://localhost/" + rmiServiceName;
        RmiService = new RMI(rmiServiceName, newServerVariable);
    }

    public boolean registerService(){
        try{
            Naming.rebind(registry, RmiService);
            Thread rmiHeartBeatThread = new Thread(RmiService);
            rmiHeartBeatThread.start();
        }
        catch (MalformedURLException | java.rmi.RemoteException e) {

            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}
