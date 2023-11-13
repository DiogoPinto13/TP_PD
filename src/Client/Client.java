package Client;

import Shared.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private static Object newObject;
    public static void setObject(Object object){
        Client.newObject = object;
        handle();
    }

    public static boolean setObjectLogin(Object object){
        Client.newObject = object;
        return handleLogin();
    }
    public static boolean setObjectRegister(Object object){
        Client.newObject = object;
        return handleLogin();
    }

    public static String getUsername(){
        return username;
    }
    public static void setUsername(String username){
        Client.username = username;
    }

    private static String username;
    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public static void prepareClient(String ... args){
        try {
            socket = new Socket(args[0], Integer.parseInt(args[1]));
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean handleLogin(){
        String response;
        String input;
        boolean flag;
        try {
            out.writeObject(newObject);
            response = (String) in.readObject();
            if(response.equals(ErrorMessages.INVALID_PASSWORD.toString())){
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }
    public static boolean handleRegister(){
        String response;
        String input;
        boolean flag;
        try {
            out.writeObject(newObject);
            response = (String) in.readObject();
            if(response.equals(ErrorMessages.USERNAME_ALREADY_EXISTS.toString())){
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String getProfileData(String input){
        Request request = new Request(Messages.REQUEST_EDIT_PROFILE, input);
        try {
            out.writeObject(request);
            return (String) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ErrorMessages.SQL_ERROR.toString();
    }
    public static boolean editProfile(String input){
        Request request = new Request(Messages.EDIT_PROFILE, input);
        try {
            out.writeObject(request);
            String response = (String) in.readObject();
            if(response.equals(Messages.EDIT_PROFILE_ERROR.toString())){
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    /*public static void handleMenuAfter(){
        try{
            do{
                //After login menu
                //get a function to print menu and get option
                System.out.println("Please choose an option:");
                System.out.println("1 - exit");
                System.out.println("2 - edit profile");
                System.out.println("3 - register presence code");
                System.out.println("4 - get all presences");
                System.out.println("5 - get all presences in a csv file");
                int inputMenu = scanner.nextInt();
                Request request;
                //muitos destes tem null na message mas será mudado à medida que formos implementando os outros requests.
                switch (inputMenu){
                    case 1:
                        request = new Request(Messages.CLOSE, null);
                        out.writeObject(request);
                        //socket.close();
                        break;
                    case 2:
                        System.out.println("Choose an option: ");
                        System.out.println("1 - Change the name: ");
                        System.out.println("2 - Change the password: ");
                        int inputOp = scanner.nextInt();
                        System.out.println("Please type the new name/password: ");
                        String inputOpt =scanner.nextLine();
                        request = new Request(Messages.EDIT_PROFILE, Integer.toString(inputOp) + "," + inputOpt);
                        out.writeObject(request);
                        //socket.close();
                        break;
                    case 3:
                        System.out.println("Please type the presence code: ");
                        input = scanner.nextLine();
                        request = new Request(Messages.REGISTER_PRESENCE_CODE, input);
                        out.writeObject(request);
                        //socket.close();
                        break;
                    case 4:
                        request = new Request(Messages.GET_PRESENCES, null);
                        out.writeObject(request);
                        //socket.close();
                        break;
                    case 5:
                        request = new Request(Messages.GET_CSV_PRESENCES, null);
                        out.writeObject(request);
                        //socket.close();
                        break;
                    default:
                        System.out.println(Messages.UNKNOWN_COMMAND.toString());
                }

            }while(!socket.isClosed());
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }*/

    public static void handle(){
        Scanner scanner = new Scanner(System.in);
        String response;
        String input;

        try {
            boolean flag;
            //socket.setSoTimeout(TIMEOUT*1000);
            out.writeObject(newObject);
            do{
                flag = false;
                response = (String) in.readObject();
                if(response == null){
                    return;
                }
                System.out.println(response);
                //temos que reenviar ao servidor as coisas que nao estavam previamente corretas
                if(response.equals(ErrorMessages.INVALID_PASSWORD.toString())){
                    flag = true;
                    System.out.println("Introduce a new valid password: ");
                    input = scanner.nextLine();
                    if(newObject instanceof Login login){
                        login.setPassword(input);
                    }
                    else{
                        socket.close();
                        return;
                    }
                    out.writeObject(newObject);
                    out.flush();
                } else if(response.equals(ErrorMessages.USERNAME_ALREADY_EXISTS.toString())){
                    flag = true;
                    System.out.println("the email address already exists");
                    input = scanner.nextLine();
                    if(newObject instanceof Register register){
                        register.setUsername(input);
                    }
                    else{
                        socket.close();
                        return;
                    }
                    out.writeObject(newObject);
                    out.flush();
                }
            }while(flag);
        } catch (UnknownHostException e) {
            System.out.println("error: unknown host");
        } catch (SocketTimeoutException e){
            System.out.println("error: the client didn't send anything to the server (timeout)");
        } catch (IOException e) {
            System.out.println("error: IO" + e);
        } catch (ClassNotFoundException e) {
            System.out.println("error while reading/writing the object from/to the server");
        }


        //a partir daqui vai pra outra funcao
        if(socket == null)
            return;
        try{
            do{
                //After login menu
                //get a function to print menu and get option
                System.out.println("Please choose an option:");
                System.out.println("1 - exit");
                System.out.println("2 - edit profile");
                System.out.println("3 - register presence code");
                System.out.println("4 - get all presences");
                System.out.println("5 - get all presences in a csv file");
                int inputMenu = scanner.nextInt();
                Request request;
                //muitos destes tem null na message mas será mudado à medida que formos implementando os outros requests.
                switch (inputMenu){
                    case 1:
                        request = new Request(Messages.CLOSE, null);
                        out.writeObject(request);
                        //socket.close();
                        break;
                    case 2:
                        System.out.println("Choose an option: ");
                        System.out.println("1 - Change the name: ");
                        System.out.println("2 - Change the password: ");
                        int inputOp = scanner.nextInt();
                        System.out.println("Please type the new name/password: ");
                        String inputOpt =scanner.nextLine();
                        request = new Request(Messages.EDIT_PROFILE, Integer.toString(inputOp) + "," + inputOpt);
                        out.writeObject(request);
                        //socket.close();
                        break;
                    case 3:
                        System.out.println("Please type the presence code: ");
                        input = scanner.nextLine();
                        request = new Request(Messages.REGISTER_PRESENCE_CODE, input);
                        out.writeObject(request);
                        //socket.close();
                        break;
                    case 4:
                        request = new Request(Messages.GET_PRESENCES, null);
                        out.writeObject(request);
                        //socket.close();
                        break;
                    case 5:
                        request = new Request(Messages.GET_CSV_PRESENCES, null);
                        out.writeObject(request);
                        //socket.close();
                        break;
                    default:
                        System.out.println(Messages.UNKNOWN_COMMAND.toString());
                }

            }while(!socket.isClosed());
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
