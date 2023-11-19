package User;

import User.UIControllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("error in syntax: java Main IPaddress port");
            return;
        }
        Client.setAddress(args[0]);
        Client.setPort(Integer.parseInt(args[1]));
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        /*int numberOfArguments = getParameters().getRaw().size();
        System.out.println("Número de argumentos fornecidos: " + numberOfArguments);


        Parameters params = getParameters();
        System.out.println("Argumentos fornecidos: " + params.getRaw());*/
        //Client.prepareClient(getParameters().getRaw().get(0), getParameters().getRaw().get(1));
        //Admin.prepareAdmin(getParameters().getRaw().get(0), getParameters().getRaw().get(1));

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("UIControllers/resources/Client/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);


        LoginController controller = fxmlLoader.getController();
        controller.setStage(stage);

        stage.setTitle("Sistema Registo de Presenças");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}