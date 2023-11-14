package User.UIControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class PresentsController {
    @FXML
    //public TableView tbPresenca;

    private Stage stage;
    private Scene scene;

    public void initialize() {
        //pede a lista das presenças e preenche a tabela


    }

    public void back(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resources/Client/beginClient.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


}
