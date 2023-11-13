package Client.UIControllers;

import Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class EditProfileController {
    @FXML
    private TextField nome;
    @FXML
    private PasswordField pass;
    @FXML
    private TextField email;
    @FXML
    private TextField nidentificacao;


    public void initialize() {
        String data = Client.getProfileData(Client.getUsername());
        String[] aData = data.split(",");

        nidentificacao.setText(aData[0]);
        email.setText(aData[1]);
        pass.setText(aData[2]);
        nome.setText(aData[3]);
    }


    public void sair(ActionEvent actionEvent) {


    }

    public void saveData(ActionEvent actionEvent) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(email.getText()).append(",");
        stringBuilder.append(nome.getText()).append(",");
        stringBuilder.append(nidentificacao.getText()).append(",");
        stringBuilder.append(pass.getText());
        System.out.println(stringBuilder.toString());
        Client.editProfile(stringBuilder.toString());

    }
}
