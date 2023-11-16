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

    public void initialize() {


        EventResult eventResult = Client.getPresences(Client.getUsername());
        String[] nomeColunas = eventResult.getColumns().split(",");

        //ObservableList<String> observableList = FXCollections.observableArrayList(eventResult.events);
        //tbPresenca.setItems(observableList);
        ObservableList<Eventos> dataEvents = FXCollections.observableArrayList();
        //pede a lista das presenças e preenche a tabela
        //ArrayList<String> eventos = eventResult.events;
        /*TableView<Eventos> tableView = new TableView<>();
        TableColumn<Eventos, String> desCol = new TableColumn<>("Designacao");
        TableColumn<Eventos, String> localCol = new TableColumn<>("Local");
        TableColumn<Eventos, String> hIni = new TableColumn<>("Hora Inicio");
        TableColumn<Eventos, String> hFim = new TableColumn<>("Hora Fim");
        tableView.getColumns().addAll(desCol, localCol, hIni, hFim);*/

        /*for(String evento : eventos){
            String[] eventoData = evento.split(",");
            Eventos event = new Eventos();
            event.setID(1);
            event.setDesignacao(eventoData[1]);
            event.setLocal(eventoData[2]);
            event.setHoraInicio(eventoData[3]);
            event.setHoraFim(eventoData[4]);
            dataEvents.add(event);
        }*/

        tbDesignacao.setCellValueFactory(new PropertyValueFactory<Eventos, String>("Designação"));
        tbLocal.setCellValueFactory(new PropertyValueFactory<Eventos, String>("Local"));
        tbInicio.setCellValueFactory(new PropertyValueFactory<Eventos, String>("Hora Início"));
        tbFim.setCellValueFactory(new PropertyValueFactory<Eventos, String>("Hora Fim"));

            Eventos event = new Eventos();
            //event.setID(1);
            event.setDesignacao("ba");
            event.setLocal("ba");
            event.setHoraInicio("ba");
            event.setHoraFim("fghj");

            dataEvents.add(event);


        tbPresenca.setItems(dataEvents);

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
