package User.UIControllers;

import javafx.event.ActionEvent;

public class BeginControllerAdmin {




    public void initializable(){
    }

    public void Events(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resources/Admin/consultaEventosCriados.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
