package View;

import Controller.ConnectDB;
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
        ConnectDB db = new ConnectDB();
        ArrayList<OAuthCredential> credentialsList = db.getCredentials();

        Parent root;
        if (credentialsList != null){// Prepare mail for mailBox
            mailBoxInit(credentialsList,db);

            root = FXMLLoader.load(getClass().getResource("../View/GUI/fxml/mailBox.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } else {
            root = FXMLLoader.load(getClass().getResource("../View/GUI/fxml/login.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }
    }

    private void mailBoxInit(ArrayList<OAuthCredential> credentialsList, ConnectDB db){
        MailBoxController mailBoxController = new MailBoxController(credentialsList,db);
        mailBoxController.test_showAllMail();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
