package Client.UIControllers;

import Shared.Login;
import Shared.Register;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

    private Stage stage;
    private Scene scene;
    private static Scene preScene;

    public void initialize() {}

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


    public void Login(ActionEvent event) {

        if(user.getText()==null || pass.getText()==null)
            return;

        Login login = new Login(user.getText(),pass.getText());

    }
}