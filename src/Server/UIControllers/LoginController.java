package Server.UIControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    public TextField user;
    @FXML
    public PasswordField pass;

    public void initialize() {

    }

    public void Login(ActionEvent event) {
        user.getText();
        pass.getText();


    }
}
