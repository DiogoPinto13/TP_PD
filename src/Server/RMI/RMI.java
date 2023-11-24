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
import java.util.concurrent.atomic.AtomicBoolean;

public class RMI extends UnicastRemoteObject implements RmiServerInterface, Runnable {
    //variables
    private static final int MAX_CHUNCK_SIZE = 10000;
    private static final int port = 4444;
    private static final String ip = "230.44.44.44";
    private static String serviceName;
    private final AtomicBoolean serverVariable;
    private static MulticastSocket socket = null; //Gonna try to figure out how multicast works
    private final File localDirectory;

    public RMI(String newRegistry, File databaseDirectory, AtomicBoolean newServerVariable) throws java.rmi.RemoteException, SocketException {
        serviceName = newRegistry;
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


            out.writeObject(new RMIMulticastMessage(serviceName, port, version));
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
        try {
            Naming.unbind("rmi://localhost/" + serviceName);
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
        System.out.println("Closing RMI.");
    }

    protected FileInputStream getRequestedFileInputStream(String fileName) throws IOException {
        String requestedCanonicalFilePath;

        fileName = fileName.trim();

        /*
         * Verifica se o ficheiro solicitado existe e encontra-se por baixo da localDirectory.
         */

        requestedCanonicalFilePath = new File(localDirectory+File.separator+fileName).getCanonicalPath();

        if(!requestedCanonicalFilePath.startsWith(localDirectory.getCanonicalPath()+File.separator)){
            System.out.println("Nao e' permitido aceder ao ficheiro " + requestedCanonicalFilePath + "!");
            System.out.println("A directoria de base nao corresponde a " + localDirectory.getCanonicalPath()+"!");
            throw new AccessDeniedException(fileName);
        }

        /*
         * Abre o ficheiro solicitado para leitura.
         */
        return new FileInputStream(requestedCanonicalFilePath);

    }

    @Override
    public void getFile(String fileName, RmiClientInterface cliRemoto) throws IOException, RemoteException {
        byte [] fileChunk = new byte[MAX_CHUNCK_SIZE];
        int nbytes;

        fileName = fileName.trim();
        System.out.println("Recebido pedido para: " + fileName);

        try(FileInputStream requestedFileInputStream = getRequestedFileInputStream(fileName)){

            /*
             * Obtem os bytes do ficheiro por blocos de bytes ("file chunks").
             */
            while((nbytes = requestedFileInputStream.read(fileChunk))!=-1){

                /*
                 * Escreve o bloco actual no cliente, invocando o metodo writeFileChunk da
                 * sua interface remota.
                 */
                cliRemoto.writeFileChunk(fileChunk, nbytes);
                /*...*/

            }

            System.out.println("Ficheiro " + new File(localDirectory+File.separator+fileName).getCanonicalPath() +
                    " transferido para o cliente com sucesso.");
            System.out.println();

            return;

        }catch(FileNotFoundException e){   //Subclasse de IOException
            System.out.println("Ocorreu a excecao {" + e + "} ao tentar abrir o ficheiro!");
            throw new FileNotFoundException(fileName);
        }catch(IOException e){
            System.out.println("Ocorreu a excecao de E/S: \n\t" + e);
            throw new IOException(fileName, e.getCause());
        }
    }
}
