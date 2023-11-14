package Client.UIControllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BeginController {
    private Stage stage;
    private Scene scene;

    public void initialize() {}


    public void registCod(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resources/CodSend.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void editDados(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resources/editarDados.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void sair(ActionEvent event) {
        Platform.exit();
    }

    public void consultPresencas(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resources/consultaPresencas.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
