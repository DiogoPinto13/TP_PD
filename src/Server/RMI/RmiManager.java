package Server.RMI;

import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Naming;

public class RmiManager {
    private final RMI RmiService;
    private Thread RmiHeartBeatThread;
    private final String registry;

    public RmiManager(String location, boolean newServerVariable) throws java.rmi.RemoteException{
        LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        registry = location;
        RmiService = new RMI(location, newServerVariable);

    }

    public boolean registerService(){
        String registration = "rmi://" + registry + "/RMIService";
        try{
            Naming.rebind(registration, RmiService);
            RmiHeartBeatThread = new Thread(RmiService);
            RmiHeartBeatThread.start();
        }
        catch (MalformedURLException | java.rmi.RemoteException e) {

            System.out.println(e);
            return false;
        }
        return true;
    }
}
