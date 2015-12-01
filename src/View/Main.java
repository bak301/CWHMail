package View;

import Controller.ConnectDB;
import Controller.Login.LoginController;
import Controller.MailBoxController;
import Model.OAuthCredential;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Welcome !");
        ConnectDB db = ConnectDB.defaultdb;
        ArrayList<OAuthCredential> credentialsList = db.getCredentials();

        FXMLLoader loader;
        Parent root;
        if (credentialsList != null){// Prepare mail for mailBox
            loader = new FXMLLoader(getClass().getResource("../View/GUI/fxml/mailBox.fxml"));

            MailBoxController controller = new MailBoxController();
            controller.setDb(db);
            controller.setCredentialsList(credentialsList);

            loader.setController(controller);
        } else {
            loader = new FXMLLoader(getClass().getResource("../View/GUI/fxml/login.fxml"));

            LoginController controller = new LoginController();
            controller.setDb(db);

            loader.setController(controller);
        }
        root = loader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
