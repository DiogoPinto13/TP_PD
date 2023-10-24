package Server;

import Shared.Login;
import Shared.Register;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
    private String receivedOption;

    public ClientHandler(Socket socket){
        clientSocket = socket;
    }

    public void run(){
        try(ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())){

            receivedOption = (String) (in.readObject());

            if(receivedOption.equals("Login")) {
                login = (Login) (in.readObject());
                if(login == null){
                    return;
                }
            }
            if(receivedOption.equals("Register")) {
                register = (Register) (in.readObject());
                if(register == null){
                    return;
                }
            }
            /*
            temos que voltar o campo que nao esta bem do lado do client e obviamente fzr as funcoes pra isto
            also temos que meter as classes que temos iguais em ambos os lados inclusive o enum com as error messages
            num package tipo shared que ambos os projetos conseguem ter acesso
            if(alreadyExists(register.username){
                String response = ErrorMessages.USERNAME_ALREADY_EXISTS.toString();
                out.writeObject(response);
                out.flush();
            }
            if(passwordValid(register.username){
                String response = ErrorMessages.INVALID_PASSWORD.toString();
                out.writeObject(response);
                out.flush();
            }
             */
            String response = "Welcome! " + (login != null ? login.getUsername() : register.getUsername());
            out.writeObject(response);
            out.flush();


        } catch (IOException e) {
            System.out.println("error: IO" + e);
        } catch (ClassNotFoundException e) {
            System.out.println("error while reading/writing the object from/to the server");
        }
        finally {
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

        DatabaseManager.createNewDatabase();
        DatabaseManager.connect();
        DatabaseManager.createNewTable();
        //DatabaseManager.ClearDatabase();
        WaitClient waitClient = new WaitClient(Integer.parseInt(args[0]));
        waitClient.start();

        System.out.println("Welcome!");
    }
}