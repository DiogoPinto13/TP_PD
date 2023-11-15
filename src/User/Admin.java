package User;

import Shared.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Admin {

    public static String getUsername(){
        return username;
    }
    public static void setUsername(String username){
        Admin.username = username;
    }

    private static String username;
    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public static void prepareAdmin(InetAddress adress, int port){
        try {
            socket = new Socket(adress, port);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String createEvent(String designation, String place, Time timeBeggining, Time timeEnding){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(designation).append(",").append(place).append(",").append(timeBeggining.toString()).append(",").append(timeEnding.toString()).append(",");
        Request request = new Request(Messages.CREATE_EVENT, stringBuilder.toString());
        try {
            out.writeObject(request);
            return (String) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ErrorMessages.SQL_ERROR.toString();
    }
    public static String editEvent(){

        return ErrorMessages.SQL_ERROR.toString();
    }
    public static String deleteEvent(){

        return ErrorMessages.SQL_ERROR.toString();
    }
    public static EventResult getEvents(){


        return null;
    }
    public static String generatePresenceCode(String code){

        return ErrorMessages.SQL_ERROR.toString();
    }
    public static EventResult queryEvents(){


        return null;
    }
    public static EventResult queryEventsToCSV(){

        return null;
    }
    public static String deletePresences(String eventDesignation, String clientName){

        return ErrorMessages.SQL_ERROR.toString();
    }
    public static String registerPresence(String eventDesignation, String clientName){


        return ErrorMessages.SQL_ERROR.toString();
    }

}
