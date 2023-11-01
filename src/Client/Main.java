package Client;

import Shared.ErrorMessages;
import Shared.Login;
import Shared.Messages;
import Shared.Register;

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
    }

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

        socket = loginRegister(scanner, args[0], args[1]);

        if(socket == null)
            return;
        try(ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())){
            do{
                //After login menu
                //get a function to print menu and get option
                input = scanner.next();
                switch (input){
                    case "exit":
                        oos.writeObject(Messages.CLOSE.toString());
                        socket.close();
                        break;
                    default:
                        System.out.println(Messages.UNKNOWN_COMMAND.toString());
                        break;
                }
            }while(!socket.isClosed());
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}