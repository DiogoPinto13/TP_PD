package Server;

import Server.RMI.RmiManager;
import Shared.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
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
        /*try {
            clientSocket.setSoTimeout(10*1000);
        } catch (SocketException e) {
            e.printStackTrace();
        }*/
        try(ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())){
            String response = null;
            boolean flagProtection = false;
            do{
                receivedObject = in.readObject();
                if(receivedObject == null)
                    return;
                else if(receivedObject instanceof Login login) {
                    response = UserManager.checkPassword(login).toString();
                    //System.out.println(response.toString());
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
                            response = UserManager.getProfileForEdition(request.getMessage());
                            break;
                        case EDIT_PROFILE:
                            String[] messages = request.getMessage().split(",");
                            response = (UserManager.editProfile(messages[0], messages[1], messages[2], messages[3])) ? Messages.EDIT_PROFILE_SUCCESS.toString() : Messages.EDIT_PROFILE_ERROR.toString();
                            break;
                        case REGISTER_PRESENCE_CODE:
                            String[] args = request.getMessage().split(",");
                            response = (EventManager.registerUserInEvent(args[1], Integer.parseInt(args[0])) ? Messages.PRESENCE_CODE_REGISTED.toString() : Messages.INVALID_PRESENCE_CODE.toString());
                            break;
                        case GET_PRESENCES:
                            //response = EventManager.queryEvents(Username, null);
                            flagProtection = true;
                            ArrayList<Integer> idsUser = EventManager.getIdsEventsByUsername(request.getMessage());
                            String filter = null;
                            EventResult eventResult = new EventResult(" ");
                            eventResult.setColumns(" ");
                            if(idsUser.size() == 0){
                                out.writeObject(eventResult);
                                out.flush();
                                break;
                            }
                            filter = EventManager.createFilterOr(idsUser);
                            out.writeObject(EventManager.queryEvents(request.getMessage(), filter));
                            out.flush();
                            //EventManager.queryEvents(username, null);
                            break;
                        case GET_CSV_PRESENCES:
                            //EventManager.queryToCSV(username, null);
                            break;
                        //admin commands here:
                        case CREATE_EVENT:
                            String[] arguments = request.getMessage().split(",");
                            //String[] argumentsTimeBegin = arguments[2].split(":");
                            //String[] argumentsTimeEnd = arguments[3].split(":");
                            Time timeBegin = new Time(arguments[2]);
                            Time timeEnd = new Time(arguments[3]);
                            //Time timeBegin = new Time(Integer.parseInt(argumentsTimeBegin[0]), Integer.parseInt(argumentsTimeBegin[1]), Integer.parseInt(argumentsTimeBegin[2]), Integer.parseInt(argumentsTimeBegin[3]), Integer.parseInt(argumentsTimeBegin[4]));
                            //Time timeEnd = new Time(Integer.parseInt(argumentsTimeEnd[0]), Integer.parseInt(argumentsTimeEnd[1]), Integer.parseInt(argumentsTimeEnd[2]), Integer.parseInt(argumentsTimeEnd[3]), Integer.parseInt(argumentsTimeEnd[4]));

                            Event event = new Event(arguments[0], arguments[1], timeBegin, timeEnd);
                            response = (EventManager.createEvent(event)) ? Messages.OK.toString() : ErrorMessages.CREATE_EVENT_FAILED.toString();
                            break;
                        case DELETE_EVENT:
                            response = (EventManager.deleteEvent(request.getMessage())) ? Messages.OK.toString() : ErrorMessages.INVALID_EVENT_NAME.toString();
                            break;
                        case GET_EVENTS:
                            flagProtection = true;
                            EventResult eventResult1 = new EventResult(" ");
                            eventResult1.setColumns(" ");

                            out.writeObject(EventManager.queryEvents(null, null));
                            out.flush();
                            break;
                        case GENERATE_PRESENCE_CODE:
                            String[] argsPresence = request.getMessage().split(",");
                            String[] times = EventManager.getTime(argsPresence[0]).split(",");
                            Time timeBeginEvent = new Time(times[0]);
                            Time timeEndEvent = new Time(times[1]);
                            Time timeAtual = new Time();
                            Event event1 = new Event(argsPresence[0],argsPresence[1],timeBeginEvent,timeEndEvent);

                            if(!EventManager.checkIfCodeAlreadyCreated(argsPresence[0]))
                                response = EventManager.registerPresenceCode(event1, Integer.parseInt(argsPresence[1]), timeAtual);
                            else{
                                int code = EventManager.generateCode();
                                response = EventManager.updatePresenceCode(code, Integer.parseInt(argsPresence[1]),argsPresence[0]) ? ErrorMessages.FAIL_REGISTER_PRESENCE_CODE.toString() : String.valueOf(code) ;
                            }
                            break;
                        case UPDATE_PRESENCE_CODE:

                            break;
                        case QUERY_EVENTS:

                            break;
                        case DELETE_PRESENCES:

                            break;
                        case INSERT_PRESENCES:

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
                if(!flagProtection){
                    out.writeObject(response);
                    out.flush();
                }
                flagProtection = false;
            }while(!clientSocket.isClosed() && serverVariable.get());
        } catch (IOException e) {
            System.out.println("error: IO" + e);
        } catch (ClassNotFoundException e) {
            System.out.println("error while reading/writing the object from/to the server");
        } catch (ParseException e) {
            e.printStackTrace();
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
        } catch (SocketException e) {
            System.out.println("Error while connecting socket to Multicast Group." + e);
            return;
        }

        //DatabaseManager.clearDatabase();
        //DatabaseManager.createNewDatabase();
        //DatabaseManager.createNewTable();
        // DatabaseManager.clearDatabase();
        //DatabaseManager.fillDatabase();
        DatabaseManager.connect();
        //DatabaseManager.testUser();

        //dbManager.createNewDatabase();
        //dbManager.connect();
        //dbManager.createNewTable();

        WaitClient waitClient = new WaitClient(Integer.parseInt(args[0]), serverVariable);
        waitClient.start();

        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.println("Welcome!");
    }
}