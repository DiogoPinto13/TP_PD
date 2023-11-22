package User.UIControllers;

import Shared.ErrorMessages;
import Shared.EventResult;
import User.Admin;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;


import java.util.ArrayList;

public class ConsultPresencesUserController {
    @FXML
    public Label nome;
    @FXML
    public Label local;
    @FXML
    public Label hInicio;
    @FXML
    public Label hFim;

    @FXML
    public TableColumn nome1;
    @FXML
    public TableColumn username;
    @FXML
    public TableColumn hRegisto;
    @FXML
    public TableView tbpresencas;

    private ObservableList<Eventos> dataPresences;


    String designacao;

    public ConsultPresencesUserController(String d){designacao = d;}

    public void initialize(){



        String a = Admin.GetInfoAboutEvent(designacao);
        if(a.equals(ErrorMessages.INVALID_EVENT_NAME))
            Platform.exit();
        String[] args = a.split(",");
        nome.setText(args[0]);
        local.setText(args[1]);
        hInicio.setText(args[2]);
        hFim.setText(args[3]);


        EventResult eventResult = Admin.getPresencesEvent(designacao);
        if(eventResult == null){
            eventResult = new EventResult(" ");
            eventResult.setColumns(" ");
            return;
        }
        String[] nomeColunas = eventResult.getColumns().split(",");

        ObservableList<String> observableList = FXCollections.observableArrayList(eventResult.events);
        //tbPresenca.setItems(observableList);
        //pede a lista das presenças e preenche a tabela
        ArrayList<String> eventos = eventResult.events;
        //int size = eventos.size();

        dataPresences = FXCollections.observableArrayList();

        nome1.setCellValueFactory(new PropertyValueFactory<>("designacao"));
        username.setCellValueFactory(new PropertyValueFactory<>("local"));
        hRegisto.setCellValueFactory(new PropertyValueFactory<>("horaInicio"));


        for(String evento : eventos){
            String[] eventoData = evento.split(",");
            Eventos event = new Eventos();
            event.setDesignacao(eventoData[1]);
            event.setLocal(eventoData[2]);
            event.setHoraInicio(eventoData[3]);
            dataPresences.add(event);
        }

        tbpresencas.setItems(dataPresences);



    }


    public void Voltar(ActionEvent event) {
    }

    public void exportarCSV(ActionEvent event) {
    }

    public void eliminarPresenca(ActionEvent event) {
    }

    public void inserirPresenca(ActionEvent event) {
    }
}
