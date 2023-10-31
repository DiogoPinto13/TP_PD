package Server;

import Server.RMI.RmiManager;
import Shared.ErrorMessages;
import Shared.Login;
import Shared.Register;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;

//wait for clients
class WaitClient extends Thread{
    private int port;
    public WaitClient(int port){
        this.port = port;
    }
    public void run(){

        try(ServerSocket socket = new ServerSocket(port)){
            System.out.println("Main server online in port: "+ socket.getLocalPort());

            while(true){

                try{
                    Socket toClientSocket = socket.accept();
                    ClientHandler clientHandler = new ClientHandler(toClientSocket);
                    clientHandler.start();
                }
                catch (Exception e){
                    System.out.println("Error while getting the client socket...");
                }
            }

        }catch (Exception e){
            System.out.println("Error while creating the ServerSocket...");
        }

    }
}
class ClientHandler extends Thread{
    private Socket clientSocket;
    private String Username;
    private Object receivedObject;

    public ClientHandler(Socket socket){
        clientSocket = socket;
    }

    public void run(){
        try(ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())){
            String response;
            do{
                receivedObject = in.readObject();
                if(receivedObject == null)
                    return;
                else if(receivedObject instanceof Login login) {
                    if(!UserManager.checkPassword(login)){
                        response = ErrorMessages.INVALID_PASSWORD.toString();
                    }
                    else{
                        response = "Welcome! " + login.getUsername();
                        Username = login.getUsername();
                    }
                }
                else if(receivedObject instanceof Register register) {
                    UserManager.registerUser(register);
                    if(!UserManager.userExists(register.getUsername())){
                        response = ErrorMessages.USERNAME_ALREADY_EXISTS.toString();
                    }
                    else{
                        response = "Welcome! " + register.getUsername();
                        Username = register.getUsername();
                    }
                }
                else{
                    response = ErrorMessages.INVALID_REQUEST.toString();
                }
                out.writeObject(response);
                out.flush();
            }while(!clientSocket.isClosed());
        } catch (IOException e) {
            System.out.println("error: IO" + e);
        } catch (ClassNotFoundException e) {
            System.out.println("error while reading/writing the object from/to the server");
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}


public class Main {

    public static void main(String[] args) {

        if(args.length != 1){
            System.out.println("Wrong syntax! java Main port");
            return;
        }
        RmiManager rmiManager;
        boolean serverVariable = true;
        try {
            rmiManager = new RmiManager("localhost", serverVariable);
            if(!rmiManager.registerService())
                throw new RemoteException();
            System.out.println("RMI Service is Online!");
        }catch (RemoteException e) {
           System.out.println("Error while creating the RMI manager: " + e);
           return;
        }

        //DatabaseManager.createNewDatabase();
        DatabaseManager.connect();
        //DatabaseManager.createNewTable();
        //DatabaseManager.clearDatabase();


        //dbManager.createNewDatabase();
        //dbManager.connect();
        //dbManager.createNewTable();

        WaitClient waitClient = new WaitClient(Integer.parseInt(args[0]));
        waitClient.start();

        System.out.println("Welcome!");
    }
}