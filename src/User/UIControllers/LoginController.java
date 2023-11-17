package User.UIControllers;

import Shared.ErrorMessages;
import Shared.Login;
import User.Admin;
import User.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;

public class LoginController {
    @FXML
    private TextField user;
    @FXML
    private PasswordField pass;
    @FXML
    private Label errorMessage;
    @FXML
    private Pane background;

    private Stage stage;
    private Scene scene;
    private static Scene preScene;

    public void initialize() {

        background.setStyle("-fx-background-image: url('resources/background.png'); " +
                "-fx-background-size: cover;");

        Client.prepareClient(Client.adress, String.valueOf(Client.port));
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void OpenRegisto(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resources/Client/registo.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public void Login(ActionEvent event) throws IOException{

        if(user.getText()==null || pass.getText()==null)
            return;

        Login login = new Login(user.getText(),pass.getText());
        String retorno = User.Client.setObjectLogin(login);

        if(retorno.equals(ErrorMessages.LOGIN_NORMAL_USER.toString())){
            User.Client.setUsername(login.getUsername());
            Parent root = FXMLLoader.load(getClass().getResource("resources/Client/beginClient.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else if(retorno.equals(ErrorMessages.LOGIN_ADMIN_USER.toString())){
            //vamos fechar a connection como client normal
            InetAddress host = Client.getSocket().getLocalAddress();
            int port = Client.getSocket().getLocalPort();
            Client.closeConnection();

            //vamos abrir uma connection especial pro admin
            Admin.prepareAdmin(host, port);
            Parent root = FXMLLoader.load(getClass().getResource("resources/Admin/beginAdmin.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        else{
            errorMessage.setText("Dados incorretos!");
        }

    }
}
