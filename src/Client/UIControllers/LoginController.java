package Client.UIControllers;

import Client.Main;
import Shared.Login;
import Shared.Register;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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


    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void OpenRegisto(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resources/registo.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public void Login(ActionEvent event) throws IOException{

        if(user.getText()==null || pass.getText()==null)
            return;

        Login login = new Login(user.getText(),pass.getText());
        boolean retorno = Client.Client.setObjectLogin(login);

        if(retorno){
            Client.Client.setUsername(login.getUsername());
            Parent root = FXMLLoader.load(getClass().getResource("resources/beginClient.fxml"));
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
