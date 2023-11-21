package Server.RMI;

import Server.DatabaseManager;
import Shared.RMIMulticastMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

public class RMI extends java.rmi.server.UnicastRemoteObject implements RmiInterface, Runnable {
    //variables
    private static final int port = 4444;
    private static final String ip = "230.44.44.44";
    private static String serviceName;
    private final AtomicBoolean serverVariable;
    private static MulticastSocket socket = null; //Gonna try to figure out how multicast works

    public RMI(String newRegistry, AtomicBoolean newServerVariable) throws java.rmi.RemoteException, SocketException {
        serviceName = newRegistry;
        serverVariable = newServerVariable;
        NetworkInterface nif;

        try {
            nif = NetworkInterface.getByInetAddress(InetAddress.getByName(ip)); //e.g., 127.0.0.1, 192.168.10.1, ...
        } catch (SocketException | NullPointerException | UnknownHostException | SecurityException ex) {
            nif = NetworkInterface.getByName("eth0"); //e.g., lo, eth0, wlan0, en0, ...
        }

        try {
            socket = new MulticastSocket(port);
            socket.joinGroup(new InetSocketAddress(ip, port), nif);
        }
        catch(IOException e){
            e.printStackTrace();
            throw new SocketException();
        }
    }

    public static void sendHeartbeat(){
        DatagramPacket pkt;
        try (ByteArrayOutputStream buff = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(buff)) {

            out.writeObject(new RMIMulticastMessage(serviceName, port, DatabaseManager.executeQuery("select versao from versao;").getInt("versao")));
            pkt = new DatagramPacket(buff.toByteArray(), buff.size(), InetAddress.getByName(ip), port);
            socket.send(pkt);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(serverVariable.get()){ //THE HEARTBEAT WORKS

            sendHeartbeat();

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
