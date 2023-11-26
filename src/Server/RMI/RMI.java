package Server.RMI;

import Server.DatabaseManager;
import Shared.RMIMulticastMessage;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.net.*;
import Shared.RMI.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RMI extends UnicastRemoteObject implements RmiServerInterface, Runnable {
    //variables
    private static final int MAX_CHUNK_SIZE = 10000;
    private static final int port = 4444;
    private static final String ip = "230.44.44.44";
    private static String serviceName;
    private final AtomicBoolean serverVariable;
    private static MulticastSocket socket = null; //Gonna try to figure out how multicast works
    private static File localDirectory = new File("");
    private static int registryPort;
    private static final List<RmiClientInterface> clients = new ArrayList<>();
    public RMI(String newRegistry, int newRegistryPort, File databaseDirectory, AtomicBoolean newServerVariable) throws java.rmi.RemoteException, SocketException {
        serviceName = newRegistry;
        registryPort = newRegistryPort;
        localDirectory = databaseDirectory;
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
             ObjectOutputStream out = new ObjectOutputStream(buff);
             ResultSet rs = DatabaseManager.executeQuery("select versao from versao;")) {
            int version = rs.getInt("versao");
            out.writeObject(new RMIMulticastMessage(serviceName, registryPort, version));
            pkt = new DatagramPacket(buff.toByteArray(), buff.size(), InetAddress.getByName(ip), port);
            socket.send(pkt);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
    public static synchronized void updateServerBackupDatabases(int databaseVersion){
        byte fileChunk[] = new byte[MAX_CHUNK_SIZE];
        int nbytes;
        String filename = DatabaseManager.getDatabaseFileName();
        for (RmiClientInterface client : clients) {
            try{
                if(client.checkDatabaseVersion(databaseVersion))
                    try(FileInputStream requestedFileInputStream = getRequestedFileInputStream(filename)){
                        client.setFout();
                        while((nbytes = requestedFileInputStream.read(fileChunk))!=-1){
                            client.writeFileChunk(fileChunk, nbytes);
                        }
                    } catch (FileNotFoundException e) {
                        clients.remove(client);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                else
                    clients.remove(client);
            }
            catch (ConnectException | RemoteException e){
                clients.remove(client);
            }
            catch (IOException e){
                clients.remove(client);
            }
        }
    }
    @Override
    public void run() {
        do{ //THE HEARTBEAT WORKS
            try{
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendHeartbeat();
        }while(serverVariable.get());
        try {
            socket.close();
            Naming.unbind("rmi://localhost/" + serviceName);
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
        System.out.println("Closing RMI.");
    }
    private static FileInputStream getRequestedFileInputStream(String filename) throws IOException {
        String requestedCanonicalFilePath;

        requestedCanonicalFilePath = new File(localDirectory+File.separator+filename).getCanonicalPath();

        if(!requestedCanonicalFilePath.startsWith(localDirectory.getCanonicalPath()+File.separator)){
            System.out.println("Nao e' permitido aceder ao ficheiro " + requestedCanonicalFilePath + "!");
            System.out.println("A directoria de base nao corresponde a " + localDirectory.getCanonicalPath()+"!");
            throw new AccessDeniedException(filename);
        }
        return new FileInputStream(requestedCanonicalFilePath);
    }
    @Override
    public synchronized void getFile(RmiClientInterface remoteClientService) throws IOException, RemoteException {
        byte [] fileChunk = new byte[MAX_CHUNK_SIZE];
        int nbytes;
        String filename = DatabaseManager.getDatabaseFileName();
        try(FileInputStream requestedFileInputStream = getRequestedFileInputStream(filename)){
            while((nbytes = requestedFileInputStream.read(fileChunk))!=-1){
                remoteClientService.writeFileChunk(fileChunk, nbytes);
            }
        }catch(FileNotFoundException e){
            System.out.println("Ocorreu a excecao {" + e + "} ao tentar abrir o ficheiro!");
            throw new FileNotFoundException(filename);
        }catch(IOException e){
            System.out.println("Ocorreu a excecao de I/O: \n\t" + e);
            throw new IOException(filename, e.getCause());
        }
    }
    @Override
    public boolean registerToServer(RmiClientInterface clientInterface) throws IOException, RemoteException {
        return clients.add(clientInterface);
    }
}
