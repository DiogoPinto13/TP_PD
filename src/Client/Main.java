package Client;

import Shared.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
    public static final int TIMEOUT = 10; //segundos

    /*
    public static Socket loginRegister(Scanner scanner, String ... args){
        int opt;
        String[] parts;
        Object newObject = null;
        String response;
        do{
            System.out.println("Please choose an option:");
            System.out.println("1 - register");
            System.out.println("2 - login");
            System.out.println("3 - exit");
            opt = Integer.parseInt(scanner.nextLine());
        }while(opt < 1 || opt > 3);
        switch(opt){
            case 1:
                do{
                    System.out.println("Please introduce the credentials: name,ID,emailAddress,password");
                    String input = scanner.nextLine();
                    parts = input.split(",");
                    if(parts.length == 4){
                        newObject = new Register(parts[0],parts[1], parts[2], parts[3]);
                    }
                }while(parts.length != 4);
                break;
            case 2:
                do{
                    System.out.println("Please introduce the credentials: username,password:");
                    String input = scanner.nextLine();
                    parts = input.split(",");
                    if(parts.length == 2){
                        newObject = new Login(parts[0],parts[1]);
                    }
                }while(parts.length != 2);
                break;
            default:
                return null;
        }
        try {
            Socket socket = new Socket(InetAddress.getByName(args[0]), Integer.parseInt(args[1])); //these things cant be here
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            //socket.setSoTimeout(TIMEOUT*1000);
            out.writeObject(newObject);
            do{
                response = (String) in.readObject();
                if(response == null){
                    return null;
                }
                System.out.println(response);
                //temos que reenviar ao servidor as coisas que nao estavam previamente corretas
                if(response.equals(ErrorMessages.INVALID_PASSWORD.toString())){
                    System.out.println("Introduce a new valid password: ");
                    String input = scanner.nextLine();
                    if(newObject instanceof Register register){
                        register.setPassword(input);
                    }
                    else if(newObject instanceof Login login){
                        login.setPassword(input);
                    }
                    else{
                        socket.close();
                        return null;
                    }
                } else if(response.equals(ErrorMessages.USERNAME_ALREADY_EXISTS.toString())){
                    System.out.println("the email address already exists");
                    String input = scanner.nextLine();
                    if(newObject instanceof Register register){
                        register.setUsername(input);
                    }
                    else if(newObject instanceof Login login){
                        login.setUsername(input);
                    }
                    else{
                        socket.close();
                        return null;
                    }
                }
                out.writeObject(newObject);
                out.flush();
            }while(response.equals(ErrorMessages.INVALID_PASSWORD.toString()) || response.equals(ErrorMessages.USERNAME_ALREADY_EXISTS.toString()));
            return socket;
        } catch (UnknownHostException e) {
            System.out.println("error: unknown host");
        } catch (SocketTimeoutException e){
            System.out.println("error: the client didn't send anything to the server (timeout)");
        } catch (IOException e) {
            System.out.println("error: IO" + e);
        } catch (ClassNotFoundException e) {
            System.out.println("error while reading/writing the object from/to the server");
        }
        return null;
    }*/

    public static void main(String[] args) {

        if(args.length != 2){
            System.out.println("error in syntax: java Main IPaddress port");
            return;
        }

        System.out.println("Welcome!");
        Scanner scanner = new Scanner(System.in);
        Socket socket = null;
        String response;
        String input;

        //socket = loginRegister(scanner, args[0], args[1]);
        int opt;
        String[] parts;
        Object newObject = null;
        do{
            System.out.println("Please choose an option:");
            System.out.println("1 - register");
            System.out.println("2 - login");
            System.out.println("3 - exit");
            opt = Integer.parseInt(scanner.nextLine());
        }while(opt < 1 || opt > 3);
        switch(opt){
            case 1:
                do{
                    System.out.println("Please introduce the credentials: name,ID,emailAddress,password");
                    input = scanner.nextLine();
                    parts = input.split(",");
                    if(parts.length == 4){
                        newObject = new Register(parts[0],parts[1], parts[2], parts[3]);
                    }
                }while(parts.length != 4);
                break;
            case 2:
                do{
                    System.out.println("Please introduce the credentials: username,password:");
                    input = scanner.nextLine();
                    parts = input.split(",");
                    if(parts.length == 2){
                        newObject = new Login(parts[0],parts[1]);
                    }
                }while(parts.length != 2);
                break;
            default:
                return;
        }
        try {
            socket = new Socket(InetAddress.getByName(args[0]), Integer.parseInt(args[1])); //these things cant be here
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            //socket.setSoTimeout(TIMEOUT*1000);
            out.writeObject(newObject);
            do{
                response = (String) in.readObject();
                if(response == null){
                    return;
                }
                System.out.println(response);
                //temos que reenviar ao servidor as coisas que nao estavam previamente corretas
                if(response.equals(ErrorMessages.INVALID_PASSWORD.toString())){
                    System.out.println("Introduce a new valid password: ");
                    input = scanner.nextLine();
                    if(newObject instanceof Register register){
                        register.setPassword(input);
                    }
                    else if(newObject instanceof Login login){
                        login.setPassword(input);
                    }
                    else{
                        socket.close();
                        return;
                    }
                } else if(response.equals(ErrorMessages.USERNAME_ALREADY_EXISTS.toString())){
                    System.out.println("the email address already exists");
                    input = scanner.nextLine();
                    if(newObject instanceof Register register){
                        register.setUsername(input);
                    }
                    else if(newObject instanceof Login login){
                        login.setUsername(input);
                    }
                    else{
                        socket.close();
                        return;
                    }
                }
                out.writeObject(newObject);
                out.flush();
            }while(response.equals(ErrorMessages.INVALID_PASSWORD.toString()) || response.equals(ErrorMessages.USERNAME_ALREADY_EXISTS.toString()));
        } catch (UnknownHostException e) {
            System.out.println("error: unknown host");
        } catch (SocketTimeoutException e){
            System.out.println("error: the client didn't send anything to the server (timeout)");
        } catch (IOException e) {
            System.out.println("error: IO" + e);
        } catch (ClassNotFoundException e) {
            System.out.println("error while reading/writing the object from/to the server");
        }


        if(socket == null)
            return;
        try(ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())){
            do{
                //After login menu
                //get a function to print menu and get option
                System.out.println("Please choose an option:");
                System.out.println("1 - exit");
                System.out.println("2 - edit profile");
                System.out.println("3 - register presence code");
                System.out.println("4 - get all presences");
                System.out.println("5 - get all presences in a csv file");
                input = scanner.next();
                //muitos destes tem null na message mas será mudado à medida que formos implementando os outros requests.
                if(input.equals("1")){
                    Request request = new Request(Messages.CLOSE, null);
                    oos.writeObject(request);
                    socket.close();
                }else if(input.equals("2")){
                    Request request = new Request(Messages.EDIT_PROFILE, null);
                    oos.writeObject(request);
                    socket.close();
                }else if(input.equals("3")){
                    System.out.println("Please type the presence code: ");
                    input = scanner.nextLine();
                    Request request = new Request(Messages.REGISTER_PRESENCE_CODE, input);
                    oos.writeObject(request);
                    socket.close();
                }
                else if(input.equals("4")){
                    Request request = new Request(Messages.GET_PRESENCES, null);
                    oos.writeObject(request);
                    socket.close();
                }
                else if(input.equals("5")){
                    Request request = new Request(Messages.GET_CSV_PRESENCES, null);
                    oos.writeObject(request);
                    socket.close();
                }
                else{
                    System.out.println(Messages.UNKNOWN_COMMAND.toString());
                }

            }while(!socket.isClosed());
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}