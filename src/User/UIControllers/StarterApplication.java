package User.UIControllers;

import User.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StarterApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        if(getParameters().getRaw().size() != 2){
            System.out.println("error in syntax: java Main IPaddress port");
            return;
        }
        else {
            /*int numberOfArguments = getParameters().getRaw().size();
            System.out.println("Número de argumentos fornecidos: " + numberOfArguments);


            Parameters params = getParameters();
            System.out.println("Argumentos fornecidos: " + params.getRaw());*/

            Client.prepareClient(getParameters().getRaw().get(0), getParameters().getRaw().get(1));
            //Admin.prepareAdmin(getParameters().getRaw().get(0), getParameters().getRaw().get(1));

            FXMLLoader fxmlLoader = new FXMLLoader(StarterApplication.class.getResource("resources/Client/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);


            LoginController controller = fxmlLoader.getController();
            controller.setStage(stage);

            stage.setTitle("Sistema Registo de Presenças");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
