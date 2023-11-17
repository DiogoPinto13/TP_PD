package User.UIControllers;

import Shared.Event;
import Shared.EventResult;
import User.Client;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class PresentsController {
    @FXML
    public TableView tbPresenca;
    @FXML
    public TableColumn tbDesignacao;
    @FXML
    public TableColumn tbLocal;
    @FXML
    public TableColumn tbInicio;
    @FXML
    public TableColumn tbFim;

    private Stage stage;
    private Scene scene;
    private static Scene preScene;

    private ObservableList<Eventos> dataEventos;

    public void initialize() {


        EventResult eventResult = Client.getPresences(Client.getUsername());
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

        tbPresenca.setItems(dataEventos);

    }

    public void back(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resources/Client/beginClient.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public void ExportCSV(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("File save...");
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSVs (*.csv)","*.csv"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        File file = fileChooser.showSaveDialog(stage.getScene().getWindow());
        if(file != null){
            //model.exportToCSV();
            //User.Client.exportToCSVFile(file);
        }
    }

    /*public void writeExcel() throws Exception {
        Writer writer = null;
        try {
            File file = new File("C:\\Person.csv.");
            writer = new BufferedWriter(new FileWriter(file));
            for (Person person : data) {

                String text = person.getFirstName() + "," + person.getLastName() + "," + person.getEmail() + "\n";



                writer.write(text);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {

            writer.flush();
            writer.close();
        }
    }*/
}
