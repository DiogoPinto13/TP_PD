package Server.RMI;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;

public class RmiManager {
    private final RMI RmiService;
    private final String registry;

    public RmiManager(String location, boolean newServerVariable) throws java.rmi.RemoteException{
        registry = location;
        RmiService = new RMI(location, newServerVariable);
    }

    public boolean registerService(){
        String registration = "rmi://" + registry + "RMIService";
        try{
            Naming.bind(registration, RmiService);
        }
        catch (MalformedURLException | java.rmi.RemoteException e) {

            System.out.println(e);
            return false;
        }
        return true;
    }
}
