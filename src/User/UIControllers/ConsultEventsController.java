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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.TableView;
import java.io.*;
import java.util.ArrayList;

public class ConsultEventsController {
    private Stage stage;
    private Scene scene;
    @FXML
    public VBox consultEvents;
    @FXML
    public TableView tbEvento;
    @FXML
    public TableColumn tbDesignacao;
    @FXML
    public TableColumn tbLocal;
    @FXML
    public TableColumn tbInicio;
    @FXML
    public TableColumn tbFim;

    private ObservableList<Eventos> dataEventos;

    public void initialize(){
        EventResult eventResult = Admin.getEvents(Admin.getUsername());
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

        dataEventos = FXCollections.observableArrayList();

        tbDesignacao.setCellValueFactory(new PropertyValueFactory<>("designacao"));
        tbLocal.setCellValueFactory(new PropertyValueFactory<>("local"));
        tbInicio.setCellValueFactory(new PropertyValueFactory<>("horaInicio"));
        tbFim.setCellValueFactory(new PropertyValueFactory<>("horafim"));


        for(String evento : eventos){
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

    public void ConsultPresentsEvent(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resources/Admin/consultaPresençasEvento.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void Edit(ActionEvent actionEvent) throws IOException {
        int i = tbEvento.getSelectionModel().getSelectedIndex();
        Eventos eventos = (Eventos) tbEvento.getItems().get(i);

        eventos.getDesignacao();
    }

    public void eliminate(ActionEvent actionEvent) {
    }

    public void createEvent(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resources/Admin/criarEvento.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void pesquisa(ActionEvent actionEvent) {
    }

    public void voltar(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resources/Admin/beginAdmin.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void exportCSV(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("File save...");
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSVs (*.csv)","*.csv"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        File file = fileChooser.showSaveDialog(consultEvents.getScene().getWindow());
        if(file != null){
            try {
                exportData(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void exportData(File file) throws IOException {
        Writer writer = null;
        try {
            //File file = new File("data.csv");
            writer = new BufferedWriter(new FileWriter(file));

            for (Eventos event : dataEventos) {
                String text = event.getDesignacao() + "," + event.getLocal() + "," + event.getHoraInicio() + "," + event.getHorafim() + "\n";
                writer.write(text);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            writer.flush();
            writer.close();
        }
    }
}
