package User.UIControllers;

import Shared.Time;
import User.Admin;
import User.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class criarEventoController {

    @FXML
    public DatePicker data;
    @FXML
    public Spinner<Integer> horaInicio;
    @FXML
    public Spinner<Integer> minutosInicio;
    @FXML
    public Spinner<Integer> horaFim;
    @FXML
    public Spinner<Integer> minutosFim;

    @FXML
    public TextField local;
    @FXML
    public TextField nome;

    private Stage stage;
    private Scene scene;

    public void initialize() {

    }

    public void createEvent(ActionEvent event) throws IOException {
        LocalDate dataSelecionada = data.getValue();
        int dia = dataSelecionada.getDayOfMonth();
        int mes = dataSelecionada.getMonthValue();
        int ano = dataSelecionada.getYear();


        Admin.createEvent(nome.getText(), local.getText(), new Time(dia,mes,ano, horaInicio.getValue(), minutosInicio.getValue()), new Time(dia,mes,ano, horaFim.getValue(), minutosFim.getValue()) );


    }

    public void voltar(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resources/Admin/beginAdmin.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
