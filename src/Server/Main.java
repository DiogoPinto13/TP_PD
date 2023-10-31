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
    private Login login;
    private Register register;
    private Object receivedObject;

    public ClientHandler(Socket socket){
        clientSocket = socket;
    }

    public void run(){
        try(ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())){

            //DatabaseManager.testUser();
            receivedObject = in.readObject();
            if(receivedObject == null)
                return;
            if(receivedObject instanceof Login) {
                login = (Login) receivedObject;
                if(!UserManager.checkPassword(login)){
                    out.writeObject(ErrorMessages.INVALID_PASSWORD.toString());
                    out.flush();
                }
            }
            if(receivedObject instanceof Register) {
                register = (Register) receivedObject;
                UserManager.registerUser(register);
                if(!UserManager.userExists(register.getUsername())){
                    out.writeObject(ErrorMessages.USERNAME_ALREADY_EXISTS.toString());
                    out.flush();
                }
            }
            String response = "Welcome! " + (login != null ? login.getUsername() : register.getUsername());
            out.writeObject(response);
            out.flush();


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