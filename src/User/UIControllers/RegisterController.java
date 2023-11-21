package User.UIControllers;

import Shared.Register;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    private Stage stage;
    private Scene scene;
    private static Scene preScene;

    @FXML
    private PasswordField pass;
    @FXML
    private TextField user;
    @FXML
    private TextField nidentificacao;
    @FXML
    private TextField email;


    public void initialize() {}

    public void Register(ActionEvent event) throws IOException {
        if(pass.getText() == null || user.getText() == null || nidentificacao.getText() == null || email.getText() == null){
            return;
        }
        Register register = new Register(user.getText(),nidentificacao.getText(),email.getText(), pass.getText());
        boolean retorno = User.Client.setObjectRegister(register);

        if(retorno){
            User.Client.setUsername(register.getUsername());
            Parent root = FXMLLoader.load(getClass().getResource("resources/Client/beginClient.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        else{
            //errorMessage.setText("Dados incorretos!");
        }

    }

    public void backLogin(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("resources/Client/login.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
