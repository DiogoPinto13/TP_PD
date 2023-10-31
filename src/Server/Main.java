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
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

//wait for clients
class WaitClient extends Thread{
    private final int port;
    private final AtomicBoolean serverVariable;
    public WaitClient(int port, AtomicBoolean newServerVariable){
        this.port = port;
        serverVariable = newServerVariable;
    }
    public void run(){

        try(ServerSocket socket = new ServerSocket(port)){
            System.out.println("Main server online in port: "+ socket.getLocalPort());

            while(serverVariable.get()){
                try{
                    Socket toClientSocket = socket.accept();
                    ClientHandler clientHandler = new ClientHandler(toClientSocket, serverVariable);
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
    private final Socket clientSocket;
    private String Username;
    private Object receivedObject;
    private final AtomicBoolean serverVariable;

    public ClientHandler(Socket socket, AtomicBoolean newServerVariable){
        clientSocket = socket;
        serverVariable = newServerVariable;
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
                        Username = login.getUsername();
                        response = "Welcome! " + Username;
                    }
                }
                else if(receivedObject instanceof Register register) {
                    UserManager.registerUser(register);
                    if(!UserManager.userExists(register.getUsername())){
                        response = ErrorMessages.USERNAME_ALREADY_EXISTS.toString();
                    }
                    else{
                        Username = register.getUsername();
                        response = "Welcome! " + Username;
                    }
                }
                else if(receivedObject instanceof String request){
                    //Takes normal string as request, will turn this into enum
                    switch (request){
                        case "Close":
                            clientSocket.close();
                            return;
                        default:
                            response = "Unknown Command.";
                            break;
                    }
                }
                else{
                    response = ErrorMessages.INVALID_REQUEST.toString();
                }
                /*if(!serverVariable.get()){
                    response = close request
                }*/
                out.writeObject(response);
                out.flush();
            }while(!clientSocket.isClosed() && serverVariable.get());
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
        AtomicBoolean serverVariable = new AtomicBoolean(true);
        try {
            rmiManager = new RmiManager("RMIService", serverVariable);
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

        WaitClient waitClient = new WaitClient(Integer.parseInt(args[0]), serverVariable);
        waitClient.start();

        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.println("Welcome!");
        //handles admin commands here
        do{
            input = scanner.next();
        }while(serverVariable.get());
    }
}