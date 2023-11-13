package Server;

import Server.RMI.RmiManager;
import Shared.*;

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
            String response = null;
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
                else if(receivedObject instanceof Request request){
                    //Takes normal string as request, will turn this into enum
                    switch(request.getTypeMessage()){
                        case CLOSE:
                            break;
                        case REQUEST_EDIT_PROFILE:
                            response = UserManager.getProfileForEdition(Username);
                            break;
                        case EDIT_PROFILE:
                            String[] messages = request.getMessage().split(",");
                            response = (UserManager.editProfile(Username, messages[0], messages[1], messages[2])) ? Messages.EDIT_PROFILE_SUCCESS.toString() : Messages.EDIT_PROFILE_ERROR.toString();
                            //response = (messages[1].equals("1") ?  UserManager.changeName(Username, messages[1]) : UserManager.changePassword(Username, messages[1])) ? "Successfully changed!" : "an error occurred";
                            break;
                        case REGISTER_PRESENCE_CODE:
                            response = (EventManager.registerUserInEvent(Username, Integer.parseInt(request.getMessage())) ? "successfully registered!" : "invalid code!");
                            break;
                        case GET_PRESENCES:
                            response = EventManager.queryEvents(Username, null);
                            //EventManager.queryEvents(username, null);
                            break;
                        case GET_CSV_PRESENCES:
                            //EventManager.queryToCSV(username, null);
                            //fuck temos que enviar pro client o ficheiro csv!
                            break;
                        default:
                            response = Messages.UNKNOWN_COMMAND.toString();
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

        //DatabaseManager.clearDatabase();
        //DatabaseManager.createNewDatabase();
        //DatabaseManager.createNewTable();
        // DatabaseManager.clearDatabase();
        //DatabaseManager.fillDatabase();
        DatabaseManager.connect();


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
            System.out.println("Please choose an option: ");
            System.out.println("1 - Create an event");
            System.out.println("2 - Edit an event");
            System.out.println("3 - Delete an event");
            System.out.println("4 - Query an event");
            System.out.println("5 - Generate a new presence code");
            System.out.println("6 - Query presences");
            System.out.println("7 - Delete a presence from a user");
            System.out.println("8 - Insert a presence manually");
            System.out.println("9 - Logout");
            int inputMenu = scanner.nextInt();
            Event event;
            switch (inputMenu) {
                case 1:
                    System.out.println("Introduce the following data: designation,place,date,time,presenceCode");
                    //event = new Event();
                    //EventManager.createEvent(event);
                    break;
                case 2:

                    break;
                case 3:

                    break;
                case 4:

                    break;
                case 5:

                    break;
                case 6:

                    break;
                case 7:

                    break;
                case 8:

                    break;
                case 9:

                    break;
                default:
                    System.out.println(Messages.UNKNOWN_COMMAND.toString());
                    break;
            }
        }while(serverVariable.get());
    }
}