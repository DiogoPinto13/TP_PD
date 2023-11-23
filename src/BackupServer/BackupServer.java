package BackupServer;

import Shared.RMIMulticastMessage;

import java.io.*;
import java.net.*;

public class BackupServer {
    public static void main(String args[]) throws IOException {
        File localDirectory;
        if(args.length != 1){
            System.out.println("Sintaxe: java BackupServer localRootDirectory");
            return;
        }

        localDirectory = new File(args[0].trim());

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

        final int timeout = 30;
        final int port = 4444;
        final String ip = "230.44.44.44";
        Object obj;
        DatagramPacket pkt;
        RMIMulticastMessage msg;
        NetworkInterface nif;
        MulticastSocket s = null;

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
        System.out.println("Socket successfully started.");
        try {
            while (true) {
                pkt = new DatagramPacket(new byte[1000], 1000);
                s.receive(pkt);

                try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(pkt.getData(), 0, pkt.getLength()))) {

                    obj = in.readObject();

                    if (obj instanceof RMIMulticastMessage) {
                        msg = (RMIMulticastMessage) obj;
                        System.out.println("ServiceName: " + msg.getServiceName());
                        System.out.println("RegistryPort: " + msg.getRegistryPort());
                        System.out.println("DatabaseVersion: " + msg.getDatabaseVersion());
                    }
                } catch (ClassNotFoundException e) {
                    System.out.println();
                    System.out.println("Mensagem recebida de tipo inesperado! " + e);
                } catch (IOException e) {
                    System.out.println();
                    System.out.println("Impossibilidade de aceder ao conteudo da mensagem recebida! " + e);
                } catch (Exception e) {
                    System.out.println();
                    System.out.println("Excepcao: " + e);
                }
            }
        }
        catch (SocketTimeoutException e){
            System.out.println("Socket Timed out, Server isn't online.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
