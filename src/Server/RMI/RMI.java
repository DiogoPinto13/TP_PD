package Server.RMI;

import Server.DatabaseManager;

import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class RMI extends java.rmi.server.UnicastRemoteObject implements RmiInterface, Runnable {
    //variables
    private final String serviceName;
    private final AtomicBoolean serverVariable;
    private static final MulticastSocket socket = null; //Gonna try to figure out how multicast works

    public RMI(String newRegistry, AtomicBoolean newServerVariable) throws java.rmi.RemoteException{
        serviceName = newRegistry;
        serverVariable = newServerVariable;
        //MulticastSocket socket = new MulticastSocket(4444, InetAddress.getByName("230.44.44.44"));
    }

    public static void sendHeartbeat(){

    }

    @Override
    public void run() {
        while(serverVariable.get()){ //THE HEARTBEAT WORKS

            //heartbeat must contain:
            //porto de escuta do registry, (Registry.REGISTRY_PORT)
            //nome de registo do seu serviço RMI no registry local (serviceName)
            //número de versão da base DatabaseManager.executeQuery("query for version");
            //System.out.println("Heartbeat");

            try{
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
