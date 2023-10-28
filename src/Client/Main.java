package Client;

import Shared.ErrorMessages;
import Shared.Login;
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
    public  static final int TIMEOUT = 10; //segundos

    public static void main(String[] args) {

        if(args.length != 2){
            System.out.println("error in syntax: java Main IPadress port");
            return;
        }


        System.out.println("Welcome!");
        Scanner scanner = new Scanner(System.in);
        String[] parts;
        Login login = null;
        Register register = null;
        String response;

        //first menu
        String option;
        int opt;
        do{
            System.out.println("Please choose an option:");
            System.out.println("1 - register");
            System.out.println("2 - login");
            System.out.println("3 - exit");
            opt = Integer.parseInt(scanner.nextLine());
        }while(opt < 1 || opt > 3);
        //scanner.close();

        //register
        if(opt == 1){
            do{
                System.out.println("Please introduce the credentials: name,ID,emailAdress,password");
                String input = scanner.nextLine();
                parts = input.split(",");
                register = new Register(parts[0],parts[1], parts[2], parts[3]);
            }while(parts.length != 4);

            //scanner.close();
        }

        //login
        if(opt == 2){
            scanner = new Scanner(System.in);
            do{
                System.out.println("Please introduce the credentials: username,password:");
                String input = scanner.nextLine();
                parts = input.split(",");
                login = new Login(parts[0], parts[1]);
            }while(parts.length != 2);

            //scanner.close();
        }

        try(Socket socket = new Socket(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            socket.setSoTimeout(TIMEOUT*1000);

            if(opt == 1){
                out.writeObject(register);
                out.flush();
            }else if(opt == 2) {
                out.writeObject(login);
                out.flush();
            }else{
                return;
            }

            do{
                response = (String) in.readObject();
                if(response == null){
                    return;
                }
                System.out.println(response);
                //temos que reenviar ao servidor as coisas que nao estavam previamente corretas
                if(response.equals(ErrorMessages.INVALID_PASSWORD.toString())){
                    System.out.println("Introduce a new valid password: ");
                    String input = scanner.nextLine();
                    if (register != null){
                        register.setPassword(input);
                        out.writeObject(register);
                        out.flush();
                    }
                } else if(response.equals(ErrorMessages.USERNAME_ALREADY_EXISTS.toString())){
                    System.out.println("the email address already exists");
                    String input = scanner.nextLine();
                    if(register != null) {
                        register.setUsername(input);
                        out.writeObject(register);
                        out.flush();
                    }
                }
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


    }
}