package Server.RMI;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.server.RemoteRef;

public class RMI extends java.rmi.server.UnicastRemoteObject implements RmiInterface, Runnable {
    //variables
    private final String registry;
    private boolean serverVariable;

    public RMI(String location, boolean newServerVariable) throws java.rmi.RemoteException{
        registry = location;
        serverVariable = newServerVariable;
    }

    @Override
    public void run() {
        while(serverVariable){
            try{
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //send heartbeat
            //System.out.println("Heartbeat");
        }
    }
}
