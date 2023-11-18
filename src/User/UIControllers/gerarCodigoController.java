package User.UIControllers;

import User.Admin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class gerarCodigoController {
    private Stage stage;
    private Scene scene;
    @FXML
    public ComboBox eventos;
    @FXML
    public TextField duracao;
    @FXML
    public Label code;


    public void initialize() {

        //getEvents

        ObservableList<String> values = FXCollections.observableArrayList("1, 2, 3, 4, 5", " ghjkl");
        eventos.setItems(values);

    }


    public void Voltar(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resources/Admin/beginAdmin.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void GerarCod(ActionEvent event) {

        eventos.getValue();
        int codigoObtido = Admin.generatePresenceCode(eventos.getValue().toString(), Integer.parseInt(duracao.getText()));
        if(codigoObtido != 0)
            code.setText(String.valueOf(codigoObtido));
        else
            code.setText("Erro ao gerar o código!");

    }
}
