package BackupServer;

import Shared.RMI.RmiServerInterface;
import Shared.RMIMulticastMessage;

import java.io.*;
import java.net.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class BackupServer {
    private static final String databaseName = "backup.db";
    public static int currentDatabaseVersion = -1;
    public static void main(String args[]) throws IOException {
        if(args.length != 1){
            System.out.println("Sintaxe: java BackupServer localRootDirectory");
            return;
        }

        File localDirectory = new File(args[0].trim());

        if(!localDirectory.exists()){
            System.out.println("A directoria " + localDirectory + " nao existe!");
            return;
        }

        if(!localDirectory.isDirectory()){
            System.out.println("O caminho " + localDirectory + " nao se refere a uma diretoria!");
            return;
        }

        if(!localDirectory.canWrite()){
            System.out.println("Sem permissoes de escrita na diretoria " + localDirectory + "!");
            return;
        }

        try (DirectoryStream<Path> directory = Files.newDirectoryStream(localDirectory.toPath())) {
            if(directory.iterator().hasNext()){
                System.out.println("A diretoria nao esta vazia.");
                return;
            }
        }

        final int timeout = 30;
        final int port = 4444;
        final String ip = "230.44.44.44";
        Object obj;
        DatagramPacket pkt;
        RMIMulticastMessage msg;
        RmiClientService clientService;
        RmiServerInterface serverInterface = null;
        NetworkInterface nif;
        MulticastSocket s;

        try {
            nif = NetworkInterface.getByInetAddress(InetAddress.getByName(ip)); //e.g., 127.0.0.1, 192.168.10.1, ...
        } catch (SocketException | NullPointerException | UnknownHostException | SecurityException ex) {
            nif = NetworkInterface.getByName("eth0"); //e.g., lo, eth0, wlan0, en0, ...
        }

        try {
            s = new MulticastSocket(port);
            s.joinGroup(new InetSocketAddress(ip, port), nif);
            s.setSoTimeout(timeout * 1000);
        }
        catch(IOException e){
            System.out.println("Error while trying to connect the Socket to a Multicast Group.");
            return;
        }
        String localFilePath = new File(localDirectory.getPath()+File.separator+databaseName).getCanonicalPath();
        clientService = new RmiClientService(localFilePath);
        try {
            System.out.println("Socket successfully started.");
            while (true) {
                pkt = new DatagramPacket(new byte[1000], 1000);
                s.receive(pkt);
                try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(pkt.getData(), 0, pkt.getLength()))) {
                    obj = in.readObject();
                    if (obj instanceof RMIMulticastMessage) {
                        msg = (RMIMulticastMessage) obj;
                        if(msg.getDatabaseVersion() != currentDatabaseVersion){
                            if(serverInterface == null){
                                serverInterface = (RmiServerInterface) LocateRegistry.getRegistry(msg.getRegistryPort()).lookup(msg.getServiceName());
                                try{
                                    clientService.setFout();
                                    serverInterface.getFile(clientService);
                                    if(!serverInterface.registerToServer(clientService))
                                        throw new RemoteException();
                                    clientService.closeFout();
                                }
                                catch(RemoteException e){
                                    System.out.println("Error while connecting to RMI Service.");
                                    break;
                                }
                                catch(IOException e){
                                    System.out.println("Error while downloading file.");
                                    break;
                                }
                                currentDatabaseVersion = msg.getDatabaseVersion();
                                System.out.println("First time Setup Complete.");
                            }
                            else{
                                System.out.println("Database versions don't match, closing app.");
                                break;
                            }
                        }
                    }
                }
                catch(NotBoundException ignored){
                    System.out.println("Heartbeat received. Registry name invalid.");
                }
                catch (ClassNotFoundException e) {
                    System.out.println("Mensagem recebida de tipo inesperado! " + e);
                }
                catch (IOException e) {
                    System.out.println("Impossibilidade de aceder ao conteudo da mensagem recebida! " + e);
                }
                catch (Exception e) {
                    System.out.println("Excepcao: " + e);
                    break;
                }
            }
        }
        catch (SocketTimeoutException e){
            System.out.println("Socket Timed out, Server isn't online.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        s.close();
        System.exit(0);
    }
}
