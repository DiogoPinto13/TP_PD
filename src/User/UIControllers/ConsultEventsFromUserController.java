package User.UIControllers;

import Shared.EventResult;
import User.Admin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ConsultEventsFromUserController {
    @FXML
    public Button pesquisa;

    @FXML
    public TableView tbEvento;
    @FXML
    public TableColumn tbDesignacao;
    @FXML
    public TableColumn tbLocal;
    @FXML
    public TableColumn tbInicio;

    private Stage stage;
    private Scene scene;
    private static Scene preScene;
    private ObservableList<Eventos> dataEventos;

    public void initialize(){

        Image imageDecline = new Image(getClass().getResourceAsStream("resources/lupa2.gif"));
        //Image imagem = new Image(getClass().getResourceAsStream("resources/lupa1.png")); // 1
        ImageView visualizadorImagem = new ImageView(imageDecline); // 2
        visualizadorImagem.setFitWidth(20); // 3
        visualizadorImagem.setFitHeight(15);
        pesquisa.setStyle("-fx-background-color: #ffff; -fx-border-color: gray; -fx-border-width: 1px; -fx-border-radius: 5px;");
        pesquisa.setGraphic(visualizadorImagem);


    }

    public void voltar(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resources/Admin/beginAdmin.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void pesquisa(ActionEvent actionEvent) {



        EventResult eventResult ;

        if (eventResult == null) {
            eventResult = new EventResult(" ");
            eventResult.setColumns(" ");
            return;
        }

        ArrayList<String> eventosNovos = eventResult.events;
        dataEventos = FXCollections.observableArrayList();

        tbDesignacao.setCellValueFactory(new PropertyValueFactory<>("designacao"));
        tbLocal.setCellValueFactory(new PropertyValueFactory<>("local"));
        tbInicio.setCellValueFactory(new PropertyValueFactory<>("horaInicio"));

        for (String evento : eventosNovos) {
            String[] eventoData = evento.split(",");
            Eventos event = new Eventos();

            event.setDesignacao(eventoData[1]);
            event.setLocal(eventoData[2]);
            event.setHoraInicio(eventoData[3]);
            event.setHoraFim(eventoData[4]);
            dataEventos.add(event);
        }

        tbEvento.setItems(dataEventos);


    }
}
