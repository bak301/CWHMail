package MainApplication;

import Controller.ConnectDB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ConnectDB db = new ConnectDB();
        String file = (db.getCredentials() != null) ? "mailBox":"login";
        Parent root = FXMLLoader.load(getClass().getResource("../GUI/fxml/" + file + ".fxml"));
        primaryStage.setTitle("Welcome !");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
